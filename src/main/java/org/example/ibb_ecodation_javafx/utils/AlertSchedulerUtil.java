package org.example.ibb_ecodation_javafx.utils;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import lombok.experimental.UtilityClass;
import org.example.ibb_ecodation_javafx.core.context.SpringContext;
import org.example.ibb_ecodation_javafx.model.UserNote;
import org.example.ibb_ecodation_javafx.service.UserNoteService;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@UtilityClass
public class AlertSchedulerUtil {
    private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private static final Set<Integer> alertedNoteIds = new HashSet<>();
    private static boolean isRunning = false;
    private static UserNoteService userNoteService;

    // Initialize UserNoteService lazily
    private static UserNoteService getUserNoteService() {
        if (userNoteService == null) {
            userNoteService = SpringContext.getContext().getBean(UserNoteService.class);
        }
        return userNoteService;
    }

    /**
     * Starts the scheduler to continuously monitor all UserNotes and show alerts when reportAt time arrives.
     */
    public void start() {
        if (isRunning) {
            return; // Prevent multiple starts
        }

        scheduler.scheduleAtFixedRate(() -> {
            try {
                LocalDateTime now = LocalDateTime.now();
                List<UserNote> notes = getUserNoteService().readAll(1);

                for (UserNote note : notes) {
                    if (note.getReportAt() != null &&
                            !alertedNoteIds.contains(note.getId ()) &&
                            (now.isEqual(note.getReportAt()) || now.isAfter(note.getReportAt()))) {
                        Platform.runLater(() -> {
                            Alert alert = new Alert(Alert.AlertType.INFORMATION);
                            alert.setTitle("Note Reminder");
                            alert.setHeaderText("Time for Note: " + note.getHeader());
                            alert.setContentText(note.getDescription());
                            alert.showAndWait();
                        });
                        alertedNoteIds.add(note.getId()); // Mark as alerted
                    }
                }
            } catch (Exception e) {
                System.err.println("Error in alert scheduler: " + e.getMessage());
            }
        }, 0, 10, TimeUnit.SECONDS); // Check every 10 seconds

        isRunning = true;
    }

    public void stop() {
        if (!isRunning) {
            return;
        }

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
        alertedNoteIds.clear();
        isRunning = false;
    }

    public void resetAlert(int noteId) {
        alertedNoteIds.remove(noteId);
    }
}