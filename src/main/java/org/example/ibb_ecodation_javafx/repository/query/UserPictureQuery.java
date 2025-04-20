package org.example.ibb_ecodation_javafx.repository.query;

import lombok.experimental.UtilityClass;

@UtilityClass
public class UserPictureQuery {

    public static String CREATE_USER_PICTURE = "INSERT INTO user_pictures (user_id, image_data,version) VALUES(?, ?,?)";
    public static String READ_ALL = "SELECT * FROM user_pictures";
    public static String READ_USER_PICTURE_BY_USER_ID = "SELECT * FROM user_pictures WHERE user_id=?";
    public static String DELETE_BY_USER_ID = "DELETE FROM user_pictures " +
            "WHERE user_id=?;";
    public static String UPDATE_USER_PICTURE_BY_USER_ID = "UPDATE user_pictures " +
            "SET image_data=?, "+
            "version=version+1 " +
            "WHERE user_id=? ";
}
