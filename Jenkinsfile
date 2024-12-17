pipeline {
    agent any

    tools {
        gradle 'Gradle'
    }

    environment {
        DOCKER_IMAGE = 'banking-system'
        DOCKER_TAG = "${BUILD_NUMBER}"
        SONAR_TOKEN = credentials('sonar-token')
        GRADLE_OPTS = '-Dorg.gradle.daemon=false'
    }

    stages {
        stage('Checkout') {
            steps {
                cleanWs()
                checkout scm: [
                    $class: 'GitSCM',
                    branches: [[name: '*/v04']], // Changed to v04 branch
                    userRemoteConfigs: [[
                        url: 'https://github.com/kobecode24/eBankify-SpringBoot-API',
                        credentialsId: 'git-credentials' // if you need credentials
                    ]]
                ]
            }
        }

        stage('Environment Check') {
            steps {
                sh '''
                    echo "Git version:"
                    git --version
                    echo "Java version:"
                    java -version
                    echo "Javac version:"
                    javac -version
                    echo "Working directory:"
                    pwd
                    ls -la
                '''
            }
        }

        stage('Build') {
            steps {
                sh '''
                    chmod +x ./gradlew
                    ./gradlew clean build -x test --info --stacktrace
                '''
            }
        }

        stage('Unit Tests') {
            steps {
                sh './gradlew test --info'
            }
            post {
                always {
                    junit '**/build/test-results/test/*.xml'
                    jacoco(
                        execPattern: '**/build/jacoco/*.exec',
                        classPattern: '**/build/classes/java/main',
                        sourcePattern: '**/src/main/java'
                    )
                }
            }
        }

        stage('Code Quality Analysis') {
            steps {
                sh '''
                    ./gradlew sonar \
                        -Dsonar.projectKey=banking-system \
                        -Dsonar.projectName=BankingSystem \
                        -Dsonar.host.url=http://sonarqube:9000 \
                        -Dsonar.login=${SONAR_TOKEN}
                '''
            }
        }

        stage('Build Docker Image') {
            steps {
                script {
                    docker.build("${DOCKER_IMAGE}:${DOCKER_TAG}")
                    docker.build("${DOCKER_IMAGE}:latest")
                }
            }
        }

        stage('Manual Approval') {
            steps {
                timeout(time: 5, unit: 'MINUTES') {
                    input message: 'Deploy to production?', ok: 'Proceed'
                }
            }
        }

        stage('Deploy') {
            steps {
                script {
                    docker.image("${DOCKER_IMAGE}:${DOCKER_TAG}").run('-p 8080:8080')
                }
            }
        }
    }

    post {
        always {
            cleanWs()
        }
        success {
            echo 'Pipeline executed successfully!'
        }
        failure {
            echo 'Pipeline execution failed!'
        }
    }
}
