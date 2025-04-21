package org.example.ibb_ecodation_javafx.service;

import javafx.stage.Window;
import lombok.RequiredArgsConstructor;
import org.example.ibb_ecodation_javafx.backup.UserBackup;
import org.example.ibb_ecodation_javafx.core.db.EntityFilter;
import org.example.ibb_ecodation_javafx.model.User;
import org.example.ibb_ecodation_javafx.repository.UserRepository;
import org.example.ibb_ecodation_javafx.repository.preparator.UserInsertPreparator;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserBackup userBackup;


    @Override
    public User save(User entity) {
        return userRepository.save(entity);
    }

    @Override
    public void update(User entity) {
        userRepository.update(entity);
    }

    @Override
    public void saveAll(List<User> entities) {
        userRepository.saveAll(entities, new UserInsertPreparator());
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

    @Override
    public Optional<User> findById(Integer integer) {
        return userRepository.findById(integer);
    }

    @Override
    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Override
    public List<User> findAllById(Integer id) {
        return userRepository.findAllById(id);
    }

    @Override
    public List<User> findAllByFilter(List<EntityFilter> filters) {
        return userRepository.findAllByFilter(filters);
    }

    @Override
    public void delete(Integer integer) {
        userRepository.delete(integer);
    }

    @Override
    public Optional<User> findFirstByFilter(List<EntityFilter> filters) {
        return userRepository.findFirstByFilter(filters);
    }
}
