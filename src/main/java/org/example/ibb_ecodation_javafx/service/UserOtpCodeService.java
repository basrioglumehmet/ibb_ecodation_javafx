package org.example.ibb_ecodation_javafx.service;

import org.example.ibb_ecodation_javafx.core.service.Crud;
import org.example.ibb_ecodation_javafx.model.UserOtpCode;

import java.util.function.Consumer;

public interface UserOtpCodeService extends Crud<UserOtpCode> {
    void createAndCallback(UserOtpCode entity, Consumer<Boolean> callback);
    void verifyOtp(String otpCode, Consumer<Boolean> callback);
}
