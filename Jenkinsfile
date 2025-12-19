pipeline {
    agent any

    environment {
        FRONT_IMAGE = "okamitah/aeger-hub-deploy-frontend"
        BACK_IMAGE  = "okamitah/aeger-hub-deploy-backend"
        INTEGRATION_USER = "toto"
        INTEGRATION_IP = "172.31.249.107"
    }

    stages {
        stage('Clean Workspace') {
            steps {
                sh 'docker system prune -af || true'
            }
        }

        stage('Clone') {
            steps {
                deleteDir()
                checkout([$class: 'GitSCM',
                    branches: [[name: "*/main"]],
                    userRemoteConfigs: [[
                        credentialsId: 'github-creds',
                        url: 'https://github.com/Okamitah/aeger_hub'
                    ]]
                ])
            }
        }

        stage('Build Frontend Assets') {
            steps {
                dir('front') {
                    sh 'rm -rf dist'
                    sh 'npm ci --prefer-offline --no-audit --progress=false'
                    sh 'npm run build'
                }
            }
        }

        stage('Debug: Check dist content') {
            steps {
                dir('front') {
                    sh 'echo "=== dist/ files ==="'
                    sh 'ls -la dist/'
                    sh 'echo "=== index.html head ==="'
                    sh 'head -n 5 dist/index.html'
                    sh 'echo "=== Check for HelloWorld ==="'
                    sh 'grep -q "HelloWorld" dist/index.html && echo "✅ Found HelloWorld" || echo "❌ HelloWorld NOT found!"'
                }
            }
        }

        stage('Build Frontend Docker Image') {
            steps {
                dir('front') {
                    sh 'docker build --no-cache -t $FRONT_IMAGE:latest .'
                }
            }
        }

        stage('Build Backend JAR') {
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
            }
        }

        stage('Build Backend Docker Image') {
            steps {
                sh 'docker build -t $BACK_IMAGE:latest back/'
            }
        }

        stage('Push Images to DockerHub') {
            steps {
                withCredentials([usernamePassword(
                    credentialsId: 'dockerhub-credentials-id',
                    usernameVariable: 'DOCKER_USER',
                    passwordVariable: 'DOCKER_PASS'
                )]) {
                    sh 'echo "$DOCKER_PASS" | docker login -u "$DOCKER_USER" --password-stdin'
                    sh 'docker push $FRONT_IMAGE:latest'
                    sh 'docker push $BACK_IMAGE:latest'
                }
            }
        }

        stage('Deploy to Integration VM') {
            steps {
                sshagent(['integration-server-key']) {
                    sh """
                        ssh -o StrictHostKeyChecking=no $INTEGRATION_USER@$INTEGRATION_IP '
                            docker pull $FRONT_IMAGE:latest
                            docker pull $BACK_IMAGE:latest

                            docker stop frontend || true
                            docker rm frontend || true
                            docker stop backend || true
                            docker rm backend || true

                            docker run -d --name backend -p 8080:8080 $BACK_IMAGE:latest

                            docker run -d --name frontend -p 80:80 \
                                -e API_URL=http://$INTEGRATION_IP:8080 \
                                $FRONT_IMAGE:latest
                        '
                    """
                }
            }
        }
    }
}
