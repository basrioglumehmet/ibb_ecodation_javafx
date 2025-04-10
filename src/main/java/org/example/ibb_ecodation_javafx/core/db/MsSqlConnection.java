package org.example.ibb_ecodation_javafx.core.db;

import org.example.ibb_ecodation_javafx.core.context.SpringContext;
import org.example.ibb_ecodation_javafx.core.logger.SecurityLogger;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MsSqlConnection extends DbConnection {

    private static MsSqlConnection instance;


    private MsSqlConnection() {
        super();
    }

    /**
     * Dikkat!
     * SMSS ile kullanıcı oluşturulmalı ilgili veritabana setlenmeli ve yetkileri verilmelidir.
     * Diğer dikkat edilmesi gereken hususlar: connect sql grant olarak ayarlanmalıdır,
     * configuration'dan ip all portu 1433 olarak verilmelidir.
     * @return
     */
    @Override
    public String getConnectionString() {
        return "jdbc:sqlserver://localhost:1433;" +
                "databaseName=ibb_java_se;" +
                "trustServerCertificate=true;" +
                "user=mehmet;password=123456";

    }

    public static synchronized MsSqlConnection getInstance() {
        if (instance == null) {
            instance = new MsSqlConnection();
        }
        return instance;
    }

    @Override
    public Connection connectToDatabase() {
        try {
            if (connection == null || connection.isClosed()) {
                connection = DriverManager.getConnection(getConnectionString());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return connection;
    }
}
