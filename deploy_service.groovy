def selector = ''
pipeline {
    agent any
    environment {
        ACR_REGISTRY = 'cicdworkshop.azurecr.io'
        ACR_RES_GROUP = 'ci-cd-workshop'
        ACR_NAME = 'cicdworkshop'
    }
    stages {
        stage('Deploy service non prod'){
            when {
                expression { return "$Environments".contains('Dev') || "$Environments".contains('Qa') || "$Environments".contains('Stg')}
            }
            steps{
                script{
                    selector = "$Environments".toLowerCase()
                }
                echo "Deploying service to $selector"
                withCredentials([
                    usernamePassword(credentialsId: 'AzureACR', usernameVariable:'ACR_USER', passwordVariable: 'ACR_PASSWORD'),
                    azureServicePrincipal('AzureServicePrincipal')
                ]) {
                    sh "az login --service-principal -u $AZURE_CLIENT_ID -p $AZURE_CLIENT_SECRET -t $AZURE_TENANT_ID"   
                    sh "az webapp config container set --docker-custom-image-name $ACR_REGISTRY/$Image_name --docker-registry-server-password $ACR_PASSWORD --docker-registry-server-url https://$ACR_REGISTRY --docker-registry-server-user $ACR_USER --name emojiselector-$selector --resource-group $ACR_RES_GROUP"
                    sh "az webapp restart --name emojiselector-$selector --resource-group $ACR_RES_GROUP"
                }
            }
        }
        stage('Deploy service PROD'){
            when {
                expression { return "$Environments".contains('Prod') }
            }
            steps{
                script{
                    def userInput = input message: 'Approve plan output for production?', ok: 'Approve plan'
                }
                echo 'Deploying service'
                withCredentials([
                    usernamePassword(credentialsId: 'AzureACR', usernameVariable:'ACR_USER', passwordVariable: 'ACR_PASSWORD'),
                    azureServicePrincipal('AzureServicePrincipal')
                ]) {
                    sh "az login --service-principal -u $AZURE_CLIENT_ID -p $AZURE_CLIENT_SECRET -t $AZURE_TENANT_ID"   
                    sh "az webapp config container set --docker-custom-image-name $ACR_REGISTRY/$Image_name --docker-registry-server-password $ACR_PASSWORD --docker-registry-server-url https://$ACR_REGISTRY --docker-registry-server-user $ACR_USER --name emojiselector --resource-group $ACR_RES_GROUP"
                    sh "az webapp restart --name emojiselector --resource-group $ACR_RES_GROUP"
                }
            }
        }
    }
}
