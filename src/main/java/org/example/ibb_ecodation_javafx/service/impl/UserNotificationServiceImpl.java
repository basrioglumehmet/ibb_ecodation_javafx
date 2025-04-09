package org.example.ibb_ecodation_javafx.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.ibb_ecodation_javafx.model.UserNotification;
import org.example.ibb_ecodation_javafx.repository.UserNotificationRepository;
import org.example.ibb_ecodation_javafx.repository.query.UserOtpCodeQuery;
import org.example.ibb_ecodation_javafx.service.UserNotificationService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.function.Consumer;

@RequiredArgsConstructor
@Service
public class UserNotificationServiceImpl implements UserNotificationService {

    private final UserNotificationRepository userNotificationRepository;

    @Override
    public UserNotification create(UserNotification entity) {
        return userNotificationRepository.create(entity, UserOtpCodeQuery.CREATE_USER_OTP_CODE, List.of(entity.getUserId(), entity.getOtpCode()));
    }

    @Override
    public void delete(int id) {

    }

    @Override
    public void read(int id, Consumer<UserNotification> callback) {

    }

    @Override
    public void update(UserNotification entity, Consumer<UserNotification> callback) {

    }
}
