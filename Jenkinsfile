pipeline {
  agent {
    docker {
      image 'maven:3-jdk-8'
      args '-v /data/jenkins/m2-common:/.m2 -v /data/jenkins/gpg:/.gnupg'
    }
    
  }
  stages {
    stage('Build') {
      steps {
        sh '''cd common-root
mvn "-Duser.home=/" "-Djenkins=true" clean deploy'''
      }
    }
  }
}