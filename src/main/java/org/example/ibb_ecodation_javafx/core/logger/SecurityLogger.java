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
import org.springframework.stereotype.Service;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class SecurityLogger {
    private static final Logger LOGGER = LogManager.getLogger(SecurityLogger.class);
    private static final String LOG_DIR = "src/main/resources/logs";  // Logs directory in the project root
    private static final String LOG_FILE = LOG_DIR + "/security_events.log"; // Log file path
    private static SecurityLogger instance;

    public SecurityLogger() {
        try {
            // Check if the logs directory exists, if not, create it
            Path logDir = Paths.get(LOG_DIR);
            if (Files.notExists(logDir)) {
                Files.createDirectories(logDir); // Create logs directory if not exists
            }

            // Configure Log4j programmatically
            configureLog4j();

        } catch (Exception e) {
            System.err.println("Unable to initialize security logging: " + e.getMessage());
        }
    }

    // Log4j configuration
    private static void configureLog4j() {
        LoggerContext context = (LoggerContext) LogManager.getContext(false);
        Configuration config = context.getConfiguration();

        // Pattern layout: [Date Time] [Level] Message
        String pattern = "[%d{yyyy-MM-dd HH:mm:ss}] [%p] %m%n";
        PatternLayout layout = PatternLayout.newBuilder()
                .withPattern(pattern)
                .build();

        // Create FileAppender
        Appender fileAppender = FileAppender.newBuilder()
                .setName("FileAppender")
                .withFileName(LOG_FILE)
                .withAppend(true) // Append mode for the log file
                .setLayout(layout)
                .build();

        fileAppender.start();
        config.addAppender(fileAppender);

        // Add appender to logger and set level
        config.addLoggerAppender(context.getLogger(SecurityLogger.class.getName()), fileAppender);
        Configurator.setLevel(SecurityLogger.class.getName(), org.apache.logging.log4j.Level.ALL);

        context.updateLoggers(); // Update the configuration
    }

    // Singleton pattern
    public static synchronized SecurityLogger getInstance() {
        if (instance == null) {
            instance = new SecurityLogger();
        }
        return instance;
    }

    // Log login attempts
    public void logLoginAttempt(String username, boolean success) {
        String message = success ?
                "Successful login attempt for user: " + username :
                "Failed login attempt for user: " + username;
        log(org.apache.logging.log4j.Level.INFO, message);
    }

    // Log access attempts
    public void logAccessAttempt(String username, String resource, boolean authorized) {
        String message = authorized ?
                username + " authorized access to " + resource :
                username + " unauthorized access attempt to " + resource;
        log(authorized ? org.apache.logging.log4j.Level.INFO : org.apache.logging.log4j.Level.WARN, message);
    }

    // Log security violations
    public void logSecurityViolation(String details) {
        log(org.apache.logging.log4j.Level.ERROR, "SECURITY VIOLATION: " + details);
    }

    // Log user operations
    public void logUserOperation(String username, String resource) {
        String message = username + " performed operation on " + resource;
        log(org.apache.logging.log4j.Level.INFO, message);
    }

    // General logging method with JavaFX thread safety
    private void log(Level level, String message) {
        if (Platform.isFxApplicationThread()) {
            LOGGER.log(level, message);
        } else {
            Platform.runLater(() -> LOGGER.log(level, message)); // Ensure safe logging from non-UI threads
        }
    }

    // Example usage
    public static void main(String[] args) {
        SecurityLogger logger = SecurityLogger.getInstance();

        // Simulate some security events
        logger.logLoginAttempt("john_doe", true);
        logger.logLoginAttempt("jane_smith", false);
        logger.logAccessAttempt("john_doe", "admin_panel", true);
        logger.logAccessAttempt("guest", "admin_panel", false);
        logger.logSecurityViolation("Multiple failed login attempts detected");
    }

    public void logOperation(String message) {
        log(Level.INFO, message);
    }
}
