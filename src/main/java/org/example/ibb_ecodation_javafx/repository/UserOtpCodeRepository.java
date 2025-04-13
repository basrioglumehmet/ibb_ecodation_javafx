package org.example.ibb_ecodation_javafx.repository;

import org.example.ibb_ecodation_javafx.core.db.MsSqlConnection;
import org.example.ibb_ecodation_javafx.model.UserOtpCode;
import org.example.ibb_ecodation_javafx.repository.base.BaseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("userOtpCodeRepository")
public class UserOtpCodeRepository extends BaseRepository<UserOtpCode> {

    @Autowired
    public UserOtpCodeRepository(MsSqlConnection msSqlConnection) {
        super(msSqlConnection.connectToDatabase());
    }
}