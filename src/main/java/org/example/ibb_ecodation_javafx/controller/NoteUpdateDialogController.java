package org.example.ibb_ecodation_javafx.controller;

import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import org.example.ibb_ecodation_javafx.core.context.SpringContext;
import org.example.ibb_ecodation_javafx.model.UserNote;
import org.example.ibb_ecodation_javafx.service.UserNoteService;
import org.example.ibb_ecodation_javafx.statemanagement.Store;
import org.example.ibb_ecodation_javafx.statemanagement.state.DarkModeState;
import org.example.ibb_ecodation_javafx.statemanagement.state.UserState;
import org.example.ibb_ecodation_javafx.ui.navbar.ShadcnNavbar;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import static org.example.ibb_ecodation_javafx.utils.ThemeUtil.changeNavbarColor;
import static org.example.ibb_ecodation_javafx.utils.ThemeUtil.changeRootPaneColor;

/**
 * Not güncelleme dialogunu yöneten kontrolör.
 */
public class NoteUpdateDialogController {
    @FXML
    private DatePicker dateField; // Changed to DatePicker
    @FXML
    private ShadcnNavbar navbar; // Changed to DatePicker
    @FXML
    private VBox rootPane; // Changed to DatePicker
    @FXML
    private org.example.ibb_ecodation_javafx.ui.input.ShadcnInput titleField; // Assuming ShadcnInput has a TextField internally
    @FXML
    private TextArea contentField;

    private final UserNoteService userNoteService = SpringContext.getContext().getBean(UserNoteService.class);
    private final Store store = Store.getInstance();

    @FXML
    public void initialize() {
        UserState userState = store.getCurrentState(UserState.class);
        UserNote selectedNote = userState.getSelectedUserNote();
        store.getState().subscribe(stateRegistry -> {
            var darkModeValue = stateRegistry.getState(DarkModeState.class).isEnabled();
            changeNavbarColor(darkModeValue, navbar);
            changeRootPaneColor(darkModeValue, rootPane);
        });
        if (selectedNote != null) {
            dateField.setValue(selectedNote.getReportAt().toLocalDate());
            titleField.getTextField().setText(selectedNote.getHeader());
            contentField.setText(selectedNote.getDescription());
        } else {
            System.out.println("Seçili not bulunamadı.");
        }
    }

    @FXML
    private void handleUpdate() {
        UserState userState = store.getCurrentState(UserState.class);
        UserNote selectedNote = userState.getSelectedUserNote();
        if (selectedNote == null || selectedNote.getId() == -1) {
            System.out.println("Güncellenecek geçerli bir not yok.");
            return;
        }

        UserNote userNote = new UserNote();
        userNote.setId(selectedNote.getId());
        userNote.setHeader(titleField.getTextField().getText());
        userNote.setDescription(contentField.getText());
        LocalDate date = dateField.getValue();
        userNote.setReportAt(date != null ? LocalDateTime.of(date, LocalTime.now()) : selectedNote.getReportAt());
        userNote.setUserId(selectedNote.getUserId());
        userNote.setVersion(selectedNote.getVersion());

        userNoteService.update(userNote, updated -> {
            System.out.println("Not güncellendi: " + updated.getHeader());
            dateField.getScene().getWindow().hide();
        });
    }

    @FXML
    private void handleCancel() {
        dateField.getScene().getWindow().hide();
    }
}