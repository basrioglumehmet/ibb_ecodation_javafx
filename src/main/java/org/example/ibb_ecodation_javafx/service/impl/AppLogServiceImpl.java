package org.example.ibb_ecodation_javafx.service.impl;

import lombok.AllArgsConstructor;
import org.example.ibb_ecodation_javafx.exception.OptimisticLockException;
import org.example.ibb_ecodation_javafx.model.AppLog;
import org.example.ibb_ecodation_javafx.repository.AppLogRepository;
import org.example.ibb_ecodation_javafx.repository.query.AppLogQuery;
import org.example.ibb_ecodation_javafx.service.AppLogService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.function.Consumer;

/**
 * AppLog işlemlerini yöneten servis sınıfı. Uygulama logu oluşturma, okuma, güncelleme ve silme işlemlerini
 * gerçekleştirir. Optimistik kilitleme ile veri tutarlılığını sağlar.
 */
@AllArgsConstructor
@Service
public class AppLogServiceImpl implements AppLogService {

    private final AppLogRepository appLogRepository;

    /**
     * Yeni bir uygulama logu oluşturur.
     *
     * @param entity Oluşturulacak uygulama logu nesnesi
     * @return Oluşturulan uygulama logu nesnesi
     */
    @Override
    public AppLog create(AppLog entity) {
        return appLogRepository.create(entity, AppLogQuery.CREATE_APP_LOG,
                List.of(entity.getDescription(), entity.getComputerName(), entity.getIpAddresses(),
                        entity.getAtTime(), entity.getVersion()));
    }

    /**
     * Belirtilen ID'ye sahip uygulama logunu siler.
     *
     * @param id Silinecek uygulama logunun ID'si
     */
    @Override
    public void delete(int id) {
        appLogRepository.delete(AppLogQuery.DELETE_APP_LOG_BY_ID, List.of(id));
    }

    /**
     * Belirtilen ID'ye sahip uygulama logunu okur ve sonucu bir geri çağırım fonksiyonuna iletir.
     *
     * @param id       Okunacak uygulama logunun ID'si
     * @param callback Uygulama logu nesnesini işlemek için geri çağırım fonksiyonu
     */
    @Override
    public void read(int id, Consumer<AppLog> callback) {
        AppLog log = appLogRepository.read(AppLog.class, AppLogQuery.READ_APP_LOG_BY_ID, List.of(id));
        callback.accept(log);
    }

    /**
     * Tüm uygulama loglarını okur ve bir liste olarak döndürür.
     *
     * @return Tüm uygulama loglarının listesi
     */
    @Override
    public List<AppLog> readAll() {
        return appLogRepository.readAll(AppLog.class, AppLogQuery.READ_ALL_APP_LOGS, List.of());
    }

    /**
     * Belirtilen uygulama logunu günceller ve sonucu bir geri çağırım fonksiyonuna iletir.
     *
     * @param entity   Güncellenecek uygulama logu nesnesi
     * @param callback Güncellenen uygulama logunu işlemek için geri çağırım fonksiyonu
     */
    @Override
    public void update(AppLog entity, Consumer<AppLog> callback) {
        try {
            AppLog updated = appLogRepository.update(entity, AppLogQuery.UPDATE_APP_LOG_BY_ID,
                    List.of(entity.getDescription(), entity.getComputerName(), entity.getIpAddresses(),
                            entity.getAtTime(), entity.getId(), entity.getVersion()));
            callback.accept(updated);
        } catch (Exception e) {
            throw new OptimisticLockException("Optimistic locking failed for App Log id=" + entity.getId());
        }
    }

    /**
     * Belirtilen ID ile tüm uygulama loglarını okur. Bu yöntem kullanım dışı bırakılmıştır.
     *
     * @param id Uygulama logu ID'si
     * @return Uygulama logu listesi
     * @throws RuntimeException Bu yöntem kullanım dışıdır
     * @deprecated Kullanım dışı bırakılmıştır
     */
    @Deprecated
    @Override
    public List<AppLog> readAll(int id) {
        throw new RuntimeException("readAll Disabled: " + getClass().getName());
    }
}