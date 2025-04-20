package org.example.ibb_ecodation_javafx.repository.impl;

import org.example.ibb_ecodation_javafx.core.repository.impl.GenericRepositoryImpl;
import org.example.ibb_ecodation_javafx.model.AppLog;
import org.example.ibb_ecodation_javafx.repository.AppLogRepository;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

@Repository
public class AppLogRepositoryImpl extends GenericRepositoryImpl<AppLog, Integer> implements AppLogRepository {

    public AppLogRepositoryImpl(DataSource dataSource, Environment environment) {
        super(dataSource, environment);
    }

    @Override
    protected String getTableName() {
        return "app_logs";
    }

    @Override
    protected RowMapper<AppLog> getRowMapper() {
        return new AppLogRowMapper();
    }

    @Override
    protected String getInsertSql() {
        return "INSERT INTO app_logs (description, ip_addresses, computer_name, at_time, version) VALUES (?, ?, ?, ?, ?)";
    }

    @Override
    protected String getUpdateSql() {
        return "UPDATE app_logs SET description = ?, ip_addresses = ?, computer_name = ?, at_time = ?, version = version + 1";
    }

    @Override
    protected String getSelectByIdSql() {
        return "SELECT * FROM app_logs WHERE id = ?";
    }

    @Override
    protected String getSelectAllByIdSql() {
        return "";
    }

    @Override
    protected String getSelectAllSql() {
        return "SELECT * FROM app_logs";
    }

    @Override
    protected String getDeleteSql() {
        return "DELETE FROM app_logs WHERE id = ?";
    }

    @Override
    protected Object[] getInsertParams(AppLog entity) {
        return new Object[]{
                entity.getDescription(),
                entity.getIpAddresses(),
                entity.getComputerName(),
                entity.getAtTime() != null ? entity.getAtTime() : new Timestamp(System.currentTimeMillis()),
                0 // Initial version for new entities
        };
    }

    @Override
    protected Object[] getUpdateParams(AppLog entity) {
        return new Object[]{
                entity.getDescription(),
                entity.getIpAddresses(),
                entity.getComputerName(),
                entity.getAtTime() != null ? entity.getAtTime() : new Timestamp(System.currentTimeMillis()),
                entity.getId()
        };
    }

    @Override
    protected Integer getVersion(AppLog entity) {
        return entity.getVersion(); // Return the actual version from the entity
    }

    private static class AppLogRowMapper implements RowMapper<AppLog> {
        @Override
        public AppLog mapRow(ResultSet rs, int rowNum) throws SQLException {
            AppLog appLog = new AppLog();
            appLog.setId(rs.getInt("id"));
            appLog.setDescription(rs.getString("description"));
            appLog.setIpAddresses(rs.getString("ip_addresses"));
            appLog.setComputerName(rs.getString("computer_name"));
            appLog.setAtTime(rs.getTimestamp("at_time"));
            appLog.setVersion(rs.getInt("version"));
            return appLog;
        }
    }
}