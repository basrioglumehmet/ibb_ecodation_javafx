package org.example.ibb_ecodation_javafx.service.impl;

import lombok.AllArgsConstructor;
import org.example.ibb_ecodation_javafx.core.logger.SecurityLogger;
import org.example.ibb_ecodation_javafx.mapper.UserMapper;
import org.example.ibb_ecodation_javafx.model.User;
import org.example.ibb_ecodation_javafx.repository.UserRepository;
import org.example.ibb_ecodation_javafx.repository.query.UserQuery;
import org.example.ibb_ecodation_javafx.service.UserService;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

@AllArgsConstructor
@Service
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    private final UserRepository userRepository;
    private final SecurityLogger securityLogger;

    @Override
    public User create(User entity) {
        entity.setPassword(BCrypt.hashpw(entity.getPassword(), BCrypt.gensalt(12)));
        // var entity = new User(1,"Mehmet Basrioğlu","admin@admin.com","123456", Role.ADMIN,true,false,0);
        var created = userRepository.create(entity, UserQuery.CREATE_USER,
                List.of(entity.getUsername(),entity.getEmail(),entity.getPassword(),entity.getRole().toString(),entity.isVerified(),entity.isLocked()));
        securityLogger.logUserOperation(entity.toString(), "kullanıcı oluşturma");
        return created;
    }

    @Override
    public void delete(int id) {
    }

    @Override
    public void read(int id, Consumer<User> callback) {
        var dbResponse = userRepository.read(User.class, UserQuery.READ_USER_BY_ID, List.of(id));
        securityLogger.logUserOperation(dbResponse.getUsername(), "kullanıcı okuma");
        callback.accept(dbResponse);
    }

    @Override
    public List<User> readAll(int id) {
        return List.of();
    }


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
        securityLogger.logUserOperation(entity.toString(), "kullanıcı güncelleme");
    }

    @Override
    public void readByEmail(String email, Consumer<User> callback) {
        var dbResponse = userRepository.read(User.class, UserQuery.READ_USER_BY_EMAIL, List.of(email));
        callback.accept(dbResponse);
        securityLogger.logUserOperation(email, "kullanıcı email adresine göre okuma");
    }

    @Override
    public boolean isEmailExists(String email) {
        var dbResponse = userRepository.read(User.class, UserQuery.READ_USER_BY_EMAIL, List.of(email));
        return Objects.nonNull(dbResponse);
    }
}
