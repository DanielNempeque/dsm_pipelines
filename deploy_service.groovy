pipeline {
    agent any
    stages {
        stage('Pull image'){
            steps{
                echo 'pull image'
            }
        }
        stage('Deploy service'){
            steps{
                echo 'Deploying service'
            }
        }

    }
}