package org.example.ibb_ecodation_javafx.core.db;

import org.example.ibb_ecodation_javafx.config.DatabaseConfig;
import org.example.ibb_ecodation_javafx.utils.YamlReader;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

@Component
public class MsSqlConnection extends DbConnection {

    private final DatabaseConfig databaseConfig;
    private static MsSqlConnection instance;

    public MsSqlConnection() {
        super();
        try {
            this.databaseConfig = YamlReader.readDatabaseConfig("/db-config.yml");
        } catch (IOException e) {
            throw new RuntimeException("DB YAML dosyası bulunamadı lütfen iletişime geçin.");
        }
    }

    public static synchronized MsSqlConnection getInstance() {
        if (instance == null) {
            instance = new MsSqlConnection();
        }
        return instance;
    }

    @Override
    public String getConnectionString() {
        return databaseConfig.getUrl() + databaseConfig.getUsername() + databaseConfig.getPassword();
    }

    @Override
    public Connection connectToDatabase() {
        try {
            if (connection == null || connection.isClosed()) {
                Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
                connection = DriverManager.getConnection(getConnectionString());
            }
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException("Veritabanı bağlantısı başarısız", e);
        }
        return connection;
    }

}