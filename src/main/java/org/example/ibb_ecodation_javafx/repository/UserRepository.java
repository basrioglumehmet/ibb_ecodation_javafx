package org.example.ibb_ecodation_javafx.repository;

import org.example.ibb_ecodation_javafx.core.context.SpringContext;
import org.example.ibb_ecodation_javafx.core.db.MsSqlConnection;
import org.example.ibb_ecodation_javafx.core.logger.SecurityLogger;
import org.example.ibb_ecodation_javafx.model.User;
import org.example.ibb_ecodation_javafx.repository.base.BaseRepository;
import org.springframework.stereotype.Component;


@Component("userRepository")
public class UserRepository extends BaseRepository<User> {

    public UserRepository() {
        super(MsSqlConnection.getInstance().connectToDatabase());
    }
}
