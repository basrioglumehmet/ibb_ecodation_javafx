package org.example.ibb_ecodation_javafx.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.ibb_ecodation_javafx.model.Authentication;
import org.example.ibb_ecodation_javafx.service.AuthenticationService;
import org.example.ibb_ecodation_javafx.service.UserService;
import org.springframework.stereotype.Service;

import java.util.function.Consumer;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {
    private final UserService userService;
    @Override
    public void signin(Authentication authentication, Consumer<Boolean> callback) {
        userService.readByEmail(authentication.getEmail(), cb -> {
            if(cb != null){
                System.out.println(cb.getUsername());
                callback.accept(true);
            }
            else{
                callback.accept(null);
            }
        });
    }

    @Override
    public void logout(Consumer<Boolean> callback) {

    }
}
