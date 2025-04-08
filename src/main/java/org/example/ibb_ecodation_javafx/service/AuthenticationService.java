package org.example.ibb_ecodation_javafx.service;

import org.example.ibb_ecodation_javafx.model.Authentication;

import java.util.function.Consumer;

public interface AuthenticationService {
    void signin(Authentication authentication, Consumer<Boolean> callback);
    void logout(Consumer<Boolean> callback);
}
