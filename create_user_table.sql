USE ibb_java_se;

CREATE TABLE users (
    id INT IDENTITY(1,1) PRIMARY KEY,  /* 'id' alanı otomatik artan */
    username VARCHAR(255), /* Kullanıcı adı */
    email VARCHAR(255), /* Kullanıcı email adresi */
    password VARCHAR(255), /* BCRYPT kullanıcı şifresi */
	role VARCHAR(255) DEFAULT('USER'),
    is_verified BIT NOT NULL DEFAULT(1), /* Üyelik onaylanmış mı */
    is_locked BIT NOT NULL DEFAULT(0), /* Üyelik kilitli mi */
	version int null DEFAULT 1
);

ALTER TABLE users ADD CONSTRAINT df_version_default DEFAULT 1 FOR version;


select * from users;

INSERT INTO dbo.users VALUES ('Mehmet Basrioğlu','admin@admin.com','$2a$12$qYqiMuTm.Ybvbz3gB3iq.OxGbNy7byDg022W7dcILR1vwh4kvU9nu','ADMIN',1,0);
INSERT INTO dbo.users VALUES ('Hamit Mızrak','user@user.com','$2a$12$qYqiMuTm.Ybvbz3gB3iq.OxGbNy7byDg022W7dcILR1vwh4kvU9nu','USER',1,0);

DROP TABLE users;

UPDATE users SET version = version + 1 where id = 2 and version = 2;

SELECT * FROM users WHERE id=2 and version = 3;