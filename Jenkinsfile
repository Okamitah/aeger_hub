pipeline {
    agent any

    stages {
        stage('Clone') {
            steps {
                checkout scm
            }
        }

        stage('Build Frontend') {
            agent {
                docker {
                    image 'node:20.18.0-alpine'
                    args '-v $HOME/.npm:/root/.npm'
                }
            }
            steps {
                sh 'npm ci --omit=dev'
                sh 'npm run build'
                stash name: 'frontend-dist', includes: 'dist/**'
            }
        }

        stage('Build Backend') {
            agent {
                docker {
                    image 'maven:3.9.6-eclipse-temurin-17'
                    args '-v $HOME/.m2:/root/.m2'
                }
            }
            steps {
                sh 'mvn clean package -DskipTests'
                stash name: 'backend-jar', includes: 'target/*.jar'
            }
        }

        stage('Deploy') {
            steps {
                unstash 'frontend-dist'
                unstash 'backend-jar'

                sh 'mkdir -p frontend/dist backend/target'
                sh 'cp -r dist/* frontend/dist/'
                sh 'cp target/*.jar backend/target/'

                sh 'docker-compose down --remove-orphans || true'
                sh 'docker-compose up -d'
            }
        }
    }
}
