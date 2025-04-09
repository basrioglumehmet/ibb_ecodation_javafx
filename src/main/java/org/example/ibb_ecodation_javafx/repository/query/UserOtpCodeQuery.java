package org.example.ibb_ecodation_javafx.repository.query;

import lombok.experimental.UtilityClass;

@UtilityClass
public class UserOtpCodeQuery {

    public static String CREATE_USER_OTP_CODE = "INSERT INTO user_otp_codes (user_id, otp) VALUES(?, ?)";

    public static String READ_USER_OTP_CODE_BY_USER_ID = "SELECT * FROM user_otp_codes WHERE user_id=?";
    public static String READ_BY_OTP_CODE = "SELECT * FROM user_otp_codes WHERE otp=?";

    public static String UPDATE_USER_OTP_CODE_BY_USER_ID = "UPDATE user_otp_codes " +
            "SET otp=?, "+
            "version=version+1 " +  // Eğer update işlemi yapılırsa version artır artık yeni hali var başkası tarafından güncellenmiştir.
            "WHERE user_id=? and version=?;";

    public static String DELETE_USER_OTP_BY_USER_ID = "DELETE FROM user_otp_codes where user_id = ?";
}
