CREATE SEQUENCE uniq_id;
CREATE TABLE todo (
  id        BIGINT PRIMARY KEY,
  completed BOOLEAN,
  title     LONGVARCHAR
);