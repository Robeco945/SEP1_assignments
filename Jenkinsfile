pipeline {
    agent any

    tools {
        jdk 'Java21'
        maven 'Maven3'
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
        stage('Deploy to Docker Hub') {
            steps {
                script {
                    docker.withRegistry('https://index.docker.io/v1/', 'docker-hub-credentials') {
                        echo 'Building Docker Image...'
                        sh 'docker build -t robeco945/shopping-cart .'
                        
                        echo 'Pushing Image to Docker Hub...'
                        sh 'docker push robeco945/shopping-cart'
                    }
                }
            }
        }
    }
}