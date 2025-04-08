package org.example.ibb_ecodation_javafx.service;

import org.example.ibb_ecodation_javafx.core.service.Crud;
import org.example.ibb_ecodation_javafx.model.User;

import java.util.function.Consumer;

public interface UserService extends Crud<User> {
    void readByEmail(String email,Consumer<User> callback);
}
