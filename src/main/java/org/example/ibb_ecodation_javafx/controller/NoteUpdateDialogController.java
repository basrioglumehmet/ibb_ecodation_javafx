package org.example.ibb_ecodation_javafx.controller;

import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.layout.VBox;
import org.example.ibb_ecodation_javafx.core.context.SpringContext;
import org.example.ibb_ecodation_javafx.core.service.LanguageService;
import org.example.ibb_ecodation_javafx.core.validation.FieldValidator;
import org.example.ibb_ecodation_javafx.core.validation.ValidationError;
import org.example.ibb_ecodation_javafx.core.validation.ValidationRule;
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
    private ShadcnInput titleField;
    @FXML
    private ShadcnInput contentField;

    private final UserNoteService userNoteService = SpringContext.getContext().getBean(UserNoteService.class);
    private final LanguageService languageService = SpringContext.getContext().getBean(LanguageService.class);
    private final Store store = Store.getInstance();
    private final String languageCode = ShadcnLanguageComboBox.getCurrentLanguageCode();

    @FXML
    public void initialize() {
        languageService.loadAll(languageCode);

        update.setText(languageService.translate("button.update"));
        close.setText(languageService.translate("button.close"));
        titleField.setHeader(languageService.translate("input.header"));
        contentField.setHeader(languageService.translate("input.description"));

        UserState userState = store.getCurrentState(UserState.class);
        UserNote selectedNote = userState.getSelectedUserNote();

        store.getState().subscribe(stateRegistry -> {
            boolean darkModeValue = stateRegistry.getState(DarkModeState.class).isEnabled();
            changeNavbarColor(darkModeValue, navbar);
            changeRootPaneColor(darkModeValue, rootPane);
        });

        if (selectedNote != null) {
            dateField.setValue(selectedNote.getReportAt().toLocalDate());
            titleField.getTextField().setText(selectedNote.getHeader());
            contentField.setText(selectedNote.getDescription());
        }
    }

    @FXML
    private void handleUpdate() {
        titleField.clearError();
        contentField.clearError();

        UserState userState = store.getCurrentState(UserState.class);
        UserNote selectedNote = userState.getSelectedUserNote();
        if (selectedNote == null || selectedNote.getId() == -1) {
            return;
        }

        FieldValidator validator = new FieldValidator();

        validator.addRule(new ValidationRule<String>() {
            @Override
            public String getValue() {
                return titleField.getTextField().getText().trim();
            }

            @Override
            public boolean validate(String value) {
                return !value.isEmpty();
            }

            @Override
            public String getErrorMessage() {
                return languageService.translate("input.header.empty");
            }

            @Override
            public ShadcnInput getInput() {
                return titleField;
            }
        });

        validator.addRule(new ValidationRule<String>() {
            @Override
            public String getValue() {
                return titleField.getTextField().getText().trim();
            }

            @Override
            public boolean validate(String value) {
                return value.isEmpty() || (value.length() >= 3 && value.length() <= 100);
            }

            @Override
            public String getErrorMessage() {
                return languageService.translate("input.header.invalid");
            }

            @Override
            public ShadcnInput getInput() {
                return titleField;
            }
        });

        validator.addRule(new ValidationRule<String>() {
            @Override
            public String getValue() {
                return contentField.getText().trim();
            }

            @Override
            public boolean validate(String value) {
                return !value.isEmpty();
            }

            @Override
            public String getErrorMessage() {
                return languageService.translate("input.description.empty");
            }

            @Override
            public ShadcnInput getInput() {
                return contentField;
            }
        });

        validator.addRule(new ValidationRule<String>() {
            @Override
            public String getValue() {
                return contentField.getText().trim();
            }

            @Override
            public boolean validate(String value) {
                return value.isEmpty() || (value.length() >= 5 && value.length() <= 500);
            }

            @Override
            public String getErrorMessage() {
                return languageService.translate("input.description.invalid");
            }

            @Override
            public ShadcnInput getInput() {
                return contentField;
            }
        });

        validator.addRule(new ValidationRule<LocalDate>() {
            @Override
            public LocalDate getValue() {
                return dateField.getValue();
            }

            @Override
            public boolean validate(LocalDate value) {
                return value != null;
            }

            @Override
            public String getErrorMessage() {
                return languageService.translate("input.reportAt.empty");
            }

            @Override
            public ShadcnInput getInput() {
                return null;
            }
        });

        validator.onError(error -> {
            if (error.getInput() != null) {
                error.getInput().setError(error.getErrorDetail());
            }
        });

        if (validator.runValidatorEngine().isEmpty()) {
            UserNote userNote = new UserNote();
            userNote.setId(selectedNote.getId());
            userNote.setHeader(titleField.getTextField().getText().trim());
            userNote.setDescription(contentField.getText().trim());
            LocalDate date = dateField.getValue();
            userNote.setReportAt(LocalDateTime.of(date, LocalTime.now()));
            userNote.setUserId(selectedNote.getUserId());
            userNote.setVersion(selectedNote.getVersion());

            userNoteService.update(userNote, updated -> {
                dateField.getScene().getWindow().hide();
            });
        }
    }

    @FXML
    private void handleCancel() {
        dateField.getScene().getWindow().hide();
    }
}