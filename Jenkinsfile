pipeline {
  agent {
    docker {
      image 'docker-group-registry.diamondq.com/maven-imagemagick-build:latest'
      args '-e MAVEN_CONFIG=/var/maven/.m2 -v /etc/passwd:/etc/passwd -v /home/mmansell:/home/mmansell -v /data/jenkins/m2-common:/var/maven/.m2 -v /data/jenkins/gpg:/var/maven/.gnupg'
    }
  }
  environment {
  	GIT_TOKEN = credentials("diamondq")
	GIT_COMMIT_SHORT = sh(
		script: "printf \$(git rev-parse --short ${GIT_COMMIT})",
		returnStdout: true
	)
	NEW_VERSION = sh(
	    script: "jx-release-version -previous-version=from-tag:v0.4 -next-version=increment:patch -tag -push-tag=false -git-user=diamondq -git-email=mike@diamondq.com -tag-prefix=v",
		returnStdout: true
	)
  }
  stages {
    stage('Build') {
      steps {
        sh 'echo "Building version ${NEW_VERSION}"'
        sh 'MAVEN_OPTS=-Duser.home=/var/maven mvn "-Djenkins=true" "-Drevision=${NEW_VERSION}" "-Dchangelist=" "-Dsha1=-${GIT_COMMIT_SHORT}" clean deploy'
      }
    }
  }
}