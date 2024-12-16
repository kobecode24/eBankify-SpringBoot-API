pipeline {
    agent any

    tools {
        gradle 'Gradle'
        jdk 'JDK 17'
    }

    environment {
        JAVA_HOME = '/opt/jdk-17.0.12+7'
        PATH = "${env.JAVA_HOME}/bin:${env.PATH}"
        DOCKER_IMAGE = 'banking-system'
        DOCKER_TAG = "${BUILD_NUMBER}"
        SONAR_TOKEN = credentials('sonar-token')
    }

    stages {
        stage('Code Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Build') {
            steps {
                script {
                    if (isUnix()) {
                        sh '/opt/gradle-8.10.2/bin/gradle clean build -x test'
                    } else {
                        bat 'C:/gradle-8.10.2/bin/gradle clean build -x test'
                    }
                }
            }
        }

        stage('Unit Tests') {
            steps {
                script {
                    if (isUnix()) {
                        sh '/opt/gradle-8.10.2/bin/gradle test'
                    } else {
                        bat 'C:/gradle-8.10.2/bin/gradle test'
                    }
                }
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
                script {
                    if (isUnix()) {
                        sh "/opt/gradle-8.10.2/bin/gradle sonar -Dsonar.host.url=http://localhost:9000 -Dsonar.login=${SONAR_TOKEN}"
                    } else {
                        bat "C:/gradle-8.10.2/bin/gradle sonar -Dsonar.host.url=http://localhost:9000 -Dsonar.login=${SONAR_TOKEN}"
                    }
                }
            }
        }

        stage('Build Docker Image') {
            steps {
                script {
                    docker.build("${DOCKER_IMAGE}:${DOCKER_TAG}")
                }
            }
        }

        stage('Manual Approval') {
            steps {
                input message: 'Deploy to production?'
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
