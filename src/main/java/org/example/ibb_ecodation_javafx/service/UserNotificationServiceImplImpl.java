package org.example.ibb_ecodation_javafx.service;

import org.example.ibb_ecodation_javafx.core.db.EntityFilter;
import org.example.ibb_ecodation_javafx.model.UserNotification;
import org.example.ibb_ecodation_javafx.repository.UserNotificationRepository;

import java.util.List;

public class UserNotificationServiceImplImpl extends UserNotificationServiceImpl {
    public UserNotificationServiceImplImpl(UserNotificationRepository userNotificationRepository) {
        super(userNotificationRepository);
    }

    @Override
    public List<UserNotification> findAllByFilter(List<EntityFilter> filters) {
        return List.of();
    }
}
