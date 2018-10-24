-- postgres
CREATE TABLE email (
  id bigserial NOT NULL,
  email varchar(255) NOT NULL,
  name varchar(255) NOT NULL,
  subject varchar(255) NOT NULL,
  body varchar(1000000) NOT NULL,
  sent_date_time timestamp without time zone,
  created_date_time timestamp without time zone NOT NULL,
  tries int NOT NULL DEFAULT 0,
  sent boolean NOT NULL DEFAULT false,
  PRIMARY KEY (id)
);
