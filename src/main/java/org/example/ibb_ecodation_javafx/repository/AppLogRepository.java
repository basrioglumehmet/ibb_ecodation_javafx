package org.example.ibb_ecodation_javafx.repository;

import org.example.ibb_ecodation_javafx.core.db.MsSqlConnection;
import org.example.ibb_ecodation_javafx.model.AppLog;
import org.example.ibb_ecodation_javafx.repository.base.BaseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("appLogRepository")
public class AppLogRepository extends BaseRepository<AppLog> {

    @Autowired
    public AppLogRepository(MsSqlConnection msSqlConnection) {
        super(msSqlConnection.connectToDatabase());
    }
}