create database if not exists dearus;
use dearus;

CREATE TABLE User (
  id   BIGINT AUTO_INCREMENT PRIMARY KEY,
  user_id     VARCHAR(100) NOT NULL UNIQUE KEY,
  nickname    VARCHAR(50) NOT NULL,
  password    VARCHAR(255) NOT NULL,
  email VARCHAR(255) NOT NULL
);

CREATE TABLE Diary (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(100) NOT NULL,
  is_deleted BIT NOT NULL DEFAULT 0,
  owner_id BIGINT NOT NULL,
  FOREIGN KEY (owner_id) REFERENCES User(id) ON DELETE CASCADE
);

CREATE TABLE Diary_Member (
  diary_id   BIGINT,
  user_id    BIGINT,
  role       ENUM('OWNER', 'GUEST') NOT NULL DEFAULT 'GUEST',
  invited_at  DATETIME DEFAULT CURRENT_TIMESTAMP,
  accepted   BOOLEAN DEFAULT FALSE,
  PRIMARY KEY (diary_id, user_id),
  FOREIGN KEY (diary_id) REFERENCES Diary(id) ON DELETE CASCADE,
  FOREIGN KEY (user_id) REFERENCES User(id) ON DELETE CASCADE
);

CREATE TABLE Diary_Entry (
  id     BIGINT AUTO_INCREMENT PRIMARY KEY,
  diary_id     BIGINT NOT NULL,
  author_id    BIGINT NOT NULL,
  emotion      ENUM('행복해', '즐거워', '감사해', '사랑해', '뿌듯해', '그저그래', '화나', '힘들어') NOT NULL default '행복해',
  title        VARCHAR(100),
  content      TEXT,
  image_url    VARCHAR(255),
  created_at   DATETIME DEFAULT CURRENT_TIMESTAMP,
  updated_at   DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  FOREIGN KEY (diary_id) REFERENCES Diary(id) ON DELETE CASCADE,
  FOREIGN KEY (author_id) REFERENCES User(id)
);

CREATE TABLE tag (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    user_id BIGINT NOT NULL,
    CONSTRAINT fk_tag_member FOREIGN KEY (user_id) REFERENCES user(id),
    CONSTRAINT uk_user_tag UNIQUE (user_id, name)
);


CREATE TABLE Entry_Tag (
  entry_id  BIGINT,
  tag_id    BIGINT,
  PRIMARY KEY (entry_id, tag_id),
  FOREIGN KEY (entry_id) REFERENCES Diary_Entry(id) ON DELETE CASCADE,
  FOREIGN KEY (tag_id) REFERENCES Tag(id) ON DELETE CASCADE
);

CREATE TABLE Comment (
  id   BIGINT AUTO_INCREMENT PRIMARY KEY,
  entry_id     BIGINT NOT NULL,
  user_id      BIGINT NOT NULL,
  content      TEXT NOT NULL,
  parent_comment_id BIGINT default null,
  created_at   DATETIME DEFAULT CURRENT_TIMESTAMP,
  updated_at   DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  FOREIGN KEY (entry_id) REFERENCES Diary_Entry(id) ON DELETE CASCADE,
  FOREIGN KEY (user_id)  REFERENCES User(id) ON DELETE CASCADE,
  FOREIGN KEY (parent_comment_id) references comment(id) on delete cascade
);

SET SQL_SAFE_UPDATES = 0;

SET SQL_SAFE_UPDATES = 1;

SHOW PROCESSLIST;

