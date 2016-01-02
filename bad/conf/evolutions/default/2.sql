# --- !Ups

CREATE TABLE message (
  user VARCHAR(255) NOT NULL,
  text TEXT NOT NULL,
  timeSent DATETIME NOT NULL
);

# --- !Downs

DROP TABLE IF EXISTS message;
