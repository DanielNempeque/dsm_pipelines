pipeline {
    agent any
    environment {
        ACR_REGISTRY = 'cicdworkshop.azurecr.io'
        ACR_RES_GROUP = 'ci-cd-workshop'
        ACR_NAME = 'cicdworkshop'
    }
    stages {
        stage('Execute tests'){
            steps{
                echo 'executing tests'
                nodejs('Node-16.0'){
                    sh 'yarn install'
                    sh 'yarn run test'
                }
            }
        }
        stage('Build artifact'){
            steps{
                step{
                    def date= new Date()
                    def image_name = "$env.BRANCH_NAME" + date.format("yyMMdd", TimeZone.getTimeZone('CST'))
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