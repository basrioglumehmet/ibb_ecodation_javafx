package org.example.ibb_ecodation_javafx.controller;

import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.layout.VBox;
import org.example.ibb_ecodation_javafx.core.context.SpringContext;
import org.example.ibb_ecodation_javafx.core.service.LanguageService;
import org.example.ibb_ecodation_javafx.model.UserNote;
import org.example.ibb_ecodation_javafx.service.UserNoteService;
import org.example.ibb_ecodation_javafx.statemanagement.Store;
import org.example.ibb_ecodation_javafx.statemanagement.state.DarkModeState;
import org.example.ibb_ecodation_javafx.ui.button.ShadcnButton;
import org.example.ibb_ecodation_javafx.ui.combobox.ShadcnLanguageComboBox;
import org.example.ibb_ecodation_javafx.ui.input.ShadcnInput;
import org.example.ibb_ecodation_javafx.ui.navbar.ShadcnNavbar;
import org.example.ibb_ecodation_javafx.utils.DialogUtil;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.example.ibb_ecodation_javafx.utils.ThemeUtil.changeNavbarColor;
import static org.example.ibb_ecodation_javafx.utils.ThemeUtil.changeRootPaneColor;

public class NoteDialogController {
    @FXML
    private ShadcnNavbar navbar;
    @FXML
    private VBox rootPane;
    @FXML
    private VBox container;
    @FXML
    private ShadcnInput headerField; // For note header
    @FXML
    private ShadcnInput descriptionField; // For note description
    @FXML
    private DatePicker dateField; // For reportAt
    @FXML
    private ShadcnButton closeButton; // For close button
    @FXML
    private ShadcnButton insertButton; // For insert button

    private final Store store = Store.getInstance();
    private final UserNoteService userNoteService = SpringContext.getContext().getBean(UserNoteService.class);
    private final LanguageService languageService = SpringContext.getContext().getBean(LanguageService.class);
    private final String languageCode = ShadcnLanguageComboBox.getCurrentLanguageCode();

    @FXML
    public void initialize() {
        // Load language resources
        languageService.loadAll(languageCode);

        // Apply translations to ShadcnInput headers
        headerField.setHeader(languageService.translate("input.header"));
        descriptionField.setHeader(languageService.translate("input.description"));
        dateField.setPromptText(languageService.translate("input.reportAt"));

        // Apply translations to buttons
        if (closeButton != null) {
            closeButton.setText(languageService.translate("button.close"));
        }
        if (insertButton != null) {
            insertButton.setText(languageService.translate("button.insert"));
        }

        // Dark mode subscription
        store.getState().subscribe(stateRegistry -> {
            boolean darkModeValue = stateRegistry.getState(DarkModeState.class).isEnabled();
            changeNavbarColor(darkModeValue, navbar);
            changeRootPaneColor(darkModeValue, rootPane);
        });
    }

    @FXML
    private void closeDialog() {
        DialogUtil.closeDialog();
    }

    @FXML
    private void insert() {
        UserNote entity = new UserNote();
        entity.setId(0); // Auto-generated
        entity.setUserId(1); // Hardcoded for now; adjust as needed
        entity.setHeader(headerField.getText() != null ? headerField.getText().trim() : "");
        entity.setDescription(descriptionField.getText() != null ? descriptionField.getText().trim() : "");
        LocalDate date = dateField.getValue();
        entity.setReportAt(date != null ? LocalDateTime.of(date, LocalDateTime.now().toLocalTime()) : LocalDateTime.now());
        entity.setVersion(0); // Default version

        userNoteService.create(entity);
        closeDialog();
    }
}