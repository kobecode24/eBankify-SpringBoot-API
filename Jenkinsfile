pipeline {
    agent any

    tools {
        gradle 'Gradle'
        jdk 'JDK 17'
    }

    stages {
        stage('Build') {
            steps {
                script {
                    if (isUnix()) {
                        sh 'chmod +x gradlew'
                        sh './gradlew clean build -x test'
                    } else {
                        bat 'gradlew clean build -x test'
                    }
                }
            }
        }

        stage('Test') {
            steps {
                script {
                    if (isUnix()) {
                        sh './gradlew test'
                    } else {
                        bat 'gradlew test'
                    }
                }
            }
            post {
                always {
                    junit '**/build/test-results/test/*.xml'
                }
            }
        }
    }

    post {
        always {
            node('built-in') {
                cleanWs()
            }
        }
        success {
            echo 'Pipeline executed successfully!'
        }
        failure {
            echo 'Pipeline execution failed!'
        }
    }
}