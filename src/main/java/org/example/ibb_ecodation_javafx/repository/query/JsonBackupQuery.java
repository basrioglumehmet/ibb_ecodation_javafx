package org.example.ibb_ecodation_javafx.repository.query;

import lombok.experimental.UtilityClass;

@UtilityClass
public class JsonBackupQuery {

    public static String CREATE_JSON_BACKUP = "INSERT INTO json_backup " +
            "(header, json_data, created_at, version) " +
            "VALUES (?, ?, ?, ?)";

    public static String READ_JSON_BACKUP_BY_ID = "SELECT * FROM json_backup WHERE id = ?";

    public static String READ_ALL_JSON_BACKUPS = "SELECT * FROM json_backup";

    public static String UPDATE_JSON_BACKUP_BY_ID = "UPDATE json_backup SET " +
            "header = ?, json_data = ?, created_at = ?, version = version + 1";

    public static String DELETE_JSON_BACKUP_BY_ID = "DELETE FROM json_backup WHERE id = ?";
}
