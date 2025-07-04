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
        dir('backend') {
          sh 'mvn clean package -DskipTests'
        }
      }
    }

    stage('Docker Build') {
      steps {
        echo '🐳 Docker 이미지 빌드'
        dir('backend') {
          sh 'docker build -t $IMAGE_NAME .'
        }
      }
    }

    stage('Docker Push') {
      steps {
        echo '📤 DockerHub에 푸시'
        withCredentials([usernamePassword(credentialsId: 'dockerhub-creds', usernameVariable: 'DOCKER_USER', passwordVariable: 'DOCKER_PASS')]) {
          dir('backend') {
            sh """
              echo "$DOCKER_PASS" | docker login -u "$DOCKER_USER" --password-stdin
              docker push $IMAGE_NAME
            """
          }
        }
      }
    }

    stage('Deploy') {
      steps {
        echo '🚀 Docker Compose로 배포 시작'
        dir('backend') {
          withCredentials([
            usernamePassword(credentialsId: 'db-credentials', usernameVariable: 'CRED_DB_USER', passwordVariable: 'CRED_DB_PASS'),
            usernamePassword(credentialsId: 'mysql-root', usernameVariable: 'CRED_MYSQL_ROOT_USER', passwordVariable: 'CRED_MYSQL_ROOT_PASS')
          ]) {
            withEnv([
              'DB_NAME=dearus'
            ]) {
              sh '''
                echo "🔐 DB 접속 확인 - 사용자: $CRED_DB_USER"

                # 🔧 .env 파일 생성
                echo "DB_NAME=$DB_NAME" > .env
                echo "DB_USERNAME=$CRED_DB_USER" >> .env
                echo "DB_PASSWORD=$CRED_DB_PASS" >> .env
                echo "MYSQL_ROOT_PASSWORD=$CRED_MYSQL_ROOT_PASS" >> .env

                cat .env

                # 🧹 기존 컨테이너 종료
                docker compose down || true

                # 🚀 새로 시작
                docker compose up -d
              '''
            }
          }
        }
      }
    }


  }

  post {
    failure {
      echo '❌ 실패: 로그 확인 요망'
    }
  }
}
