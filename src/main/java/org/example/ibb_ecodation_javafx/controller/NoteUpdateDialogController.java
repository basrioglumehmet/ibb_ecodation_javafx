package org.example.ibb_ecodation_javafx.controller;

import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import org.example.ibb_ecodation_javafx.core.context.SpringContext;
import org.example.ibb_ecodation_javafx.core.service.LanguageService;
import org.example.ibb_ecodation_javafx.model.UserNote;
import org.example.ibb_ecodation_javafx.service.UserNoteService;
import org.example.ibb_ecodation_javafx.statemanagement.Store;
import org.example.ibb_ecodation_javafx.statemanagement.state.DarkModeState;
import org.example.ibb_ecodation_javafx.statemanagement.state.UserState;
import org.example.ibb_ecodation_javafx.ui.button.ShadcnButton;
import org.example.ibb_ecodation_javafx.ui.combobox.ShadcnLanguageComboBox;
import org.example.ibb_ecodation_javafx.ui.input.ShadcnInput;
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
    private DatePicker dateField;
    @FXML
    private ShadcnNavbar navbar;
    @FXML
    private VBox rootPane;
    @FXML
    private ShadcnButton close;
    @FXML
    private ShadcnButton update;
    @FXML
    private org.example.ibb_ecodation_javafx.ui.input.ShadcnInput titleField; // Assuming ShadcnInput has a TextField
    @FXML
    private ShadcnInput contentField;

    private final UserNoteService userNoteService = SpringContext.getContext().getBean(UserNoteService.class);
    private final LanguageService languageService = SpringContext.getContext().getBean(LanguageService.class);
    private final Store store = Store.getInstance();
    private final String languageCode = ShadcnLanguageComboBox.getCurrentLanguageCode();

    @FXML
    public void initialize() {
        // Load language resources
        languageService.loadAll(languageCode);

        // Set button texts with translations
        update.setText(languageService.translate("button.update"));
        close.setText(languageService.translate("button.close"));
        titleField.setHeader(languageService.translate("input.header"));
        contentField.setHeader(languageService.translate("input.description"));
        // Load selected note from store
        UserState userState = store.getCurrentState(UserState.class);
        UserNote selectedNote = userState.getSelectedUserNote();

        // Subscribe to dark mode changes
        store.getState().subscribe(stateRegistry -> {
            boolean darkModeValue = stateRegistry.getState(DarkModeState.class).isEnabled();
            changeNavbarColor(darkModeValue, navbar);
            changeRootPaneColor(darkModeValue, rootPane);
        });

        // Populate fields if note exists
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