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
                    azureServicePrincipal('AzureServicePrincipal')
                ]) {
                    sh "az login --service-principal -u $AZURE_CLIENT_ID -p $AZURE_CLIENT_SECRET -t $AZURE_TENANT_ID "
                    sh "az account set --subscription $AZURE_SUBSCRIPTION_ID"
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