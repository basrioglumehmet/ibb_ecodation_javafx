package org.example.ibb_ecodation_javafx.repository;

import org.example.ibb_ecodation_javafx.core.db.MsSqlConnection;
import org.example.ibb_ecodation_javafx.model.UserNotification;
import org.example.ibb_ecodation_javafx.repository.base.BaseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("userNotificationRepository")
public class UserNotificationRepository extends BaseRepository<UserNotification> {

    @Autowired
    public UserNotificationRepository(MsSqlConnection msSqlConnection) {
        super(msSqlConnection.connectToDatabase());
    }
}