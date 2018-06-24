pipeline {
  agent {
    docker {
      image 'maven:3-jdk-10'
      args '--user root:root -v /data/jenkins/m2-common:/root/.m2 -v /data/jenkins/gpg:/root/.gnupg'
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