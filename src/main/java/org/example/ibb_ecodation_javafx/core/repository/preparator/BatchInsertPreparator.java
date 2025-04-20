package org.example.ibb_ecodation_javafx.core.repository.preparator;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public interface BatchInsertPreparator<T> {
    void setValues(PreparedStatement ps, T item) throws SQLException;
}