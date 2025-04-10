package org.example.ibb_ecodation_javafx.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.ibb_ecodation_javafx.core.logger.SecurityLogger;
import org.example.ibb_ecodation_javafx.model.UserNotification;
import org.example.ibb_ecodation_javafx.model.UserOtpCode;
import org.example.ibb_ecodation_javafx.repository.UserNotificationRepository;
import org.example.ibb_ecodation_javafx.repository.query.UserNotificationQuery;
import org.example.ibb_ecodation_javafx.repository.query.UserOtpCodeQuery;
import org.example.ibb_ecodation_javafx.service.UserNotificationService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.function.Consumer;

import static org.example.ibb_ecodation_javafx.utils.TrayUtil.showTrayNotification;

@RequiredArgsConstructor
@Service
public class UserNotificationServiceImpl implements UserNotificationService {

    private final UserNotificationRepository userNotificationRepository;
    private final SecurityLogger securityLogger;

    @Override
    public UserNotification create(UserNotification entity) {
        //user_id, header, description, type, version
        showTrayNotification(entity.getDescription(), entity.getHeader());
        securityLogger.logUserOperation(entity.getUserId()+"(USER ID)", "bildirim oluşturma");
        return userNotificationRepository.create(entity, UserNotificationQuery.CREATE, List.of(entity.getUserId(), entity.getHeader(),
                entity.getDescription(),entity.getType(),entity.getVersion()));
    }

    @Override
    public void delete(int id) {
        userNotificationRepository.delete(UserNotificationQuery.DELETE_BY_ID, List.of(id));
    }

    @Override
    public void read(int id, Consumer<UserNotification> callback) {
        callback.accept(userNotificationRepository.read(UserNotification.class,UserNotificationQuery.READ_BY_ID,List.of(id)));
    }

    @Override
    public List<UserNotification> readAll(int id) {
        securityLogger.logUserOperation(id+"(USER ID)", "bildirim okuma");
        return userNotificationRepository.readAll(UserNotification.class,UserNotificationQuery.READ_ALL_BY_USER_ID,List.of(id));
    }

    @Deprecated
    @Override
    public void update(UserNotification entity, Consumer<UserNotification> callback) {
        throw new RuntimeException("Notification update is disabled.");
    }
}
