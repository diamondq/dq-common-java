pipeline {
  agent {
    docker {
      image 'maven:3-jdk-8'
      args '--user 1000:1000 -e MAVEN_CONFIG=/var/maven/.m2 -v /data/jenkins/m2-common:/var/maven/.m2 -v /data/jenkins/gpg:/var/maven/.gnupg'
    }
    
  }
  stages {
    stage('Build') {
      steps {
        sh '''cd common-root
MAVEN_OPTS=-Duser.home=/var/maven mvn "-Djenkins=true" clean deploy'''
      }
    }
  }
}