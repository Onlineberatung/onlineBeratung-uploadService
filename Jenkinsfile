pipeline {
	agent any
	environment {
        ARTIFACT_TAG = "${BRANCH_NAME}-${BUILD_ID}"
    }
	stages {
 	    stage('Build') {
            agent {
                docker {
                    image 'maven:3.5.4-jdk-8-alpine'
                    args '-v /root/.m2:/root/.m2'
                }
            }
            steps {
                echo 'Build using goals: clean and package'
                sh 'mvn -B -Pprod clean package'
                stash includes: 'target/*.jar', name: 'targetfiles'
            }
        }
        stage('Docker build') {
        	steps {
        		echo 'Creating Docker Container..'
        		unstash 'targetfiles'
        		sh 'docker build --no-cache -t ${DOCKER_REGISTRY}/${ARTIFACT_GROUP}/${ARTIFACT_NAME_UPLOADSERVICE} .'
        	}
        }
        stage('Docker push') {
        	agent any
        	steps {
        		echo 'Pushing Docker Container to repository..'
        		script {
                    docker.withRegistry(DOCKER_REGISTRY_URL, DOCKER_REGISTRY_CREDENTIALS_ID) {
                        docker.image("${DOCKER_REGISTRY}/${ARTIFACT_GROUP}/${ARTIFACT_NAME_UPLOADSERVICE}").push('${ARTIFACT_TAG}')
                    }
                }
        	}
        }
    }
}