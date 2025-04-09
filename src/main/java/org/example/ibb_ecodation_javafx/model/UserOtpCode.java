package org.example.ibb_ecodation_javafx.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.ibb_ecodation_javafx.annotation.JdbcNamedField;
import org.example.ibb_ecodation_javafx.core.db.Entity;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserOtpCode  implements Entity {
    @JdbcNamedField(dbFieldName = "user_id")
    private int userId;
    @JdbcNamedField(dbFieldName = "otp")
    private String otpCode;
    private int version = 1;
}
