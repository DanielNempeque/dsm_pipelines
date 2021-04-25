pipeline {
    agent any
    stages {
        stage('Pull image'){
            steps{
                echo 'pull image'
            }
        }
        stage('Deploy service DEV'){
            steps{
                echo 'Deploying service'
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