package org.example.ibb_ecodation_javafx.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.ibb_ecodation_javafx.annotation.DbField;
import org.example.ibb_ecodation_javafx.core.db.Entity;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserOtpCode  implements Entity {
    @DbField(name = "user_id")
    private int userId;
    @DbField(name = "otp")
    private String otpCode;
    @DbField(name = "version")
    private int version = 1;
}
