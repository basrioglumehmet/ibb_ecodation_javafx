package org.example.ibb_ecodation_javafx.core.db;

import org.example.ibb_ecodation_javafx.config.DatabaseConfig;
import org.example.ibb_ecodation_javafx.core.context.SpringContext;
import org.example.ibb_ecodation_javafx.core.logger.SecurityLogger;
import org.example.ibb_ecodation_javafx.utils.YamlReader;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MsSqlConnection extends DbConnection {

    private DatabaseConfig databaseConfig;
    private final SecurityLogger securityLogger = SecurityLogger.getInstance();
    private static MsSqlConnection instance;


    private MsSqlConnection() {
        super();
        try{
            databaseConfig = YamlReader.readDatabaseConfig("/db-config.yml");
            securityLogger.logOperation("DB YAML dosyası başarıyla yüklendi. Uygulama kullanıma hazır:"+databaseConfig.toString());
        } catch (IOException e) {
            securityLogger.logOperation("DB YAML dosyası bulunamadı lütfen iletişime geçin.");
            throw new RuntimeException("DB YAML dosyası bulunamadı lütfen iletişime geçin.");
        }
    }

    /**
     * Dikkat!
     * SMSS ile kullanıcı oluşturulmalı ilgili veritabana setlenmeli ve yetkileri verilmelidir.
     * Diğer dikkat edilmesi gereken hususlar: connect sql grant olarak ayarlanmalıdır,
     * configuration'dan ip all portu 1433 olarak verilmelidir.
     */
    @Override
    public String getConnectionString() {
        return databaseConfig.getUrl()+databaseConfig.getUsername()+databaseConfig.getPassword();

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
                Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
                connection = DriverManager.getConnection(getConnectionString());
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return connection;
    }
}
