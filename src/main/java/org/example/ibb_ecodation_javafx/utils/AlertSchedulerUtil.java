package org.example.ibb_ecodation_javafx.utils;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import lombok.experimental.UtilityClass;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@UtilityClass
public class AlertSchedulerUtil {
    private  ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);;


    public void startMinuteAlert() {
        scheduler.scheduleAtFixedRate(() -> {
            Platform.runLater(() -> {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Minute Alert");
                alert.setHeaderText("One Minute Has Passed");
                alert.setContentText("This is your scheduled alert, triggered every minute!");
                alert.showAndWait();
            });
        }, 0, 10, TimeUnit.SECONDS); // Start immediately, repeat every 1 minute
    }

    public void stop() {
        if (scheduler != null && !scheduler.isShutdown()) {
            scheduler.shutdown();
            try {
                if (!scheduler.awaitTermination(5, TimeUnit.SECONDS)) {
                    scheduler.shutdownNow();
                }
            } catch (InterruptedException e) {
                scheduler.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }
    }
}