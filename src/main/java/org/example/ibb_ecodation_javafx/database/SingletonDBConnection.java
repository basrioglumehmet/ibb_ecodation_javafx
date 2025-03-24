package org.example.ibb_ecodation_javafx.database;

import lombok.Getter;
import org.example.ibb_ecodation_javafx.common.interfaces.IDatabaseConnection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class SingletonDBConnection implements IDatabaseConnection {
    private static final String CONNECTION_STRING = "jdbc:h2:./h2db/user_management2";
    private static final String DB_USERNAME = "sa";
    private static final String DB_PASSWORD = "";

    private static SingletonDBConnection instance;

    @Getter
    private Connection connection;

    private SingletonDBConnection() {
        openConnection();
    }

    private void openConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                Class.forName("org.h2.Driver");
                connection = DriverManager.getConnection(CONNECTION_STRING, DB_USERNAME, DB_PASSWORD);
                System.out.println("Database connected successfully!");
            }
        } catch (ClassNotFoundException | SQLException e) {
            throw new RuntimeException("Error connecting to database!", e);
        }
    }

    public static synchronized SingletonDBConnection getInstance() {
        if (instance == null) {
            instance = new SingletonDBConnection();
        } else {
            instance.openConnection();  // Ensure connection is open if instance already exists
        }
        return instance;
    }

    @Override
    public void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
                System.out.println("Database connection closed.");
            } catch (SQLException e) {
                throw new RuntimeException("Error closing connection!", e);
            }
        }
    }
}
