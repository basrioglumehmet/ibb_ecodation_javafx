package org.example.ibb_ecodation_javafx.repository.impl;

import org.example.ibb_ecodation_javafx.core.repository.impl.GenericRepositoryImpl;
import org.example.ibb_ecodation_javafx.model.UserNote;
import org.example.ibb_ecodation_javafx.model.UserNotification;
import org.example.ibb_ecodation_javafx.repository.UserNotificationRepository;
import org.example.ibb_ecodation_javafx.repository.query.UserNoteQuery;
import org.example.ibb_ecodation_javafx.repository.query.UserNotificationQuery;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;

@Repository
public class UserNotificationRepositoryImpl extends GenericRepositoryImpl<UserNotification, Integer> implements UserNotificationRepository {
    public UserNotificationRepositoryImpl(DataSource dataSource, Environment environment) {
        super(dataSource, environment);
    }

    @Override
    protected String getTableName() {
        return "user_notifications";
    }

    @Override
    protected RowMapper<UserNotification> getRowMapper() {
        return new UserNotificationRowMapper();
    }

    @Override
    protected String getInsertSql() {
        return UserNotificationQuery.CREATE;
    }

    @Override
    protected String getUpdateSql() {
        return UserNotificationQuery.UPDATE_BY_ID_AND_VERSION;
    }

    @Override
    protected String getSelectByIdSql() {
        return null;
    }

    @Override
    protected String getSelectAllByIdSql() {
        return UserNotificationQuery.READ_ALL_BY_USER_ID;
    }

    @Override
    protected String getSelectAllSql() {
        return UserNotificationQuery.READ_ALL;
    }

    @Override
    protected String getDeleteSql() {
        return UserNotificationQuery.DELETE_BY_ID;
    }
    //user_id, header, description, type, version
    @Override
    protected Object[] getInsertParams(UserNotification entity) {
        return new Object[]{
                entity.getUserId(),
                entity.getHeader(),
                entity.getDescription(),
                entity.getType(),
                entity.getVersion()
        };
    }

    @Override
    protected Object[] getUpdateParams(UserNotification entity) {
        return new Object[]{
                entity.getUserId(),
                entity.getHeader(),
                entity.getDescription(),
                entity.getType(),
                entity.getVersion()
        };
    }

    @Override
    protected Integer getVersion(UserNotification entity) {
        return entity.getVersion();
    }

    //Inner class
    private static class UserNotificationRowMapper implements RowMapper<UserNotification> {
        @Override
        public UserNotification mapRow(ResultSet rs, int rowNum) throws SQLException {
            UserNotification notification = new UserNotification();
            notification.setId(rs.getInt("id"));
            notification.setUserId(rs.getInt("user_id"));
            notification.setHeader(rs.getString("header"));
            notification.setDescription(rs.getString("description"));
            notification.setType(rs.getString("type"));
            notification.setVersion(rs.getInt("version"));
            return notification;
        }
    }
}
