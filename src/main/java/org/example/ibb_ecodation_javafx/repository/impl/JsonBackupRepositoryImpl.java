package org.example.ibb_ecodation_javafx.repository.impl;

import org.example.ibb_ecodation_javafx.core.repository.impl.GenericRepositoryImpl;
import org.example.ibb_ecodation_javafx.model.AppLog;
import org.example.ibb_ecodation_javafx.model.JsonBackup;
import org.example.ibb_ecodation_javafx.repository.JsonBackupRepository;
import org.example.ibb_ecodation_javafx.repository.query.JsonBackupQuery;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Repository
public class JsonBackupRepositoryImpl extends GenericRepositoryImpl<JsonBackup, Integer> implements JsonBackupRepository {


    public JsonBackupRepositoryImpl(DataSource dataSource, Environment environment) {
        super(dataSource, environment);
    }

    @Override
    protected String getTableName() {
        return "json_backup";
    }

    @Override
    protected RowMapper<JsonBackup> getRowMapper() {
        return new JsonBackupRowMapper();
    }

    @Override
    protected String getInsertSql() {
        return JsonBackupQuery.CREATE_JSON_BACKUP;
    }

    @Override
    protected String getUpdateSql() {
        return JsonBackupQuery.UPDATE_JSON_BACKUP_BY_ID;
    }

    @Override
    protected String getSelectByIdSql() {
        return JsonBackupQuery.READ_JSON_BACKUP_BY_ID;
    }

    @Override
    protected String getSelectAllByIdSql() {
        return "";
    }

    @Override
    protected String getSelectAllSql() {
        return JsonBackupQuery.READ_ALL_JSON_BACKUPS;
    }

    @Override
    protected String getDeleteSql() {
        return JsonBackupQuery.DELETE_JSON_BACKUP_BY_ID;
    }

    @Override
    protected Object[] getInsertParams(JsonBackup entity) {
        return new Object[]{
                entity.getHeader(),
                entity.getJsonData(),
                entity.getCreatedAt(),
                entity.getVersion()
        };
    }

    @Override
    protected Object[] getUpdateParams(JsonBackup entity) {
        return new Object[]{
                entity.getHeader(),
                entity.getJsonData(),
                entity.getCreatedAt(),
                entity.getVersion()
        };
    }

    @Override
    protected Integer getVersion(JsonBackup entity) {
        return entity.getVersion();
    }


    //Inner class
    private static class JsonBackupRowMapper implements RowMapper<JsonBackup> {
        @Override
        public JsonBackup mapRow(ResultSet rs, int rowNum) throws SQLException {
            JsonBackup jsonBackup = new JsonBackup();
            jsonBackup.setId(rs.getInt("id"));
            jsonBackup.setHeader(rs.getString("header"));
            jsonBackup.setJsonData(rs.getString("json_data"));
            jsonBackup.setCreatedAt(rs.getTimestamp("created_at"));
            jsonBackup.setVersion(rs.getInt("version"));
            return jsonBackup;
        }
    }
}
