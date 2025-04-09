package org.example.ibb_ecodation_javafx.service;

import org.example.ibb_ecodation_javafx.model.Authentication;
import org.example.ibb_ecodation_javafx.model.User;
import org.example.ibb_ecodation_javafx.model.dto.RegisterDto;
import org.example.ibb_ecodation_javafx.model.enums.AuthenticationResult;

import java.util.function.Consumer;

public interface AuthenticationService {
    void signin(Authentication authentication, Consumer<AuthenticationResult> callback);
    void logout(Consumer<Boolean> callback);

    void signup(RegisterDto registerDto, Consumer<AuthenticationResult> callback);
}
