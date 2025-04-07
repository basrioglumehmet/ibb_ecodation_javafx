package org.example.ibb_ecodation_javafx.core.db;

import java.util.function.Consumer;

public interface Update<T> {
    void update(T entity, Consumer<T> callback);
}