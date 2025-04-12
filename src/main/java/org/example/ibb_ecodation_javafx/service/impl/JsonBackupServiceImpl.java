package org.example.ibb_ecodation_javafx.service.impl;

import lombok.AllArgsConstructor;
import org.example.ibb_ecodation_javafx.core.logger.SecurityLogger;
import org.example.ibb_ecodation_javafx.exception.OptimisticLockException;
import org.example.ibb_ecodation_javafx.model.JsonBackup;
import org.example.ibb_ecodation_javafx.repository.JsonBackupRepository;
import org.example.ibb_ecodation_javafx.repository.query.JsonBackupQuery;
import org.example.ibb_ecodation_javafx.service.JsonBackupService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.function.Consumer;

/**
 * JsonBackup işlemlerini yöneten servis sınıfı. JSON yedek oluşturma, okuma, güncelleme ve silme işlemlerini
 * gerçekleştirir. Optimistik kilitleme ile veri tutarlılığını sağlar.
 */
@AllArgsConstructor
@Service
public class JsonBackupServiceImpl implements JsonBackupService {

    private final JsonBackupRepository jsonBackupRepository;
    private final SecurityLogger securityLogger;

    /**
     * Yeni bir JSON yedek oluşturur.
     *
     * @param entity Oluşturulacak JSON yedek nesnesi
     * @return Oluşturulan JSON yedek nesnesi
     */
    @Override
    public JsonBackup create(JsonBackup entity) {
        JsonBackup created = jsonBackupRepository.create(entity, JsonBackupQuery.CREATE_JSON_BACKUP,
                List.of(entity.getHeader(), entity.getJsonData(), entity.getCreatedAt(), entity.getVersion()));
        securityLogger.logOperation("JSON Backup created: header=" + entity.getHeader());
        return created;
    }

    /**
     * Belirtilen ID'ye sahip JSON yedeği siler.
     *
     * @param id Silinecek JSON yedeğin ID'si
     */
    @Override
    public void delete(int id) {
        jsonBackupRepository.delete(JsonBackupQuery.DELETE_JSON_BACKUP_BY_ID, List.of(id));
        securityLogger.logOperation("JSON Backup deleted: id=" + id);
    }

    /**
     * Belirtilen ID'ye sahip JSON yedeği okur ve sonucu bir geri çağırım fonksiyonuna iletir.
     *
     * @param id       Okunacak JSON yedeğin ID'si
     * @param callback JSON yedek nesnesini işlemek için geri çağırım fonksiyonu
     */
    @Override
    public void read(int id, Consumer<JsonBackup> callback) {
        JsonBackup backup = jsonBackupRepository.read(JsonBackup.class, JsonBackupQuery.READ_JSON_BACKUP_BY_ID, List.of(id));
        callback.accept(backup);
        securityLogger.logOperation("JSON Backup read: id=" + id);
    }

    /**
     * Tüm JSON yedekleri okur ve bir liste olarak döndürür.
     *
     * @return Tüm JSON yedeklerin listesi
     */
    @Override
    public List<JsonBackup> readAll() {
        List<JsonBackup> backups = jsonBackupRepository.readAll(JsonBackup.class, JsonBackupQuery.READ_ALL_JSON_BACKUPS, List.of());
        securityLogger.logOperation("All JSON Backups read: count=" + backups.size());
        return backups;
    }

    /**
     * Belirtilen JSON yedeği günceller ve sonucu bir geri çağırım fonksiyonuna iletir.
     *
     * @param entity   Güncellenecek JSON yedek nesnesi
     * @param callback Güncellenen JSON yedeği işlemek için geri çağırım fonksiyonu
     */
    @Override
    public void update(JsonBackup entity, Consumer<JsonBackup> callback) {
        try {
            JsonBackup updated = jsonBackupRepository.update(entity, JsonBackupQuery.UPDATE_JSON_BACKUP_BY_ID,
                    List.of(entity.getHeader(), entity.getJsonData(), entity.getCreatedAt(), entity.getId(), entity.getVersion()));
            callback.accept(updated);
            securityLogger.logOperation("JSON Backup updated: id=" + entity.getId());
        } catch (Exception e) {
            securityLogger.logOperation("JSON Backup update failed: id=" + entity.getId() + ", error=" + e.getMessage());
            throw new OptimisticLockException("Optimistic locking failed for JSON Backup id=" + entity.getId());
        }
    }

    /**
     * Belirtilen ID ile tüm JSON yedekleri okur. Bu yöntem kullanım dışı bırakılmıştır.
     *
     * @param id JSON yedek ID'si
     * @return JSON yedek listesi
     * @throws RuntimeException Bu yöntem kullanım dışıdır
     * @deprecated Kullanım dışı bırakılmıştır
     */
    @Deprecated
    @Override
    public List<JsonBackup> readAll(int id) {
        throw new RuntimeException("readAll Disabled: " + getClass().getName());
    }
}
