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
                    image 'node:20.19.0'
		    args '-e HOME=/tmp'
                }
            }
            steps {
                dir('front') {
                    sh 'npm ci --omit=dev'
                    sh 'npm run build'
                }
                stash name: 'frontend-dist', includes: 'front/dist/**'
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
                dir('back') {
                    sh 'mvn clean package -DskipTests'
                }
                stash name: 'backend-jar', includes: 'back/target/*.jar'
            }
        }

        stage('Deploy') {
            steps {
                unstash 'frontend-dist'
                unstash 'backend-jar'

                sh '''
                    mkdir -p front/dist back/target
                    cp -r front/dist/* front/dist/ 2>/dev/null || true
                    cp back/target/*.jar back/target/ 2>/dev/null || true
                '''

                sh 'docker-compose down --remove-orphans || true'
                sh 'docker-compose up -d --build'
            }
        }
    }
}
