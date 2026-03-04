pipeline {
    agent any

    parameters {
        choice(
            name: 'ENVIRONMENT',
            choices: ['integration', 'test', 'prod'],
            description: 'Select deployment environment'
        )
        choice(
            name: 'MAVEN_PROFILE',
            choices: ['integration', 'test', 'prod'],
            description: 'Maven build profile'
        )
    }

    environment {
        FRONT_IMAGE = "okamitah/aeger-hub-deploy-frontend"
        BACK_IMAGE  = "okamitah/aeger-hub-deploy-backend"
        
        INTEGRATION_USER = "toto"
        INTEGRATION_IP   = "172.31.250.95"

        PROD_USER     = "toto"
        PROD_FRONT_IP = "172.31.253.126"
        PROD_BACK_IP  = "172.31.253.155"
        PROD_DB_IP    = "172.31.252.33"
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
                    sh 'docker build --no-cache -t $FRONT_IMAGE:${ENVIRONMENT} -t $FRONT_IMAGE:latest .'
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
                    sh "mvn clean package -P${MAVEN_PROFILE} -U"
                }
            }
        }

        stage('Build Backend Docker Image') {
            steps {
                sh 'docker build --no-cache -t $BACK_IMAGE:${ENVIRONMENT} -t $BACK_IMAGE:latest back/'
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
                    sh 'docker push $FRONT_IMAGE:${ENVIRONMENT}'
                    sh 'docker push $FRONT_IMAGE:latest'
                    sh 'docker push $BACK_IMAGE:${ENVIRONMENT}'
                    sh 'docker push $BACK_IMAGE:latest'
                }
            }
        }

        stage('Deploy') {
            steps {
                script {
                    sshagent(['integration-server-key']) {
                        if (params.ENVIRONMENT == 'integration') {
                            def dbPassword = 'aeger'

                            sh """
                                ssh -o StrictHostKeyChecking=no ${env.INTEGRATION_USER}@${env.INTEGRATION_IP} '
                                    docker network create aeger-net 2>/dev/null || true

                                    docker pull $BACK_IMAGE:${ENVIRONMENT}
                                    docker pull $FRONT_IMAGE:${ENVIRONMENT}

                                    docker stop postgres-db backend frontend 2>/dev/null || true
                                    docker rm postgres-db backend frontend 2>/dev/null || true

                                    docker run -d --name postgres-db --network aeger-net \\
                                        -v /home/${env.INTEGRATION_USER}/aeger_db_data:/var/lib/postgresql/data \\
                                        -e POSTGRES_DB=aeger_hub_db \\
                                        -e POSTGRES_USER=aeger \\
                                        -e POSTGRES_PASSWORD=${dbPassword} \\
                                        postgres:15

                                    sleep 10

                                    docker run -d --name backend --network aeger-net -p 8080:8080 \\
                                        -e SPRING_PROFILES_ACTIVE=${MAVEN_PROFILE} \\
                                        -e SPRING_DATASOURCE_URL=jdbc:postgresql://postgres-db:5432/aeger_hub_db \\
                                        -e SPRING_DATASOURCE_USERNAME=aeger \\
                                        -e SPRING_DATASOURCE_PASSWORD=${dbPassword} \\
                                        $BACK_IMAGE:${ENVIRONMENT}

                                    docker run -d --name frontend --network aeger-net -p 80:80 \\
                                        --add-host=host.docker.internal:host-gateway \\
                                        $FRONT_IMAGE:${ENVIRONMENT}
                                '
                            """

                        } else if (params.ENVIRONMENT == 'prod') {
                            def dbPassword = credentials('prod-db-password')

                            sh """
                                ssh -o StrictHostKeyChecking=no ${env.PROD_USER}@${env.PROD_DB_IP} '
                                    docker stop postgres-db 2>/dev/null || true
                                    docker rm postgres-db 2>/dev/null || true

                                    docker run -d --name postgres-db -p 5432:5432 \\
                                        -v /home/${env.PROD_USER}/aeger_db_data:/var/lib/postgresql/data \\
                                        -e POSTGRES_DB=aeger_hub_db \\
                                        -e POSTGRES_USER=aeger \\
                                        -e POSTGRES_PASSWORD=${dbPassword} \\
                                        postgres:15
                                '
                            """

                            sh """
                                ssh -o StrictHostKeyChecking=no ${env.PROD_USER}@${env.PROD_BACK_IP} '
                                    docker pull $BACK_IMAGE:${ENVIRONMENT}

                                    docker stop backend 2>/dev/null || true
                                    docker rm backend 2>/dev/null || true

                                    docker run -d --name backend -p 8080:8080 \\
                                        -e SPRING_PROFILES_ACTIVE=${MAVEN_PROFILE} \\
                                        -e SPRING_DATASOURCE_URL=jdbc:postgresql://${env.PROD_DB_IP}:5432/aeger_hub_db \\
                                        -e SPRING_DATASOURCE_USERNAME=aeger \\
                                        -e SPRING_DATASOURCE_PASSWORD=${dbPassword} \\
                                        $BACK_IMAGE:${ENVIRONMENT}
                                '
                            """

                            sh """
                                ssh -o StrictHostKeyChecking=no ${env.PROD_USER}@${env.PROD_FRONT_IP} '
                                    docker pull $FRONT_IMAGE:${ENVIRONMENT}

                                    docker stop frontend 2>/dev/null || true
                                    docker rm frontend 2>/dev/null || true

                                    docker run -d --name frontend -p 80:80 \\
                                        $FRONT_IMAGE:${ENVIRONMENT}
                                '
                            """
                        }
                    }
                }
            }
        }
    }
}
