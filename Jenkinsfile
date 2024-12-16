pipeline {
    agent any

    tools {
        gradle 'Gradle'
        jdk 'JDK 17'
    }

    environment {
        JAVA_HOME = '/opt/jdk-17.0.12+7'
        PATH = "${env.JAVA_HOME}/bin:${env.PATH}"
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

        stage('Test') {
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
