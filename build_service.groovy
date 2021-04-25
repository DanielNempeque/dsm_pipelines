pipeline {
    agent any
    environment {
        ACR_REGISTRY = 'cicdworkshop.azurecr.io'
        IMAGE_NAME    = "$env.BRANCH_NAME"+"$java.time.LocalDate.now()"
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
                acrQuickBuild azureCredentialsId: 'AzureServicePrincipal',
                    resourceGroupName: env.ACR_RES_GROUP,
                    registryName: env.ACR_NAME,
                    platform: "Linux",
                    dockerfile: "Dockerfile",
                    imageNames: [[image: "$env.ACR_REGISTRY/$env.IMAGE_NAME:$env.BUILD_NUMBER"]]
            }
        }
    }
}