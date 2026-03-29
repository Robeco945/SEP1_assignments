pipeline {
    agent any

    tools {
        jdk 'Java21'
        maven 'Maven3'
    }

    environment {
        IMAGE_NAME = 'robeco945/shopping-cart-localized'
        IMAGE_TAG = "${env.BUILD_NUMBER}"
    }

    stages {
        stage('Build') {
            steps {
                echo 'Building the project...'
                sh 'mvn clean compile'
            }
        }
        stage('Test') {
            steps {
                echo 'Running unit tests and checking coverage...'
                sh 'mvn verify'
            }
        }

        stage('Package') {
            steps {
                echo 'Packaging JAR artifact...'
                sh 'mvn -DskipTests package'
            }
        }

        stage('Build Docker Image') {
            steps {
                echo 'Building Docker image...'
                sh 'docker build -t $IMAGE_NAME:$IMAGE_TAG -t $IMAGE_NAME:latest .'
            }
        }

        stage('Push Docker Image') {
            steps {
                script {
                    docker.withRegistry('https://index.docker.io/v1/', 'docker-hub-credentials') {
                        echo 'Pushing Docker image to Docker Hub...'
                        sh 'docker push $IMAGE_NAME:$IMAGE_TAG'
                        sh 'docker push $IMAGE_NAME:latest'
                    }
                }
            }
        }
    }
}