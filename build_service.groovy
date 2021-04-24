pipeline {
    agent any
    stages {
        stage('Execute tests'){
            steps{
                echo 'executing tests'
            }
        }
        stage('Build artifact'){
            steps{
                echo 'Building artifacts'
            }
        }
        stage('Store artifact'){
            steps{
                echo 'Storing the image in ACR'
            }
        }
    }
}