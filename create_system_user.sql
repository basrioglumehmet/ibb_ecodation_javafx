CREATE LOGIN mehmet WITH PASSWORD = '123456';


create user mehmet for login mehmet;

USE ibb_java_se;
ALTER ROLE db_datareader ADD MEMBER mehmet;
ALTER ROLE db_datawriter ADD MEMBER mehmet;