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
        stage('Build artifact') {
            steps {
                script {
                    def now = new Date()
                    def image_name = ''
                    image_name = "$env.BRANCH_NAME" + now.format("yyMMdd", TimeZone.getTimeZone('CST'))
                }
                acrQuickBuild azureCredentialsId: 'AzureServicePrincipal',
                        resourceGroupName: env.ACR_RES_GROUP,
                        registryName: env.ACR_NAME,
                        platform: "Linux",
                        dockerfile: "Dockerfile",
                        imageNames: [[image: "$env.ACR_REGISTRY/$image_name:$env.BUILD_NUMBER"]]
            } 
        }
    }
}