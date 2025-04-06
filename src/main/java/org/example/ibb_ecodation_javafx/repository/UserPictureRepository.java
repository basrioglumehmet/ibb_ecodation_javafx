package org.example.ibb_ecodation_javafx.repository;

import org.example.ibb_ecodation_javafx.core.db.MsSqlConnection;
import org.example.ibb_ecodation_javafx.model.UserPicture;
import org.example.ibb_ecodation_javafx.repository.base.BaseRepository;
import org.springframework.stereotype.Component;

@Component("userPictureRepository")
public class UserPictureRepository extends BaseRepository<UserPicture> {
    public UserPictureRepository() {
        super(MsSqlConnection.getInstance().connectToDatabase());
    }
}
