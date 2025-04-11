package org.example.ibb_ecodation_javafx.repository;

import org.example.ibb_ecodation_javafx.core.db.MsSqlConnection;
import org.example.ibb_ecodation_javafx.model.UserNote;
import org.example.ibb_ecodation_javafx.repository.base.BaseRepository;


public class UserNoteRepository extends BaseRepository<UserNote> {
    public UserNoteRepository() {
        super(MsSqlConnection.getInstance().connectToDatabase());
    }
}
