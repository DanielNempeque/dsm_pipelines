pipeline {
    agent any
    environment {
        ACR_REGISTRY = 'cicdworkshop.azurecr.io'
        ACR_RES_GROUP = 'ci-cd-workshop'
        ACR_NAME = 'cicdworkshop'
    }
    stages {
        stage('Deploy service DEV'){
            steps{
                echo 'Deploying service'
                withCredentials([
                    usernamePassword(credentialsId: 'AzureACR', usernameVariable:'ACR_USER', passwordVariable: 'ACR_PASSWORD'),
                     usernamePassword(credentialsId: 'AzureDnempeque', usernameVariable:'USERNAME', passwordVariable: 'PASSWORD')
                ]) {
                    sh "az login -u $USERNAME -p $PASSWORD"   
                    sh "az webapp config container set --docker-custom-image-name $Image_name --docker-registry-server-password $ACR_PASSWORD --docker-registry-server-url https://$ACR_REGISTRY --docker-registry-server-user $ACR_USER --name emojiselector-dev --resource-group $ACR_RES_GROUP"
                    sh "az webapp restart --name emojiselector-dev --resource-group $ACR_RES_GROUP"
                }
            }
        }
        stage('Deploy service STG'){
            steps{
                echo 'Deploying service'
            }
        }
        stage('Deploy service PROD'){
            steps{
                echo 'Deploying service'
            }
        }
    }
}