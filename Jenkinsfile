pipeline {
    agent any
    stages {
        stage('Test API') {
            steps {
                dir('api') {
                    sh '''
                        docker-compose \
                            -f docker-compose.test.yaml \
                            up \
                            --build --abort-on-container-exit
                    '''
                    sh 'docker-compose -f docker-compose.test.yaml down -v'
                }
            }
        }
        stage('Build Docker Images') {
            when { branch 'master' }
            environment {
                DOCKER_USER = credentials('DOCKER_USER')
                DOCKER_PASSWORD = credentials('DOCKER_PASSWORD')
            }
            steps {
                dir('api') {
                    sh 'docker login -u ${DOCKER_USER} -p ${DOCKER_PASSWORD}'
                    sh '''docker build -t ${DOCKER_USER}/core:$(sed -rn 's/^.*"version": "(.*)",$/\1/p' package.json) .'''
                    sh '''docker push ${DOCKER_USER}/core:$(sed -rn 's/^.*"version": "(.*)",$/\1/p' package.json)'''
                }
            }
        }
        stage('Deploy') {
            when { branch 'master' }
            steps {
                dir('api') {
                    sh '''kubectl set image deployment/core-api core-api:${DOCKER_USER}/core:$(sed -rn 's/^.*"version": "(.*)",$/\1/p' package.json)'''
                }
            }
        }
    }
}