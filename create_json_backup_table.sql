CREATE TABLE json_backup (
    id INT PRIMARY KEY IDENTITY(1,1),
    header NVARCHAR(255) NOT NULL,
    json_data NVARCHAR(MAX) NOT NULL,
	version int null DEFAULT 1 --Optimistic locking işlemi için kullanılır.
);

INSERT INTO json_backup (header, json_data, version) VALUES
('User Backup Ayşe 2025-04-12', '{"id":1,"username":"ayse_yilmaz","email":"ayse.yilmaz@example.com","role":"USER","is_verified":true,"is_locked":false,"version":1}',  1),
('User Backup Fatma 2025-04-12', '{"id":2,"username":"fatma_demir","email":"fatma.demir@example.com","role":"ADMIN","is_verified":false,"is_locked":false,"version":1}',  1),
('User Backup Zeynep 2025-04-12', '{"id":3,"username":"zeynep_kaya","email":"zeynep.kaya@example.com","role":"USER","is_verified":true,"is_locked":true,"version":1}',  1),
('User Backup Ahmet 2025-04-12', '{"id":4,"username":"ahmet_ozturk","email":"ahmet.ozturk@example.com","role","USER","is_verified":true,"is_locked":false,"version":1}', 1),
('User Backup Mehmet 2025-04-12', '{"id":5,"username":"mehmet_celik","email":"mehmet.celik@example.com","role":"USER","is_verified":false,"is_locked":false,"version":1}',  1);