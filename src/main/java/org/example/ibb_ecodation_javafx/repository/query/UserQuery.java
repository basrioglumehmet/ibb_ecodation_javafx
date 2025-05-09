package org.example.ibb_ecodation_javafx.repository.query;

import lombok.experimental.UtilityClass;

@UtilityClass
public class UserQuery {
    public static String CREATE_USER = "INSERT INTO users (username, email, password, role, is_verified, is_locked) VALUES(?,?,?,?,?,?)";
    public static String READ_USER_BY_ID = "SELECT * FROM users WHERE id=?";
    public static String READ_USER_BY_EMAIL = "SELECT * FROM users WHERE email=?";
    public static String READ_USERS = "SELECT * FROM users";
    public static String IS_EMAIL_EXISTS = " SELECT TOP 1 * FROM users WHERE email=?";
    /*
          entity.getUsername(),
                entity.getEmail(),
                entity.getPassword(),
                entity.getRole().name(),
                entity.isVerified(),
                entity.isLocked(),
                entity.getId(),
                entity.getVersion() // Version for WHERE clause
     */
    public static String UPDATE_USER_BY_ID = "UPDATE users " +
            "SET username=?, " +
            "email=?, " +
            "password=?, " +
            "role=?, "+
            "is_verified=?, " +
            "is_locked=?, " +
            "version=version+1 " +
            "WHERE id=?";

    public static String DELETE_BY_ID = "DELETE FROM users WHERE id=?";


}
