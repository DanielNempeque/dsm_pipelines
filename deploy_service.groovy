def selector = ''
def new_image = ''
pipeline {
    agent any
    options {
        ansiColor('xterm')
    }
    parameters{
        string(defaultValue: 'latest', description: 'image to deploy', name: 'IMAGE_TAG', trim: true)
    }
    environment {
        ECR_REGISTRY = '436054236749.dkr.ecr.us-east-1.amazonaws.com'
        REPO = "cicdworkshop"
        ECS_NAME = 'cicd-workshop'
        TASK_FAMILY = 'cicd-definition'
    }
    stages {
        stage('Deploy'){
            steps{
                script{
                    echo "Deploying service to $selector"
                    withAWS(credentials: 'aws-credentials', region: 'us-east-1') {
                        def task_definition = sh script:"aws ecs describe-task-definition --task-definition '$TASK_FAMILY' --region 'us-east-1'", returnStdout: true
                        def new_task_definition = sh script:"""echo '$task_definition' | jq '.taskDefinition | .containerDefinitions[0].image = "$ECR_REGISTRY/$REPO:$IMAGE_TAG" | del(.taskDefinitionArn) | del(.revision) | del(.status) | del(.requiresAttributes) | del(.compatibilities)'""", returnStdout: true
                        sh "aws ecs register-task-definition --region 'us-east-1' --cli-input-json '$new_task_definition'"
                        sh "aws ecs update-service --cluster $ECS_NAME  --service $REPO --task-definition $TASK_FAMILY --force-new-deployment"
                    }
                }
            }
        }
    }
}
