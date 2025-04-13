package org.example.ibb_ecodation_javafx.repository;

import org.example.ibb_ecodation_javafx.core.db.MsSqlConnection;
import org.example.ibb_ecodation_javafx.model.UserNote;
import org.example.ibb_ecodation_javafx.repository.base.BaseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UserNoteRepository extends BaseRepository<UserNote> {

    @Autowired
    public UserNoteRepository(MsSqlConnection msSqlConnection) {
        super(msSqlConnection.connectToDatabase());
    }
}