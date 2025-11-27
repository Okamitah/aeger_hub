pipeline {
    agent any

    stages {
        stage('Clone') {
            steps {
                checkout scm
            }
        }

        stage('Build Frontend') {
            steps {
                dir('frontend') {
                    sh 'npm ci --omit=dev'
                    sh 'npm run build'
                }
            }
        }

        stage('Build Backend') {
            steps {
                dir('backend') {
                    sh 'mvn clean package -DskipTests'
                }
            }
        }

        stage('Deploy') {
            steps {
                sh 'echo "Stopping old containers..."'
                sh 'docker-compose down --remove-orphans || true'
                sh 'echo "Starting new containers..."'
                sh 'docker-compose up -d --build'
                sh 'echo "Deployment complete!"'
            }
        }
    }
}
