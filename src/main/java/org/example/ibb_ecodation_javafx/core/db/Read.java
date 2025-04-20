package org.example.ibb_ecodation_javafx.core.db;


import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

public interface Read<T> {
    void read(int id, Consumer<T> callback);
    Optional<List<T>> readAll(int id); // Returns an Optional containing a list of T
}