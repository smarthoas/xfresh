--создание базы данных
CREATE DATABASE test;

--структура для хранения проверок
DROP TABLE IF EXISTS db_check;
CREATE TABLE db_check (
  query    TEXT NOT NULL,
  dsc    TEXT NOT NULL
) CHARACTER SET utf8;

--пример проверок
INSERT INTO db_check (query, dsc) VALUES
  ('select 1 result, \'ERROR\' dsc', 'Error example'),
  ('select 0 result, \'OK\' dsc', 'OK example');
