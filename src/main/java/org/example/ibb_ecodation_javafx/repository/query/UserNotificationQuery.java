package org.example.ibb_ecodation_javafx.repository.query;

import lombok.experimental.UtilityClass;

@UtilityClass
public class UserNotificationQuery {

    public static String CREATE = "INSERT INTO user_notifications (user_id, header, description, type, version) " +
            "VALUES (?, ?, ?, ?, ?)";

    public static String READ_ALL = "SELECT * FROM user_notifications";
    public static String READ_ALL_BY_USER_ID = "SELECT * FROM user_notifications WHERE user_id = ?";

    public static String UPDATE_BY_ID_AND_VERSION = "UPDATE user_notifications " +
            "SET user_id = ?, header = ?, description = ?, type = ?, version = version + 1 ";

    public static String DELETE_BY_ID = "DELETE FROM user_notifications WHERE id = ?";
}
