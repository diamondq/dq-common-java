pipeline {
  agent {
    docker {
      image 'docker-group-registry.diamondq.com/maven-imagemagick-build:latest'
      args '-e MAVEN_CONFIG=/var/maven/.m2 -v /data/jenkins/m2-common:/var/maven/.m2 -v /data/jenkins/gpg:/var/maven/.gnupg'
    }
  }
  environment {
	GIT_COMMIT_SHORT = sh(
		script: "printf \$(git rev-parse --short ${GIT_COMMIT})",
		returnStdout: true
	)
	NEW_VERSION = sh(
	    script: "jx-release-version -fetch-tags -previous-version=0.4 -next-version=increment:patch -tag"
	    returnStdout: true
	)
  }
  stages {
    stage('Build') {
      steps {
        sh 'MAVEN_OPTS=-Duser.home=/var/maven mvn "-Djenkins=true" "-Drevision=${NEW_VERSION}" "-Dchangelist=" "-Dsha1=-${GIT_COMMIT_SHORT}" clean deploy'
      }
    }
  }
}