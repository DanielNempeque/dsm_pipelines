def image_name = ''
def date = ''
pipeline {
    agent any
    options {
        ansiColor('xterm')
    }
    environment {
        ECR_REGISTRY = '436054236749.dkr.ecr.us-east-1.amazonaws.com'
        REPO = "cicdworkshop"
        CI = 'true'
    }
    stages {
        stage('Execute tests'){
            steps{
                echo 'executing tests'
                nodejs('Node-16.0'){
                    sh 'npm install'
                    sh 'npm run test -- --coverage'
                }
            }
        }
        stage('Build app'){
            steps{
                echo 'executing tests'
                nodejs('Node-16.0'){
                    sh 'npm run build'
                }
            }
        }
        stage('Build artifact') {
            steps {
                script {
                    sh 'docker --version'
                    def now = new Date()

                    date = now.format("yyMMdd", TimeZone.getTimeZone('CST'))
                    withCredentials([usernamePassword(credentialsId: 'AWSCreds', usernameVariable:'AWS_ACCESS_KEY_ID', passwordVariable: 'AWS_SECRET_ACCESS_KEY')]) {
                        sh "aws ecr get-login-password --region us-east-1 | docker login --username AWS --password-stdin $env.ECR_REGISTRY"
                        def imageWithTag = "$env.ECR_REGISTRY/$env.REPO:$env.BRANCH_NAME"+"_"+"$env.BUILD_NUMBER"+"_"+"$date"
                        def image = docker.build imageWithTag
                        // push image
                        image.push()
                    }
                }
            }
        }
    }
}

