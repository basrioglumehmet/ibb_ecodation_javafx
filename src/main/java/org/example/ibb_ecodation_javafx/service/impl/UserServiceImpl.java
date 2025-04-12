package org.example.ibb_ecodation_javafx.service.impl;

import javafx.stage.Window;
import lombok.AllArgsConstructor;
import org.example.ibb_ecodation_javafx.backup.UserBackup;
import org.example.ibb_ecodation_javafx.core.logger.SecurityLogger;
import org.example.ibb_ecodation_javafx.core.service.LanguageService;
import org.example.ibb_ecodation_javafx.exception.OptimisticLockException;
import org.example.ibb_ecodation_javafx.mapper.UserMapper;
import org.example.ibb_ecodation_javafx.model.JsonBackup;
import org.example.ibb_ecodation_javafx.model.User;
import org.example.ibb_ecodation_javafx.repository.UserRepository;
import org.example.ibb_ecodation_javafx.repository.query.UserQuery;
import org.example.ibb_ecodation_javafx.service.JsonBackupService;
import org.example.ibb_ecodation_javafx.service.UserService;
import org.example.ibb_ecodation_javafx.utils.JsonBackupUtil;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * Kullanıcı işlemlerini yöneten servis sınıfı. Kullanıcı oluşturma, güncelleme, silme, okuma ve yedekleme gibi
 * işlemleri gerçekleştirir. Optimistik kilitleme mekanizması ile veri tutarlılığını sağlar.
 */
@AllArgsConstructor
@Service
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    private final UserRepository userRepository;
    private final SecurityLogger securityLogger;
    private final UserBackup userBackup;
    private final JsonBackupService jsonBackupService;
    private final LanguageService languageService;

    /**
     * Yeni bir kullanıcı oluşturur ve şifresini şifreler.
     *
     * @param entity Oluşturulacak kullanıcı nesnesi
     * @return Oluşturulan kullanıcı nesnesi
     */
    @Override
    public User create(User entity) {
        entity.setPassword(BCrypt.hashpw(entity.getPassword(), BCrypt.gensalt(12)));
        var created = userRepository.create(entity, UserQuery.CREATE_USER,
                List.of(entity.getUsername(), entity.getEmail(), entity.getPassword(), entity.getRole().toString(), entity.isVerified(), entity.isLocked()));
        securityLogger.logUserOperation(entity.toString(), "kullanıcı oluşturma");
        return created;
    }

    /**
     * Belirtilen ID'ye sahip kullanıcıyı siler.
     *
     * @param id Silinecek kullanıcının ID'si
     */
    @Override
    public void delete(int id) {
        userRepository.delete(UserQuery.DELETE_BY_ID, List.of(id));
    }

    /**
     * Belirtilen ID'ye sahip kullanıcıyı okur ve sonucu bir geri çağırım fonksiyonuna iletir.
     *
     * @param id       Okunacak kullanıcının ID'si
     * @param callback Kullanıcı nesnesini işlemek için geri çağırım fonksiyonu
     */
    @Override
    public void read(int id, Consumer<User> callback) {
        var dbResponse = userRepository.read(User.class, UserQuery.READ_USER_BY_ID, List.of(id));
        securityLogger.logUserOperation(dbResponse.getUsername(), "kullanıcı okuma");
        callback.accept(dbResponse);
    }

    /**
     * Tüm kullanıcıları okur ve bir liste olarak döndürür.
     *
     * @return Tüm kullanıcıların listesi
     */
    public List<User> readAll() {
        return userRepository.readAll(User.class, UserQuery.READ_USERS, List.of());
    }

    /**
     * Belirtilen kullanıcıların yedeğini oluşturur.
     *
     * @param users  Yedeklenecek kullanıcıların listesi
     * @param window Yedekleme işlemi için kullanılacak pencere
     */
    @Override
    public void createBackup(List<User> users, Window window) {
        userBackup.export(users, window);
        //DB'ye backup verisini kaydet
        var entity = new JsonBackup(0,
                languageService.translate("new_backup")
                        .concat(String.format("- %s",
                                LocalDateTime.now().toString()
                        )),
                JsonBackupUtil.generateRawData(users),
                LocalDateTime.now(),
                0);
        jsonBackupService.create(entity);

    }

    /**
     * Yedek dosyasından kullanıcıları yükler.
     *
     * @param window Yedek yükleme işlemi için kullanılacak pencere
     * @return Yüklenen kullanıcıların listesi
     */
    @Override
    public List<User> loadBackup(Window window) {
        return userBackup.importBackup(window);
    }

    /**
     * Belirtilen ID ile tüm kullanıcıları okur. Bu yöntem kullanım dışı bırakılmıştır.
     *
     * @param id Kullanıcı ID'si
     * @return Kullanıcı listesi
     * @throws RuntimeException Bu yöntem kullanım dışıdır
     * @deprecated Kullanım dışı bırakılmıştır
     */
    @Deprecated
    @Override
    public List<User> readAll(int id) {
        throw new RuntimeException("readAll Disabled:" + getClass().getName());
    }

    /**
     * Belirtilen kullanıcıyı günceller ve sonucu bir geri çağırım fonksiyonuna iletir.
     *
     * @param entity   Güncellenecek kullanıcı nesnesi
     * @param callback Güncellenen kullanıcıyı işlemek için geri çağırım fonksiyonu
     */
    @Override

    public void update(User entity, Consumer<User> callback) {
        var user = userRepository.update(entity,
                UserQuery.UPDATE_USER_BY_ID,
                List.of(entity.getUsername(),
                        entity.getEmail(),
                        entity.getPassword(),
                        entity.isVerified(),
                        entity.isLocked(),
                        entity.getId(),
                        entity.getVersion()));
        callback.accept(user);
        securityLogger.logUserOperation(entity.toString(), "kullanıcı güncelleme");
    }

    /**
     * Belirtilen e-posta adresine sahip kullanıcıyı okur ve sonucu bir geri çağırım fonksiyonuna iletir.
     *
     * @param email    Okunacak kullanıcının e-posta adresi
     * @param callback Kullanıcı nesnesini işlemek için geri çağırım fonksiyonu
     */
    @Override
    public void readByEmail(String email, Consumer<User> callback) {
        var dbResponse = userRepository.read(User.class, UserQuery.READ_USER_BY_EMAIL, List.of(email));
        callback.accept(dbResponse);
        securityLogger.logUserOperation(email, "kullanıcı email adresine göre okuma");
    }

    /**
     * Birden fazla kullanıcıyı toplu olarak kaydeder veya günceller. Optimistik kilitleme ile veri tutarlılığını sağlar.
     *
     * @param users Kaydedilecek veya güncellenecek kullanıcıların listesi
     * @return Kaydedilen ve güncellenen kullanıcıların birleşik listesi
     * @throws OptimisticLockException Versiyon uyuşmazlığı durumunda
     */
    @Override

    public List<User> saveAll(List<User> users) {
        List<List<Object>> insertParamsList = new ArrayList<>();
        List<User> updatedUsers = new ArrayList<>();
        List<User> createdUsers = new ArrayList<>();

        List<User> existingUsers = userRepository.readAll(User.class, UserQuery.READ_USERS, List.of());

        for (User user : users) {
            User existingUser = findUserByEmail(existingUsers, user.getEmail());

            if (existingUser != null) {
                // Versiyon uyuşmazlıkları için tekrar deneme mekanizması
                int maxRetries = 5; // Sağlamlık için deneme sayısı artırıldı
                int attempt = 0;
                boolean isUpdated = false;

                while (attempt < maxRetries && !isUpdated) {
                    try {
                        // En güncel kullanıcıyı al, geçerli versiyonu sağla
                        User latestUser = userRepository.read(User.class, UserQuery.READ_USER_BY_EMAIL, List.of(user.getEmail()));
                        if (latestUser == null) {
                            securityLogger.logUserOperation(user.getEmail(), "Kullanıcı artık mevcut değil, yeni olarak işleniyor");
                            break; // Kullanıcı silindi, yeni olarak işle
                        }

                        // Kullanıcının versiyonunu en güncel hale getir
                        user.setVersion(latestUser.getVersion());
                        user.setPassword(BCrypt.hashpw(user.getPassword(), BCrypt.gensalt(12)));

                        // Güncellemeyi dene
                        var updatedUser = updateUser(user);
                        updatedUsers.add(updatedUser);
                        isUpdated = true;
                    } catch (OptimisticLockException e) {
                        attempt++;
                        securityLogger.logUserOperation(user.getEmail(),
                                String.format("Versiyon uyuşmazlığı, deneme %d/%d: %s", attempt, maxRetries, e.getMessage()));
                        if (attempt == maxRetries) {
                            throw new OptimisticLockException(
                                    String.format("Kullanıcı %s için optimistik kilitleme hatası, %d deneme sonrası: %s",
                                            user.getEmail(), maxRetries, e.getMessage()));
                        }
                        // Diğer işlemler tamamlanana kadar kısa bir gecikme
                        try {
                            Thread.sleep(100); // 100ms gecikme
                        } catch (InterruptedException ie) {
                            Thread.currentThread().interrupt();
                            throw new RuntimeException("Yeniden deneme gecikmesi sırasında kesildi", ie);
                        }
                    }
                }
            } else {
                // Yeni kullanıcı oluştur
                List<Object> params = List.of(
                        user.getUsername(),
                        user.getEmail(),
                        user.getPassword(),
                        user.getRole().toString(),
                        user.isVerified(),
                        user.isLocked()
                );
                insertParamsList.add(params);
                createdUsers.add(user);
            }
        }

        // Yeni kullanıcıları veritabanına kaydet
        if (!insertParamsList.isEmpty()) {
            userRepository.saveAll(UserQuery.CREATE_USER, insertParamsList);
            createdUsers.forEach(user -> securityLogger.logUserOperation(user.toString(), "Kullanıcı oluşturuldu"));
        }

        // Güncellenen kullanıcıları logla
        updatedUsers.forEach(user -> securityLogger.logUserOperation(user.toString(), "Kullanıcı güncellendi"));

        // Güncellenen ve oluşturulan kullanıcıları birleştir
        List<User> allUsers = new ArrayList<>();
        allUsers.addAll(updatedUsers);
        allUsers.addAll(createdUsers);

        return allUsers;
    }

    /**
     * Verilen e-posta adresine sahip kullanıcıyı mevcut kullanıcılar listesinden bulur.
     *
     * @param existingUsers Aranacak kullanıcıların listesi
     * @param email         Bulunacak kullanıcının e-posta adresi
     * @return E-posta adresine sahip kullanıcı veya null
     */
    private User findUserByEmail(List<User> existingUsers, String email) {
        for (User user : existingUsers) {
            if (user.getEmail().equals(email)) {
                return user;
            }
        }
        return null;
    }

    /**
     * Belirtilen e-posta adresine sahip bir kullanıcının var olup olmadığını kontrol eder.
     *
     * @param email Kontrol edilecek e-posta adresi
     * @return Kullanıcı varsa true, yoksa false
     */
    @Override
    public boolean isEmailExists(String email) {
        var dbResponse = userRepository.read(User.class, UserQuery.READ_USER_BY_EMAIL, List.of(email));
        return Objects.nonNull(dbResponse);
    }

    /**
     * Kullanıcıyı günceller ve şifresini şifreler.
     *
     * @param user Güncellenecek kullanıcı nesnesi
     * @return Güncellenen kullanıcı nesnesi
     */
    private User updateUser(User user) {
        user.setPassword(BCrypt.hashpw(user.getPassword(), BCrypt.gensalt(12)));
        var updated = userRepository.update(user, UserQuery.UPDATE_USER_BY_ID,
                List.of(user.getUsername(), user.getEmail(), user.getPassword(), user.isVerified(), user.isLocked(), user.getId(), user.getVersion()));
        return updated;
    }
}