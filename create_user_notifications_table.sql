﻿USE ibb_java_se;

-- One to many: Bir kullanıcıya birden fazla bildirim atanabilir
CREATE TABLE user_notifications (
    id INT IDENTITY(1,1) PRIMARY KEY,   -- Otomatik artan birincil anahtar
    user_id INT NOT NULL,                -- users tablosuna dış anahtar
    header VARCHAR(255),                 -- Bildirim başlığı
    description VARCHAR(255),            -- Bildirim açıklaması
    type VARCHAR(255) DEFAULT 'INFO',    -- Bildirim türü (varsayılan: INFO)
		version int null DEFAULT 1
    CONSTRAINT FK_UserNotifications FOREIGN KEY (user_id)
        REFERENCES users(id)
);

select * from user_notifications;
