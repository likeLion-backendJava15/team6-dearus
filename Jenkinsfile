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
        echo '📦 Maven build 시작'
        sh 'mvn clean package -DskipTests'
      }
    }

    stage('Docker Build') {
      steps {
        echo '🐳 Docker 이미지 빌드'
        sh 'docker build -t $IMAGE_NAME .'
      }
    }

    stage('Docker Push') {
      steps {
        echo '📤 DockerHub에 푸시'
        withCredentials([usernamePassword(credentialsId: 'dockerhub-creds', usernameVariable: 'DOCKER_USER', passwordVariable: 'DOCKER_PASS')]) {
          sh """
            echo "$DOCKER_PASS" | docker login -u "$DOCKER_USER" --password-stdin
            docker push $IMAGE_NAME
          """
        }
      }
    }

    stage('Deploy') {
      steps {
        echo '🚀 배포 준비'
        withCredentials([usernamePassword(credentialsId: 'db-credentials', usernameVariable: 'DB_USER', passwordVariable: 'DB_PASS')]) {
          sh """
            echo "데이터베이스 사용자: $DB_USER"
            echo "데이터베이스 비밀번호: $DB_PASS"
          """
        }
      }
    }
  }
}
