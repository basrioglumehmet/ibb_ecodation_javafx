package org.example.ibb_ecodation_javafx.controller;

import lombok.RequiredArgsConstructor;
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
public class CreateNoteController {
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
    private ShadcnDatePicker dateField;
    @FXML
    private ShadcnButton closeButton;
    @FXML
    private ShadcnButton insertButton;

    private final Store store = Store.getInstance();
    private final UserNoteService userNoteService;
    private final LanguageService languageService;
    private final String languageCode = ShadcnLanguageComboBox.getCurrentLanguageCode();

    @FXML
    public void initialize() {
        languageService.loadAll(languageCode);

        headerField.setHeader(languageService.translate("input.header"));
        descriptionField.setHeader(languageService.translate("input.description"));
        dateField.setHeader(languageService.translate("input.reportAt"));

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
        dateField.clearError();

        FieldValidator validator = new FieldValidator();

        // Header validation
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
            public ValidatableComponent getComponent() {
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
            public ValidatableComponent getComponent() {
                return headerField;
            }
        });

        // Description validation
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
            public ValidatableComponent getComponent() {
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
            public ValidatableComponent getComponent() {
                return descriptionField;
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
            var userDetail = store.getCurrentState(UserState.class).getUserDetail();
            UserNote entity = new UserNote();
            entity.setId(0);
            entity.setUserId(userDetail.getUserId());
            entity.setHeader(headerField.getText().trim());
            entity.setDescription(descriptionField.getText().trim());
            LocalDate date = dateField.getValue();
            entity.setReportAt(
                    Timestamp.valueOf(LocalDateTime.of(date, LocalTime.MIDNIGHT))
            );
            entity.setVersion(0);
            System.out.println("Selected date: " + date);
            System.out.println("Report at: " + entity.getReportAt());
            userNoteService.save(entity);
            closeDialog();
        }
    }
}