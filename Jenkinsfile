pipeline {
  agent any
  stages {
    stage('Clean') {
      steps {
        sh './gradlew clean'
      }
    }
    stage('Jar') {
      steps {
        sh './gradlew jar'
      }
    }
    stage('Test') {
      steps {
        sh './gradlew test'
      }
    }
    stage('Build') {      
      steps {
        sh './gradlew build'
      }
    }
  }
  tools {
    jdk 'JDK - 1.8.0_152'
  }
  environment {
    GRADLE_OPTS = '-Xmx64m -Dorg.gradle.jvmargs=\'-Xmx1024m -XX:MaxPermSize=64m\''
  }
}