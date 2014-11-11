CREATE DATABASE xfresh_auth;

USE xfresh_auth;

CREATE TABLE auth_user (
    user_id               INTEGER           AUTO_INCREMENT        PRIMARY KEY
  , login                 VARCHAR(100)
  , fio                   VARCHAR(256)
  , passwd_hash           VARCHAR(256)
  , passwd_add            VARCHAR(256)
) ENGINE = INNODB character set utf8;

CREATE UNIQUE INDEX idx_auth_user_login ON auth_user(login);

GRANT ALL PRIVILEGES ON xfresh_auth.* TO xfresh@'%' IDENTIFIED BY 'xfresh' WITH GRANT OPTION;


