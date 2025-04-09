USE ibb_java_se;

-- One to one
CREATE TABLE user_otp_codes (
    user_id INT NOT NULL PRIMARY KEY,  -- users tablosunun birincil anahtarıdır (aynı zamanda bu tablonun PK'si)
    otp VARCHAR(8),        
	version int null DEFAULT 1 --Optimistic locking işlemi için kullanılır.
    CONSTRAINT FK_OtpCodes FOREIGN KEY (user_id)
        REFERENCES users(id)
);