package org.example.ibb_ecodation_javafx.core.db;

import org.example.ibb_ecodation_javafx.core.context.SpringContext;
import org.example.ibb_ecodation_javafx.core.logger.SecurityLogger;

import java.sql.Connection;

public abstract class DbConnection implements Database {
    protected Connection connection;


    public abstract String getConnectionString();
    public abstract Connection connectToDatabase();

    public void destroyConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
