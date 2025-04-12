package org.example.ibb_ecodation_javafx.controller;

import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.layout.VBox;
import org.example.ibb_ecodation_javafx.core.context.SpringContext;
import org.example.ibb_ecodation_javafx.core.logger.SecurityLogger;
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
    private ShadcnInput headerField;
    @FXML
    private ShadcnInput descriptionField;
    @FXML
    private DatePicker dateField;
    @FXML
    private ShadcnButton closeButton;
    @FXML
    private ShadcnButton insertButton;

    private final Store store = Store.getInstance();
    private final UserNoteService userNoteService = SpringContext.getContext().getBean(UserNoteService.class);
    private final LanguageService languageService = SpringContext.getContext().getBean(LanguageService.class);
    private final String languageCode = ShadcnLanguageComboBox.getCurrentLanguageCode();
    private final SecurityLogger securityLogger = SpringContext.getContext().getBean(SecurityLogger.class);
    @FXML
    public void initialize() {
        languageService.loadAll(languageCode);

        headerField.setHeader(languageService.translate("input.header"));
        descriptionField.setHeader(languageService.translate("input.description"));
        dateField.setPromptText(languageService.translate("input.reportAt"));

        if (closeButton != null) {
            closeButton.setText(languageService.translate("button.close"));
        }
        if (insertButton != null) {
            insertButton.setText(languageService.translate("button.insert"));
        }

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
        headerField.clearError();
        descriptionField.clearError();

        FieldValidator validator = new FieldValidator();

        validator.addRule(new ValidationRule<String>() {
            @Override
            public String getValue() {
                return headerField.getText().trim();
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
                return headerField;
            }
        });

        validator.addRule(new ValidationRule<String>() {
            @Override
            public String getValue() {
                return headerField.getText().trim();
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
                return headerField;
            }
        });

        validator.addRule(new ValidationRule<String>() {
            @Override
            public String getValue() {
                return descriptionField.getText().trim();
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
                return descriptionField;
            }
        });

        validator.addRule(new ValidationRule<String>() {
            @Override
            public String getValue() {
                return descriptionField.getText().trim();
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
                return descriptionField;
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
            public ShadcnInput getInput() {
                return null;
            }
        });

        validator.addRule(new ValidationRule<LocalDate>() {
            @Override
            public LocalDate getValue() {
                return dateField.getValue();
            }

            @Override
            public boolean validate(LocalDate value) {
                return value == null || value.isAfter(LocalDate.now().minusYears(1));
            }

            @Override
            public String getErrorMessage() {
                return languageService.translate("input.reportAt.tooOld");
            }

            @Override
            public ShadcnInput getInput() {
                return null;
            }
        });

        validator.onError(error -> {
            if (error.getInput() != null) {
                error.getInput().setError(error.getErrorDetail());
            } else {
                securityLogger.logSecurityViolation(error.getErrorDetail());
            }
        });

        if (validator.runValidatorEngine().isEmpty()) {
            var userDetail = store.getCurrentState(UserState.class).getUserDetail();
            UserNote entity = new UserNote();
            entity.setId(0);
            entity.setUserId(userDetail.getUserId());
            entity.setHeader(headerField.getText().trim());
            entity.setDescription(descriptionField.getText().trim());
            LocalDate date = dateField.getValue();
            entity.setReportAt(LocalDateTime.of(date, LocalDateTime.now().toLocalTime()));
            entity.setVersion(0);

            userNoteService.create(entity);
            closeDialog();
        }
    }
}