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
        stage('SCM'){
            steps{
                checkout scm: [[$class: 'GitSCM', source: 'https://github.com/Nexpeque/cicdworkshop.git', clean: true], poll: false
            }
        }
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
                echo 'Building app'
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
                    withAWS(credentials: 'aws-credentials', region: 'us-east-1') {
                        sh "aws ecr get-login-password --region us-east-1 | docker login --username AWS --password-stdin $ECR_REGISTRY"
                        def imageWithLatest = "$ECR_REGISTRY/$REPO:latest"
                        def imageWithTag = "$ECR_REGISTRY/$REPO:$BUILD_NUMBER"+"_"+"$date"
                        if (GIT_BRANCH == "main"){
                            imageWithTag = "$ECR_REGISTRY/$REPO:$BUILD_NUMBER"+"_"+"$date"+"_"+"$GIT_BRANCH"
                        }
                        def image = docker.build imageWithTag
                        def latest = docker.build imageWithLatest
                        // push image
                        image.push()
                        latest.push()
                    }
                }
            }
        }
    }
}

