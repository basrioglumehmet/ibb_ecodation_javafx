package org.example.ibb_ecodation_javafx.service;

import lombok.RequiredArgsConstructor;
import org.example.ibb_ecodation_javafx.core.db.EntityFilter;
import org.example.ibb_ecodation_javafx.core.service.GenericService;
import org.example.ibb_ecodation_javafx.model.UserNotification;
import org.example.ibb_ecodation_javafx.repository.UserNotificationRepository;
import org.example.ibb_ecodation_javafx.utils.TrayUtil;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class UserNotificationServiceImpl implements UserNotificationService {

    private final UserNotificationRepository userNotificationRepository;

    @Override
    public UserNotification save(UserNotification entity) {
        TrayUtil.showTrayNotification(entity.getDescription(), entity.getHeader());
        return userNotificationRepository.save(entity);
    }

    @Override
    public void update(UserNotification entity) {
        userNotificationRepository.update(entity);
    }

    @Override
    public Optional<UserNotification> findById(Integer integer) {
        return userNotificationRepository.findById(integer);
    }

    @Override
    public List<UserNotification> findAll() {
        return userNotificationRepository.findAll();
    }

    @Override
    public List<UserNotification> findAllById(Integer id) {
        return userNotificationRepository.findAllById(id);
    }

    @Override
    public List<UserNotification> findAllByFilter(List<EntityFilter> filters) {
        return List.of();
    }

    @Override
    public void delete(Integer integer) {
        userNotificationRepository.delete(integer);
    }

    @Override
    public Optional<UserNotification> findFirstByFilter(List<EntityFilter> filters) {
        return userNotificationRepository.findFirstByFilter(filters);
    }

}
