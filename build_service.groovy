def image_name = ''
def date = ''
pipeline {
    agent any
    environment {
        ACR_REGISTRY = 'cicdworkshop.azurecr.io'
        ACR_RES_GROUP = 'ci-cd-workshop'
        ACR_NAME = 'cicdworkshop'
        CI = 'true'
    }
    stages {
        stage('Execute tests'){
            steps{
                echo 'executing tests'
                nodejs('Node-16.0'){
                    sh 'npm install'
                    sh 'npm run test --detectOpenHandles'
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
                    image_name = "$env.BRANCH_NAME"
                    date = now.format("yyMMdd", TimeZone.getTimeZone('CST'))
                    withCredentials([usernamePassword(credentialsId: 'AzureACR', usernameVariable:'ACR_USER', passwordVariable: 'ACR_PASSWORD')]) {
                        sh 'docker login -u $ACR_USER -p $ACR_PASSWORD $ACR_REGISTRY'
                        def imageWithTag = "$env.ACR_REGISTRY/$image_name:$env.BUILD_NUMBER"+"_"+"$date"
                        def image = docker.build imageWithTag
                        // push image
                        image.push()
                    }
                }
            } 
        }
    }
}

