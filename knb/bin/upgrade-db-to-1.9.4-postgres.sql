UPDATE db_version SET status=0;

INSERT INTO db_version (version, status, date_created) 
  VALUES ('1.9.4', 1, CURRENT_DATE);