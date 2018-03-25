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
        stage('Deploy API') {
            when { branch 'master' }
            environment {
                DOCKER_USER = credentials('DOCKER_USER')                
                DOCKER_PASSWORD = credentials('DOCKER_PASSWORD')
            }
            steps {
                dir('api') {
                    sh '/bin/bash ./deploy.sh core core-api core-api'
                }
            }
        }
    }
}