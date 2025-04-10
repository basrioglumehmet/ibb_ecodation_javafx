package org.example.ibb_ecodation_javafx.core.db;


import java.util.List;
import java.util.function.Consumer;

public interface Read<T> {
    void read(int id, Consumer<T> callback);
    List<T> readAll(int id);
}