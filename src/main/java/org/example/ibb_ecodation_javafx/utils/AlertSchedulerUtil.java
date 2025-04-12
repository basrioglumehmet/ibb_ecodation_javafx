package org.example.ibb_ecodation_javafx.utils;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import lombok.experimental.UtilityClass;
import org.example.ibb_ecodation_javafx.core.context.SpringContext;
import org.example.ibb_ecodation_javafx.core.service.LanguageService;
import org.example.ibb_ecodation_javafx.model.UserNote;
import org.example.ibb_ecodation_javafx.service.UserNoteService;
import org.example.ibb_ecodation_javafx.statemanagement.Store;
import org.example.ibb_ecodation_javafx.statemanagement.state.TranslatorState;
import org.example.ibb_ecodation_javafx.statemanagement.state.UserState;
import org.example.ibb_ecodation_javafx.ui.combobox.ShadcnLanguageComboBox;

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
    private static final LanguageService languageService = SpringContext.getContext().getBean(LanguageService.class);
    private static final String languageCode = ShadcnLanguageComboBox.getCurrentLanguageCode();

    private static UserNoteService getUserNoteService() {
        if (userNoteService == null) {
            userNoteService = SpringContext.getContext().getBean(UserNoteService.class);
        }
        return userNoteService;
    }

    public void start() {
        if (isRunning) {
            return;
        }

        scheduler.scheduleAtFixedRate(() -> {
            try {
                LocalDateTime now = LocalDateTime.now();
                Store store = Store.getInstance();
                UserState userState = store.getCurrentState(UserState.class);
                int userId = userState.getUserDetail() != null ? userState.getUserDetail().getUserId() : -1;

                if (userId == -1) {
                    return;
                }

                List<UserNote> notes = getUserNoteService().readAll(userId);

                for (UserNote note : notes) {
                    if (note.getReportAt() != null &&
                            !alertedNoteIds.contains(note.getId()) &&
                            (now.isEqual(note.getReportAt()) || now.isAfter(note.getReportAt()))) {
                        Platform.runLater(() -> {
                            languageService.loadAll(store.getCurrentState(TranslatorState.class).countryCode().getCode());
                            Alert alert = new Alert(Alert.AlertType.INFORMATION);
                            alert.setTitle(languageService.translate("alert.note.title"));
                            alert.setHeaderText(languageService.translate("alert.note.header") + note.getHeader());
                            alert.setContentText(note.getDescription());
                            alert.showAndWait();
                        });
                        alertedNoteIds.add(note.getId());
                    }
                }
            } catch (Exception e) {
                System.err.println("Error in alert scheduler: " + e.getMessage());
            }
        }, 0, 10, TimeUnit.SECONDS);

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