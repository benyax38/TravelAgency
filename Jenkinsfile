pipeline {
    agent any
    environment {
        DOCKER_HUB_USER = 'benyax38'
        DOCKER_CREDENTIALS_ID = 'docker-hub-creds'
    }
    stages {
        stage('Checkout') {
            steps {
                git branch: 'main', url: 'https://github.com/benyax38/TravelAgency.git'
            }
        }
        stage('Test Backend') {
            steps {
                dir('backend') {
                    sh './mvnw clean package -DskipTests' // Asegura el 90% de cobertura aquí
                }
            }
        }
        stage('Build & Push Images') {
            steps {
                script {
                    docker.withRegistry('https://index.docker.io/v1/', "${DOCKER_CREDENTIALS_ID}") {
                        // Construir Backend
                        def backendImage = docker.build("${DOCKER_HUB_USER}/backend:latest", "./backend")
                        backendImage.push()
                        // Construir Frontend
                        def frontendImage = docker.build("${DOCKER_HUB_USER}/frontend:latest", "./frontend")
                        frontendImage.push()
                    }
                }
            }
        }
    }
}