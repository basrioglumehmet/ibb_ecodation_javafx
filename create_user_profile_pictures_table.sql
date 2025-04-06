USE ibb_java_se;

-- One to one
CREATE TABLE user_pictures (
    user_id INT NOT NULL PRIMARY KEY,  -- users tablosunun birincil anahtarıdır (aynı zamanda bu tablonun PK'si)
    image_data VARBINARY(MAX),           -- Profil fotoğrafı binary verisi
	version int null DEFAULT 1 --Optimistic locking işlemi için kullanılır.
    CONSTRAINT FK_ProfilePicture FOREIGN KEY (user_id)
        REFERENCES users(id)
);


select * from user_pictures;