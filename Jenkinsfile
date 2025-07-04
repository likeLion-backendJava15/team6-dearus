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
            usernamePassword(credentialsId: 'mysql-root', usernameVariable: 'CRED_MYSQL_USER', passwordVariable: 'CRED_MYSQL_ROOT_PASS')
          ]) {
            withEnv([
              'DB_NAME=dearus',
              'DB_USERNAME=' + env.CRED_DB_USER,
              'DB_PASSWORD=' + env.CRED_DB_PASS,
              'MYSQL_ROOT_PASSWORD=' + env.CRED_MYSQL_ROOT_PASS
            ]) {
              sh '''
                echo "🔐 DB 접속 확인 - 사용자: $DB_USERNAME"

                # .env 파일 생성
                cat <<EOF > .env
                DB_NAME=$DB_NAME
                DB_USERNAME=$DB_USERNAME
                DB_PASSWORD=$DB_PASSWORD
                MYSQL_ROOT_PASSWORD=$MYSQL_ROOT_PASSWORD
                EOF

                # 기존 컨테이너 종료 (실패 무시)
                docker compose down || true

                # 최신 코드로 재배포
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
