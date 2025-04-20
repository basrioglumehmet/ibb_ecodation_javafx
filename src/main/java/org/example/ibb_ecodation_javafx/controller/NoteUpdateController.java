package org.example.ibb_ecodation_javafx.controller;

import lombok.RequiredArgsConstructor;
import org.example.ibb_ecodation_javafx.statemanagement.state.TranslatorState;
import org.example.ibb_ecodation_javafx.ui.ValidatableComponent;
import org.example.ibb_ecodation_javafx.ui.datepicker.ShadcnDatePicker;
import org.springframework.stereotype.Controller;
import javafx.fxml.FXML;
import javafx.scene.layout.VBox;
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
import org.example.ibb_ecodation_javafx.utils.DialogUtil;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import static org.example.ibb_ecodation_javafx.utils.ThemeUtil.changeNavbarColor;
import static org.example.ibb_ecodation_javafx.utils.ThemeUtil.changeRootPaneColor;

@Controller
@RequiredArgsConstructor
public class NoteUpdateController {
    @FXML
    private ShadcnNavbar navbar;
    @FXML
    private VBox rootPane;
    @FXML
    private ShadcnInput titleField;
    @FXML
    private ShadcnInput contentField;
    @FXML
    private ShadcnDatePicker dateField;
    @FXML
    private ShadcnButton close;
    @FXML
    private ShadcnButton update;

    private final UserNoteService userNoteService;
    private final LanguageService languageService;
    private final Store store = Store.getInstance();

    @FXML
    public void initialize() {
        languageService.loadAll(store.getCurrentState(TranslatorState.class).countryCode().getCode());

        update.setText(languageService.translate("button.update"));
        close.setText(languageService.translate("button.close"));
        titleField.setHeader(languageService.translate("input.header"));
        contentField.setHeader(languageService.translate("input.description"));
        dateField.setHeader(languageService.translate("input.reportAt"));

        UserState userState = store.getCurrentState(UserState.class);
        UserNote selectedNote = userState.getSelectedUserNote();

        store.getState().subscribe(stateRegistry -> {
            boolean darkModeValue = stateRegistry.getState(DarkModeState.class).isEnabled();
            changeNavbarColor(darkModeValue, navbar);
            changeRootPaneColor(darkModeValue, rootPane);
        });

        if (selectedNote != null && selectedNote.getId() != -1) {
            titleField.setText(selectedNote.getHeader());
            contentField.setText(selectedNote.getDescription());
        }
    }

    @FXML
    private void handleUpdate() {
        titleField.clearError();
        contentField.clearError();
        dateField.clearError();

        UserState userState = store.getCurrentState(UserState.class);
        UserNote selectedNote = userState.getSelectedUserNote();
        if (selectedNote == null || selectedNote.getId() == -1) {
            return;
        }

        FieldValidator validator = new FieldValidator();

        // Title validation
        validator.addRule(new ValidationRule<String>() {
            @Override
            public String getValue() {
                return titleField.getText().trim();
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
            public ValidatableComponent getComponent() {
                return titleField;
            }
        });

        validator.addRule(new ValidationRule<String>() {
            @Override
            public String getValue() {
                return titleField.getText().trim();
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
            public ValidatableComponent getComponent() {
                return titleField;
            }
        });

        // Content validation
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
            public ValidatableComponent getComponent() {
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
            public ValidatableComponent getComponent() {
                return contentField;
            }
        });

        // Date validation
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
            public ValidatableComponent getComponent() {
                return dateField;
            }
        });

        validator.addRule(new ValidationRule<LocalDate>() {
            @Override
            public LocalDate getValue() {
                return dateField.getValue();
            }

            @Override
            public boolean validate(LocalDate value) {
                return value == null || !value.isAfter(LocalDate.now());
            }

            @Override
            public String getErrorMessage() {
                return languageService.translate("input.reportAt.future");
            }

            @Override
            public ValidatableComponent getComponent() {
                return dateField;
            }
        });

        validator.onError(error -> {
            if (error.getComponent() != null) {
                error.getComponent().setError(error.getErrorDetail());
            } else {
                System.out.println(error.getErrorDetail());
            }
        });

        if (validator.runValidatorEngine().isEmpty()) {
            UserNote userNote = new UserNote();
            userNote.setId(selectedNote.getId());
            userNote.setHeader(titleField.getText().trim());
            userNote.setDescription(contentField.getText().trim());
            LocalDate date = dateField.getValue();
            userNote.setReportAt(
                    Timestamp.valueOf(LocalDateTime.of(date, LocalTime.MIDNIGHT))
            );
            userNote.setUserId(selectedNote.getUserId());
            userNote.setVersion(selectedNote.getVersion());

            userNoteService.update(userNote);
            DialogUtil.closeDialog();

        }
    }

    @FXML
    private void handleCancel() {
        DialogUtil.closeDialog();
    }
}