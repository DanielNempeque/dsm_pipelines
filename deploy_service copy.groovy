def selector = ''
def new_image = ''
pipeline {
    agent any
    options {
        ansiColor('xterm')
    }
    properties([parameters([string(defaultValue: 'latest', description: 'image to deploy', name: 'IMAGE_TAG', trim: true)])])
    environment {
        ECR_REGISTRY = '436054236749.dkr.ecr.us-east-1.amazonaws.com'
        REPO = "cicdworkshop"
        ECS_NAME = 'cicd-workshop'
        TASK_FAMILY = 'cicd-definition'
    }
    stages {
        stage('Deploy'){
            steps{
                echo "Deploying service to $selector"
                withAWS(credentials: 'aws-credentials', region: 'us-east-1') {
                    sh """
                        TASK_DEFINITION=$(aws ecs describe-task-definition --task-definition "$TASK_FAMILY" --region "us-east-1")
                        NEW_TASK_DEFINTIION=$(echo \\$TASK_DEFINITION | jq --arg IMAGE "$IMAGE_TAG" '.taskDefinition | .containerDefinitions[0].image = $IMAGE | del(.taskDefinitionArn) | del(.revision) | del(.status) | del(.requiresAttributes) | del(.compatibilities)')
                        aws ecs register-task-definition --region "us-east-1" --cli-input-json "\\$NEW_TASK_DEFINTIION"
                    """
                    sh "aws ecs update-service --cluster $ECS_NAME  --service $REPO --force-new-deployment"
                }
            }
        }
    }
}
