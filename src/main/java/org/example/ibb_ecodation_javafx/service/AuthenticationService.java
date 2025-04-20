package org.example.ibb_ecodation_javafx.service;

import org.example.ibb_ecodation_javafx.model.Authentication;
import org.example.ibb_ecodation_javafx.model.dto.RegisterDto;
import org.example.ibb_ecodation_javafx.model.dto.SignInDto;
import org.example.ibb_ecodation_javafx.model.enums.AuthenticationResult;

public interface AuthenticationService {
    SignInDto signin(Authentication authentication);
    Boolean logout();
    AuthenticationResult signup(RegisterDto registerDto);
}