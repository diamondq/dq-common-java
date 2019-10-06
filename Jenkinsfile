pipeline {
  agent {
    docker {
      image 'maven:3-jdk-8'
      args '--user 1000:1000 -v /data/jenkins/m2-common:/root/.m2 -v /data/jenkins/gpg:/root/.gnupg'
    }
    
  }
  stages {
    stage('Build') {
      steps {
        sh '''cd common-root
mvn "-Djenkins=true" clean deploy'''
      }
    }
  }
}