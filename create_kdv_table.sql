USE ibb_java_se;
GO

CREATE TABLE vat (
    id INT NOT NULL PRIMARY KEY,
    baseAmount DECIMAL,           -- KDV'siz tutar
    rate DECIMAL NOT NULL,         -- KDV oranı (%18 gibi)
    amount DECIMAL NOT NULL,      -- KDV tutarı
    totalAmount DECIMAL NOT NULL, -- KDV dahil toplam tutar
    receiptNumber VARCHAR(100) NOT NULL,
    transactionDate DATE NOT NULL,
    description VARCHAR(255),
    exportFormat VARCHAR(50),
    is_deleted BIT DEFAULT 0            -- Soft delete için bayrak (0: aktif, 1: silinmiş)
);
