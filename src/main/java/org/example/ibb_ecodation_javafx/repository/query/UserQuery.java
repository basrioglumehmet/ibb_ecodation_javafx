package org.example.ibb_ecodation_javafx.repository.query;

import lombok.experimental.UtilityClass;

@UtilityClass
public class UserQuery {
    public static String CREATE_USER = "INSERT INTO users (username, email, password, role, is_verified, is_locked) VALUES(?,?,?,?,?,?)";
    public static String READ_USER_BY_ID = "SELECT * FROM users WHERE id=?";
    public static String READ_USER_BY_EMAIL = "SELECT * FROM users WHERE email=?";
    public static String UPDATE_USER_BY_ID = "UPDATE users " +
            "SET username=?, " +
            "email=?, " +
            "password=?, " +
            "is_verified=?, " +
            "is_locked=?, " +  // Add a comma here to separate the columns
            "version=version+1 " +  // Correct syntax for version increment
            "WHERE id=? and version = ?;";


}
