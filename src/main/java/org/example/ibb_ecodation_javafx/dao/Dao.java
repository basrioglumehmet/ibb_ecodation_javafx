package org.example.ibb_ecodation_javafx.dao;

import org.example.ibb_ecodation_javafx.common.CrudOperation;

import java.sql.Connection;
import java.util.List;
import java.util.Optional;

public interface Dao<T,Dto>  extends CrudOperation<T> {

    // LIST
    List<T> list();

    // FIND
    Optional<T> findByName(String name);
    Optional<T> findById(int id);

    // Gövdeli Method
    default Connection databaseConnection(){
        return null;
    }
}