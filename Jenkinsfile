pipeline {
    agent any

    environment {
        FRONT_IMAGE = "okamitah/aeger-hub-deploy-frontend"
        BACK_IMAGE  = "okamitah/aeger-hub-deploy-backend"
        
        DEPLOY_USER = "toto"
        FRONT_IP    = "172.31.253.126"
        BACK_IP     = "172.31.253.155"
        DB_IP       = "172.31.252.33"
    }

    stages {
        stage('Clean Workspace') {
            steps {
                deleteDir()
            }
        }

        stage('Clone') {
            steps {
                checkout([$class: 'GitSCM',
                    branches: [[name: "*/main"]],
                    doClean: true,
                    extensions: [
                        [$class: 'CloneOption', depth: 0, noTags: false, reference: ''],
                        [$class: 'PruneStaleBranch']
                    ],
                    userRemoteConfigs: [[
                        credentialsId: 'github-creds',
                        url: 'https://github.com/Okamitah/aeger_hub'
                    ]]
                ])
            }
        }

        stage('Build Frontend Assets') {
            agent {
                docker {
                    image 'node:20.19.0'
                    args '-e HOME=/tmp'
                }
            }
            steps {
                dir('front') {
                    sh 'rm -rf dist'
                    sh 'npm ci'
                    sh 'export NODE_OPTIONS="--max_old_space_size=1024" && npm run build'
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
                    reuseNode true
                }
            }
            steps {
                dir('back') {
                    sh "mvn clean package -U"
                }
            }
        }

        stage('Build Backend Docker Image') {
            steps {
                sh 'docker build --no-cache -t $BACK_IMAGE:latest back/'
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

    stage('Deploy Database') {
        steps {
            withCredentials([string(credentialsId: 'prod-server-password', variable: 'SSH_PASS')]) {
                sh """
                    sshpass -p '${SSH_PASS}' ssh -o StrictHostKeyChecking=no ${env.DEPLOY_USER}@${env.DB_IP} '
                    docker stop postgres-db 2>/dev/null || true
                    docker rm postgres-db 2>/dev/null || true

                    docker run -d --name postgres-db -p 5432:5432 \\
                    -v /home/toto/aeger_db_data:/var/lib/postgresql/data \\
                    -e POSTGRES_DB=aeger_hub_db \\
                    -e POSTGRES_USER=aeger \\
                    -e POSTGRES_PASSWORD=aeger \\
                    postgres:15
                    '
                    """
            }
        }
    }

    stage('Deploy Backend') {
        steps {
            withCredentials([string(credentialsId: 'prod-server-password', variable: 'SSH_PASS')]) {
                sh """
                    sshpass -p '${SSH_PASS}' ssh -o StrictHostKeyChecking=no ${env.DEPLOY_USER}@${env.BACK_IP} '
                    docker pull $BACK_IMAGE:latest

                    docker stop backend 2>/dev/null || true
                    docker rm backend 2>/dev/null || true

                    docker run -d --name backend -p 8080:8080 \\
                    -e SPRING_DATASOURCE_URL=jdbc:postgresql://${env.DB_IP}:5432/aeger_hub_db \\
                    -e SPRING_DATASOURCE_USERNAME=aeger \\
                    -e SPRING_DATASOURCE_PASSWORD=aeger \\
                    $BACK_IMAGE:latest
                    '
                    """
            }
        }
    }

    stage('Deploy Frontend') {
        steps {
            withCredentials([string(credentialsId: 'prod-server-password', variable: 'SSH_PASS')]) {
                sh """
                    sshpass -p '${SSH_PASS}' ssh -o StrictHostKeyChecking=no ${env.DEPLOY_USER}@${env.FRONT_IP} '
                    docker pull $FRONT_IMAGE:latest

                    docker stop frontend 2>/dev/null || true
                    docker rm frontend 2>/dev/null || true

                    docker run -d --name frontend -p 80:80 \\
                    $FRONT_IMAGE:latest
                    '
                    """
            }
        }
    }
}
