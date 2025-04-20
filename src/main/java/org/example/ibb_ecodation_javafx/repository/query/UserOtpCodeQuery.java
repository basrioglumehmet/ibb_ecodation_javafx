package org.example.ibb_ecodation_javafx.repository.query;

import lombok.experimental.UtilityClass;

@UtilityClass
public class UserOtpCodeQuery {

    public static String CREATE_USER_OTP_CODE = "INSERT INTO user_otp_codes (user_id, otp) VALUES(?, ?)";

    public static String READ_ALL = "SELECT * FROM user_otp_codes;";

    public static String READ_USER_OTP_CODE_BY_ID = "SELECT * FROM user_otp_codes WHERE user_id=?";

    public static String UPDATE = "UPDATE user_otp_codes " +
            "SET otp=?, "+
            "version=version+1 WHERE user_id=?;";

    public static String DELETE_USER_OTP_BY_USER_ID = "DELETE FROM user_otp_codes where user_id = ?";
}
