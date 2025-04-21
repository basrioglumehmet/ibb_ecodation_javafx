package org.example.ibb_ecodation_javafx.utils;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import lombok.RequiredArgsConstructor;
import org.example.ibb_ecodation_javafx.core.service.LanguageService;
import org.example.ibb_ecodation_javafx.model.UserNote;
import org.example.ibb_ecodation_javafx.service.UserNoteService;
import org.example.ibb_ecodation_javafx.statemanagement.Store;
import org.example.ibb_ecodation_javafx.statemanagement.state.TranslatorState;
import org.example.ibb_ecodation_javafx.statemanagement.state.UserState;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Component("alertScheduler")
public class AlertScheduler {
    private ScheduledExecutorService scheduler; // No longer static
    private static final Set<Integer> alertedNoteIds = new HashSet<>();
    private static boolean isRunning = false;
    private static boolean hasShownConnectionError = false;
    private final UserNoteService userNoteService;
    private final LanguageService languageService;

    public AlertScheduler(UserNoteService userNoteService, LanguageService languageService) {
        this.userNoteService = userNoteService;
        this.languageService = languageService;
        this.scheduler = Executors.newScheduledThreadPool(1); // Initialize scheduler
    }

    public void start() {
        // Ensure scheduler is initialized and not terminated
        if (scheduler == null || scheduler.isShutdown() || scheduler.isTerminated()) {
            scheduler = Executors.newScheduledThreadPool(1);
        }

        // Already running, no need to restart
        if (isRunning) return;

        // Schedule task to run daily
        scheduler.scheduleAtFixedRate(() -> {
            try {
                LocalDateTime now = LocalDateTime.now();
                Store store = Store.getInstance();
                UserState userState = store.getCurrentState(UserState.class);
                int userId = userState.getUserDetail() != null ? userState.getUserDetail().getUserId() : -1;

                if (userId == -1) return;

                List<UserNote> notes = userNoteService.findAllById(userId);

                if (notes.isEmpty()) {
                    stop(); // Stop if no notes
                    return;
                }

                if (hasShownConnectionError) {
                    hasShownConnectionError = false;
                }

                for (UserNote note : notes) {
                    if (note.getReportAt() != null && !alertedNoteIds.contains(note.getId())) {
                        LocalDateTime reportAt = note.getReportAt().toLocalDateTime().truncatedTo(ChronoUnit.DAYS);
                        LocalDateTime nowTruncated = now.truncatedTo(ChronoUnit.DAYS);

                        if (nowTruncated.isEqual(reportAt) || nowTruncated.isAfter(reportAt)) {
                            Platform.runLater(() -> {
                                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                                alert.setTitle(languageService.translate("alert.note.title"));
                                alert.setHeaderText(languageService.translate("alert.note.header") + note.getHeader());
                                alert.setContentText(note.getDescription());
                                alert.showAndWait();
                            });
                            alertedNoteIds.add(note.getId());
                        }
                    }
                }

            } catch (Exception e) {
                if (!hasShownConnectionError) {
                    hasShownConnectionError = true;
                    Platform.runLater(() ->
                            TrayUtil.showTrayNotification("Veritabanı bağlantısı yok. Notlar alınamıyor.", "IBB Bootcamp Uyarı")
                    );
                }
            }
        }, 0, 1, TimeUnit.DAYS);

        isRunning = true;
    }

    public void stop() {
        if (!isRunning) return;

        // Shutdown scheduler
        if (scheduler != null) {
            scheduler.shutdown();
            try {
                // Wait briefly for tasks to complete
                if (!scheduler.awaitTermination(2, TimeUnit.SECONDS)) {
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