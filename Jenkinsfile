pipeline {
    agent any
    environment {
        CLIENT_ID = credentials('CLIENT_ID')
        DOCKER_USER = credentials('DOCKER_USER')
    }
    stages {
        stage('Test') {
            environment {
                SQL_HOST = credentials('TEST_SQL_HOST')
                SQL_DATABASE = credentials('TEST_SQL_DATABASE')
                SQL_USER = credentials('TEST_SQL_USER')
                SQL_PASSWORD = credentials('TEST_SQL_PASSWORD')
            }
            steps {
                dir('api') {
                    sh 'docker build -f Dockerfile.test -t ng_core_test .'
                    sh '''
                        docker run \
                            --rm \
                            -e SQL_HOST \
                            -e SQL_DATABASE \
                            -e SQL_USER \
                            -e SQL_PASSWORD \
                            -e CLIENT_ID \
                            ng_core_test
                    '''
                }
            }
        }
        stage('Build Deployment Images') {
            environment {
                DOCKER_PASSWORD = credentials('DOCKER_PASSWORD')
            }
            steps {
                dir('api') {
                    sh 'docker login -u ${DOCKER_USER} -p ${DOCKER_PASSWORD}'
                    sh 'docker build -t ${DOCKER_USER}/core .'
                    sh 'docker push ${DOCKER_USER}/core'
                }
            }
        }
        stage('Stage') {
            environment {
                SQL_HOST = credentials('STAGE_SQL_HOST')
                SQL_DATABASE = credentials('STAGE_SQL_DATABASE')
                SQL_USER = credentials('STAGE_SQL_USER')
                SQL_PASSWORD = credentials('STAGE_SQL_PASSWORD')                
            }
            steps {
                dir('api') {
                    sh 'docker rm -f ng_core_stage || true'
                    sh '''
                        docker run \
                            --rm -d \
                            --name ng_core_stage \
                            --net="host" \
                            -e NODE_ENV=test \
                            -e SQL_HOST \
                            -e SQL_DATABASE \
                            -e SQL_USER \
                            -e SQL_PASSWORD \
                            -e CLIENT_ID \
                            ${DOCKER_USER}/core
                    '''
                }
            }
        }
        stage('Deploy') {
            environment {
                DEPLOY_USER = credentials('DEPLOY_USER')
                DEPLOY_SRV = credentials('DEPLOY_SRV')
            }
            steps {
                dir('api') {
                    sh '/bin/bash ./deploy.sh'
                }
            }
        }
    }
}