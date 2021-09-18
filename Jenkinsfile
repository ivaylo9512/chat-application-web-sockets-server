pipeline {
    agent any
    triggers {
        pollSCM '* * * * *'
    }
    tools {
        jdk 'jdk-16'
    }
    stages {
        stage('Build') {
            steps {
                sh 'java -version'
                sh "chmod +x gradlew"
                sh './gradlew assemble'
            }
        }
        stage('Test') {
            steps {
                sh 'java -version'
                sh "chmod +x gradlew"
                sh './gradlew test'
            }
        }
        stage('Publish Test Coverage Report') {
            steps {
                step([$class: 'JacocoPublisher',
                    execPattern: '**/build/jacoco/*.exec',
                    classPattern: '**/build/classes',
                    sourcePattern: 'src/main/java',
                    exclusionPattern: 'src/test*'
                ])
                sh 'curl -Os https://uploader.codecov.io/latest/linux/codecov'
                sh 'chmod +x codecov'
                sh './codecov -t b2a44063-1f59-4288-8c0f-0a5c717dc2d6'
            }
        }
    }
}