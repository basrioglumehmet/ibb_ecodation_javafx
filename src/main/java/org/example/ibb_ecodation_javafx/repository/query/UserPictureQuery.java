package org.example.ibb_ecodation_javafx.repository.query;

import lombok.experimental.UtilityClass;

@UtilityClass
public class UserPictureQuery {

    public static String CREATE_USER_PICTURE = "INSERT INTO user_pictures (user_id, image_data) VALUES(?, ?)";

    public static String READ_USER_PICTURE_BY_USER_ID = "SELECT * FROM user_pictures WHERE user_id=?";

    public static String UPDATE_USER_PICTURE_BY_USER_ID = "UPDATE user_pictures " +
            "SET image_data=?, "+
            "version=version+1 " +  // Eğer update işlemi yapılırsa version artır artık yeni hali var başkası tarafından güncellenmiştir.
            "WHERE user_id=? and version=?;";
}
