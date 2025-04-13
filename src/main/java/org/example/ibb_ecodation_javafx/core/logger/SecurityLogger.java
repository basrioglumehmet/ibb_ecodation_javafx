package org.example.ibb_ecodation_javafx.core.logger;

import javafx.application.Platform;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.appender.FileAppender;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.Configurator;
import org.apache.logging.log4j.core.layout.PatternLayout;
import org.example.ibb_ecodation_javafx.model.AppLog;
import org.example.ibb_ecodation_javafx.service.AppLogService;
import org.springframework.stereotype.Service;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;

import static org.example.ibb_ecodation_javafx.utils.SystemInfoUtil.getComputerName;
import static org.example.ibb_ecodation_javafx.utils.SystemInfoUtil.getIpAddress;

@Service
public class SecurityLogger {

    private static final Logger LOGGER = LogManager.getLogger(SecurityLogger.class);
    private static final String LOG_DIR = "src/main/resources/logs";
    private static final String LOG_FILE = LOG_DIR + "/security_events.log";
    private static SecurityLogger instance;
    private final AppLogService appLogService;

    public SecurityLogger(AppLogService appLogService) {
        this.appLogService = appLogService;
        try {
            Path logDir = Paths.get(LOG_DIR);
            if (Files.notExists(logDir)) {
                Files.createDirectories(logDir);
            }
            configureLog4j();
        } catch (Exception e) {
            System.err.println("Unable to initialize security logging: " + e.getMessage());
        }
    }

    private static void configureLog4j() {
        LoggerContext context = (LoggerContext) LogManager.getContext(false);
        Configuration config = context.getConfiguration();
        String pattern = "[%d{yyyy-MM-dd HH:mm:ss}] [%p] %m%n";
        PatternLayout layout = PatternLayout.newBuilder()
                .withPattern(pattern)
                .build();
        Appender fileAppender = FileAppender.newBuilder()
                .setName("FileAppender")
                .withFileName(LOG_FILE)
                .withAppend(true)
                .setLayout(layout)
                .build();
        fileAppender.start();
        config.addAppender(fileAppender);
        config.addLoggerAppender(context.getLogger(SecurityLogger.class.getName()), fileAppender);
        Configurator.setLevel(SecurityLogger.class.getName(), org.apache.logging.log4j.Level.ALL);
        context.updateLoggers();
    }

    public static synchronized SecurityLogger getInstance(AppLogService appLogService) {
        if (instance == null) {
            instance = new SecurityLogger(appLogService);
        }
        return instance;
    }

    public void logLoginAttempt(String username, boolean success) {
        String message = success ?
                "Successful login attempt for user: " + username :
                "Failed login attempt for user: " + username;
        log(org.apache.logging.log4j.Level.INFO, message);
    }

    public void logAccessAttempt(String username, String resource, boolean authorized) {
        String message = authorized ?
                username + " authorized access to " + resource :
                username + " unauthorized access attempt to " + resource;
        log(authorized ? org.apache.logging.log4j.Level.INFO : org.apache.logging.log4j.Level.WARN, message);
    }

    public void logSecurityViolation(String details) {
        String message = "SECURITY VIOLATION: " + details;
        log(org.apache.logging.log4j.Level.ERROR, message);
    }

    public void logUserOperation(String username, String resource) {
        String message = username + " performed operation on " + resource;
        log(org.apache.logging.log4j.Level.INFO, message);
    }

    public void logOperation(String message) {
        log(Level.INFO, message);
    }

    private void log(Level level, String message) {
        // Bu Runnable, log işlemini güvenli bir şekilde yürütür (UI thread kontrolü için)
        Runnable logTask = () -> {
            LOGGER.log(level, message);
            try {
                String computerName = getComputerName();
                String ipAddresses = getIpAddress();
                AppLog log = new AppLog(0, message, computerName, ipAddresses, LocalDateTime.now(), 1);
                appLogService.create(log);
            } catch (Exception e) {
                System.err.println("Failed to log to database: " + e.getMessage());
            }
        };

        if (Platform.isFxApplicationThread()) {
            logTask.run();
        } else {
            Platform.runLater(logTask);
        }
    }


    public static void main(String[] args) {
        SecurityLogger logger = SecurityLogger.getInstance(null);
        logger.logLoginAttempt("Mehmet Basrioğlu", true);
        logger.logLoginAttempt("Mehmet Basrioğlu", false);
        logger.logAccessAttempt("Mehmet Basrioğlu", "admin_panel", true);
        logger.logAccessAttempt("guest", "admin_panel", false);
        logger.logSecurityViolation("Multiple failed login attempts detected");
    }
}