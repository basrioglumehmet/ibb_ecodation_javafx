package org.example.ibb_ecodation_javafx.repository.impl;

import org.example.ibb_ecodation_javafx.core.repository.impl.GenericRepositoryImpl;
import org.example.ibb_ecodation_javafx.model.AppLog;
import org.example.ibb_ecodation_javafx.model.UserOtpCode;
import org.example.ibb_ecodation_javafx.repository.UserOtpCodeRepository;
import org.example.ibb_ecodation_javafx.repository.query.UserOtpCodeQuery;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;

@Repository
public class UserOtpCodeRepositoryImpl extends GenericRepositoryImpl<UserOtpCode, Integer> implements UserOtpCodeRepository {
    public UserOtpCodeRepositoryImpl(DataSource dataSource, Environment environment) {
        super(dataSource, environment);
    }

    @Override
    protected String getTableName() {
        return "user_otp_codes";
    }

    @Override
    protected RowMapper<UserOtpCode> getRowMapper() {
        return new UserOtpCodeRowMapper();
    }

    @Override
    protected String getInsertSql() {
        return UserOtpCodeQuery.CREATE_USER_OTP_CODE;
    }

    @Override
    protected String getUpdateSql() {
        return UserOtpCodeQuery.UPDATE;
    }

    @Override
    protected String getSelectByIdSql() {
        return UserOtpCodeQuery.READ_USER_OTP_CODE_BY_ID;
    }

    @Override
    protected String getSelectAllByIdSql() {
        return "";
    }

    @Override
    protected String getSelectAllSql() {
        return UserOtpCodeQuery.READ_ALL;
    }

    @Override
    protected String getDeleteSql() {
        return UserOtpCodeQuery.DELETE_USER_OTP_BY_USER_ID;
    }

    @Override
    protected Object[] getInsertParams(UserOtpCode entity) {
        return new Object[]{
                entity.getUserId(),
                entity.getOtpCode()
        };
    }

    @Override
    protected Object[] getUpdateParams(UserOtpCode entity) {
        return new Object[]{
                entity.getOtpCode(),
                entity.getUserId(),
        };
    }

    @Override
    protected Integer getVersion(UserOtpCode entity) {
        return entity.getVersion();
    }

    private static class UserOtpCodeRowMapper implements RowMapper<UserOtpCode> {
        @Override
        public UserOtpCode mapRow(ResultSet rs, int rowNum) throws SQLException {
            UserOtpCode userOtpCode = new UserOtpCode();
            userOtpCode.setOtpCode(rs.getString("otp"));
            userOtpCode.setUserId(rs.getInt("user_id"));
            userOtpCode.setVersion(rs.getInt("version"));
            return userOtpCode;
        }
    }
}
