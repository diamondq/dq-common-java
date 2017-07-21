pipeline {
  agent {
    docker {
      image 'maven:3-jdk-8'
      args '-v /data/jenkins/m2:/.m2 -v /data/jenkins/gpg:/.gnupg'
    }
    
  }
  stages {
    stage('Clean') {
      steps {
        sh '''cd common-root
mvn "-Duser.home=/" clean'''
      }
    }
    stage('Build') {
      steps {
        sh '''cd common-root
mvn "-Duser.home=/" install'''
      }
    }
    stage('Deploy') {
      steps {
        sh '''cd common-root
mvn "-Duser.home=/" -Dmaven.test.skip=true deploy'''
      }
    }
  }
}