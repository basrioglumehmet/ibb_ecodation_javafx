package org.example.ibb_ecodation_javafx.repository;

import org.example.ibb_ecodation_javafx.core.db.MsSqlConnection;
import org.example.ibb_ecodation_javafx.model.JsonBackup;
import org.example.ibb_ecodation_javafx.repository.base.BaseRepository;
import org.springframework.stereotype.Component;

@Component("jsonBackupRepository")
public class JsonBackupRepository extends BaseRepository<JsonBackup> {

    public JsonBackupRepository() {
        super(MsSqlConnection.getInstance().connectToDatabase());
    }
}
