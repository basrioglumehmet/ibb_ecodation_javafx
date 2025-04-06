package org.example.ibb_ecodation_javafx.core.db;

import org.example.ibb_ecodation_javafx.model.User;

import java.util.function.Consumer;

public interface Read<T> {
    void read(int id, Consumer<T> callback);
}