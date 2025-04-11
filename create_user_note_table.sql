USE ibb_java_se;
GO
DROP TABLE user_notes;
CREATE TABLE user_notes (
    id INT  IDENTITY(1,1) PRIMARY KEY,
	user_id INT NOT NULL,                -- users tablosuna dış anahtar
    report_at DATETIME  NOT NULL,
	header VARCHAR(255),
    description VARCHAR(255),
	version int null DEFAULT 1
    CONSTRAINT FK_NoteFK FOREIGN KEY (user_id)
        REFERENCES users(id)
);

SELECT * from user_notes;