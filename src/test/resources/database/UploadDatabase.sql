CREATE TABLE IF NOT EXISTS UPLOADBYUSER (
  ID BIGINT NOT NULL,
  USER_ID varchar(36) NOT NULL,
  SESSION_ID varchar(36) NOT NULL,
  CREATE_DATE datetime NOT NULL,
  PRIMARY KEY (ID)
);
CREATE SEQUENCE IF NOT EXISTS SEQUENCE_UPLOADBYUSER
START WITH 1
INCREMENT BY 1;
