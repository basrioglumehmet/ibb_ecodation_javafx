package org.example.ibb_ecodation_javafx.repository.impl;

import org.example.ibb_ecodation_javafx.core.repository.impl.GenericRepositoryImpl;
import org.example.ibb_ecodation_javafx.model.User;
import org.example.ibb_ecodation_javafx.model.enums.Role;
import org.example.ibb_ecodation_javafx.repository.UserRepository;
import org.example.ibb_ecodation_javafx.repository.query.UserQuery;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;

@Repository("userRepositoryImpl")
public class UserRepositoryImpl extends GenericRepositoryImpl<User, Integer> implements UserRepository {

    public UserRepositoryImpl(DataSource dataSource, Environment environment) {
        super(dataSource, environment);
    }

    @Override
    protected String getTableName() {
        return "users";
    }

    @Override
    protected RowMapper<User> getRowMapper() {
        return new UserRowMapper();
    }

    @Override
    protected String getInsertSql() {
        return UserQuery.CREATE_USER;
    }

    @Override
    protected String getUpdateSql() {
        return UserQuery.UPDATE_USER_BY_ID;
    }

    @Override
    protected String getSelectByIdSql() {
        return "SELECT * FROM users WHERE id = ?";
    }

    @Deprecated
    @Override
    protected String getSelectAllByIdSql() {
        //Sistem altyapısında kullanıcılara ait başka kullanıcı olmadığından burası deprecated yapıdadır.
        return null;
    }

    @Override
    protected String getSelectAllSql() {
        return "SELECT * FROM users";
    }

    @Override
    protected String getDeleteSql() {
        return "DELETE FROM users WHERE id = ?";
    }

    @Override
    protected Object[] getInsertParams(User entity) {
        //INSERT INTO users (username, email, password, role, is_verified, is_locked) VALUES(?,?,?,?,?,?)
        return new Object[]{
                entity.getUsername(),
                entity.getEmail(),
                entity.getPassword(),
                entity.getRole().name(),
                entity.isVerified(),
                entity.isLocked()
        };
    }
//SQL [UPDATE users SET username=?, email=?, password=?, role=?, is_verified=?,
// is_locked=?, version=version+1 WHERE id=? AND version = ?]; The index 8 is out of range
    @Override
    protected Object[] getUpdateParams(User entity) {
        return new Object[]{
                entity.getUsername(),
                entity.getEmail(),
                entity.getPassword(),
                entity.getRole().name(),
                entity.isVerified(),
                entity.isLocked(),
                entity.getId()
        };
    }

    @Override
    protected Integer getVersion(User entity) {
        return entity.getVersion();
    }

    private static class UserRowMapper implements RowMapper<User> {
        @Override
        public User mapRow(ResultSet rs, int rowNum) throws SQLException {
            User user = new User();
            user.setId(rs.getInt("id"));
            user.setUsername(rs.getString("username"));
            user.setEmail(rs.getString("email"));
            user.setPassword(rs.getString("password"));
            user.setRole(Role.valueOf(rs.getString("role")));
            user.setVerified(rs.getBoolean("is_verified"));
            user.setLocked(rs.getBoolean("is_locked"));
            user.setVersion(rs.getInt("version"));
            return user;
        }
    }
}