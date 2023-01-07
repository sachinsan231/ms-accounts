// Declarative
//with declarative checkout is automated
pipeline{
	agent any
	//agent { docker { image 'node:13.8'}}
	environment {
		dockerHome = tool 'myDocker'
		mavenHome = tool 'myMaven'
		PATH = "$dockerHome/bin:$mavenHome/bin:$PATH"
	}
	stages{
		stage('Checkout'){
			steps{
				//sh 'node --version'
				sh 'mvn --version'
				sh 'docker version'
				echo 'Build'
				echo "PATH - $PATH"
				echo "BUILD_URL - $env.BUILD_URL"
				echo "BUILD_TAG - $env.BUILD_TAG"
				echo "BUILD_NUMBER - $env.BUILD_NUMBER"
				/* echo 'build info'
				echo 'PATH: $PATH'
				echo 'BUILD_NUMBER : $env.BUILD_NUMBER'
				echo 'BUIL ID : $env.BUILD_ID'
				echo 'JOB NAME: $env.JOB_NAME'
				echo 'BUILD URL: $env.BUILD_URL' */
			}
		}

		/*stage('Compile'){
			steps{
				sh 'mvn clean compile'
			}
		}
		/* stage('Test'){
					steps{
					sh 'mvn test'
				}
			}
			stage('Integration Test'){
				steps{
					sh 'mvn failsafe:integration-test failsafe:verify'
				}
			}  */

		stage('Package'){
			steps{
				sh 'mvn package -DskipTests'
			}
		}

		stage('Build Docker Image'){

			steps{
				//docker build -t sachinsan2131/currency-exchange-devops:$env.BUILD_TAG
				script{
					dockerImage= docker.build("sachinsan2131/accounts:${env.BUILD_TAG}")
				}
			}
		}

		stage('Push Docker Image'){
			steps{
				script{
					docker.withRegistry('', 'dockerhub'){
						dockerImage.push();
						dockerImage.push('latest');
					}
				}
			}
		}
	} 
	post {
		always {
			echo 'I run always'
		}
		success {
			echo 'I run when you are successful'
		}
		failure {
			echo 'I run when you fail'
		}
	}
}