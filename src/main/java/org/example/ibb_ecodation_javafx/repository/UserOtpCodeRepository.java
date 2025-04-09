package org.example.ibb_ecodation_javafx.repository;

import org.example.ibb_ecodation_javafx.core.db.MsSqlConnection;
import org.example.ibb_ecodation_javafx.model.UserOtpCode;
import org.example.ibb_ecodation_javafx.repository.base.BaseRepository;
import org.springframework.stereotype.Component;

@Component("userOtpCodeRepository")
public class UserOtpCodeRepository extends BaseRepository<UserOtpCode> {
    public UserOtpCodeRepository() {
        super(MsSqlConnection.getInstance().connectToDatabase());
    }
}
