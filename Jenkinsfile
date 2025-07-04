pipeline {
  agent any

  tools {
    jdk 'jdk21'
    maven 'maven'
  }

  environment {
    IMAGE_NAME = 'yennies/dearus-app'
  }

  stages {
    stage('Build') {
      steps {
        dir('backend') {
          echo '📦 Maven build 시작'
          sh 'mvn clean package -DskipTests'
        }
      }
    }

    stage('Docker Build') {
      steps {
        dir('backend') {
          echo '🐳 Docker 이미지 빌드'
          sh 'docker build -t $IMAGE_NAME .'
        }
      }
    }

    stage('Docker Push') {
      steps {
        dir('backend') {
          echo '📤 DockerHub 푸시'
          withCredentials([usernamePassword(credentialsId: 'dockerhub-creds', usernameVariable: 'DOCKER_USER', passwordVariable: 'DOCKER_PASS')]) {
            sh '''
              echo "$DOCKER_PASS" | docker login -u "$DOCKER_USER" --password-stdin
              docker push $IMAGE_NAME
            '''
          }
        }
      }
    }
  }

  post {
    success {
      echo "✅ 빌드 및 Docker 이미지 푸시 완료"
    }
    failure {
      echo "❌ 실패: 로그 확인 요망"
    }
  }
}
