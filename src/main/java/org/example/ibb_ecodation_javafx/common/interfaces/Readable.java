package org.example.ibb_ecodation_javafx.common.interfaces;

import java.sql.SQLException;

public interface Readable<T> {
    void read(T item) throws SQLException;
}
