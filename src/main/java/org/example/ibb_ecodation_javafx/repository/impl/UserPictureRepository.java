package org.example.ibb_ecodation_javafx.repository.impl;

import org.example.ibb_ecodation_javafx.core.repository.impl.GenericRepositoryImpl;
import org.example.ibb_ecodation_javafx.model.UserOtpCode;
import org.example.ibb_ecodation_javafx.model.UserPicture;
import org.example.ibb_ecodation_javafx.repository.query.UserPictureQuery;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;

@Repository
public class UserPictureRepository extends GenericRepositoryImpl<UserPicture, Integer> implements org.example.ibb_ecodation_javafx.repository.UserPictureRepository {

    public UserPictureRepository(DataSource dataSource, Environment environment) {
        super(dataSource, environment);
    }

    @Override
    protected String getTableName() {
        return "user_pictures";
    }

    @Override
    protected RowMapper<UserPicture> getRowMapper() {
        return new UserPictureRowMapper();
    }

    @Override
    protected String getInsertSql() {
        return UserPictureQuery.CREATE_USER_PICTURE;
    }

    @Override
    protected String getUpdateSql() {
        return UserPictureQuery.UPDATE_USER_PICTURE_BY_USER_ID;
    }

    @Override
    protected String getSelectByIdSql() {
        return UserPictureQuery.READ_USER_PICTURE_BY_USER_ID;
    }

    @Override
    protected String getSelectAllByIdSql() {
        return "";
    }

    @Override
    protected String getSelectAllSql() {
        return UserPictureQuery.READ_ALL;
    }

    @Override
    protected String getDeleteSql() {
        return UserPictureQuery.DELETE_BY_USER_ID;
    }

    @Override
    protected Object[] getInsertParams(UserPicture entity) {
        return new Object[]{
                entity.getUserId(),
                entity.getImageData(),
                entity.getVersion()
        };
    }

    @Override
    protected Object[] getUpdateParams(UserPicture entity) {
        return new Object[]{
                entity.getImageData(),
                entity.getUserId(),
        };
    }

    @Override
    protected Integer getVersion(UserPicture entity) {
        return entity.getVersion();
    }

    private static class UserPictureRowMapper implements RowMapper<UserPicture> {
        @Override
        public UserPicture mapRow(ResultSet rs, int rowNum) throws SQLException {
            UserPicture userPicture = new UserPicture();
            userPicture.setUserId(rs.getInt("user_id"));
            userPicture.setImageData(rs.getBytes("image_data"));
            userPicture.setVersion(rs.getInt("version"));
            return userPicture;
        }
    }
}
