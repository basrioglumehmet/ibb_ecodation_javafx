package org.example.ibb_ecodation_javafx.controller;

import lombok.RequiredArgsConstructor;
import org.example.ibb_ecodation_javafx.statemanagement.state.TranslatorState;
import org.example.ibb_ecodation_javafx.ui.ValidatableComponent;
import org.springframework.stereotype.Controller;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.example.ibb_ecodation_javafx.core.context.SpringContext;
import org.example.ibb_ecodation_javafx.core.service.LanguageService;
import org.example.ibb_ecodation_javafx.core.validation.FieldValidator;
import org.example.ibb_ecodation_javafx.core.validation.ValidationError;
import org.example.ibb_ecodation_javafx.core.validation.ValidationRule;
import org.example.ibb_ecodation_javafx.model.User;
import org.example.ibb_ecodation_javafx.model.enums.Role;
import org.example.ibb_ecodation_javafx.service.UserService;
import org.example.ibb_ecodation_javafx.statemanagement.Store;
import org.example.ibb_ecodation_javafx.statemanagement.state.DarkModeState;
import org.example.ibb_ecodation_javafx.ui.button.ShadcnButton;
import org.example.ibb_ecodation_javafx.ui.button.ShadcnSwitchButton;
import org.example.ibb_ecodation_javafx.ui.combobox.ShadcnLanguageComboBox;
import org.example.ibb_ecodation_javafx.ui.input.ShadcnInput;
import org.example.ibb_ecodation_javafx.ui.navbar.ShadcnNavbar;
import org.example.ibb_ecodation_javafx.utils.DialogUtil;

import java.util.regex.Pattern;

import static org.example.ibb_ecodation_javafx.utils.ThemeUtil.*;


@Controller
@RequiredArgsConstructor
public class AddUserController {
    @FXML
    private ShadcnNavbar navbar;
    @FXML
    private VBox rootPane;
    @FXML
    private VBox container;
    @FXML
    private ShadcnInput email;
    @FXML
    private ShadcnInput username;
    @FXML
    private ShadcnInput password;
    @FXML
    private ShadcnInput role;
    @FXML
    private ShadcnButton closeButton;
    @FXML
    private ShadcnButton insertButton;
    private Label isVerifiedLabel;
    private ShadcnSwitchButton isVerifiedSwitch;

    private final UserService userService;
    private final LanguageService languageService;
    private  Store store = Store.getInstance();

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");


    @FXML
    public void initialize() {
        languageService.loadAll(store.getCurrentState(TranslatorState.class).countryCode().getCode());

        username.setHeader(languageService.translate("input.username.placeholder"));
        email.setHeader(languageService.translate("input.email.placeholder"));
        password.setHeader(languageService.translate("input.password.placeholder"));
        role.setHeader(languageService.translate("input.role.placeholder"));

        if (closeButton != null) {
            closeButton.setText(languageService.translate("button.close"));
        }
        if (insertButton != null) {
            insertButton.setText(languageService.translate("button.insert"));
        }

        isVerifiedLabel = new Label(languageService.translate("label.isVerified"));
        isVerifiedLabel.setStyle("-fx-font-family:'Poppins'; -fx-font-size:20px; -fx-font-weight:bold;");

        isVerifiedSwitch = new ShadcnSwitchButton();

        HBox switchContainer = new HBox(20, isVerifiedLabel, isVerifiedSwitch);

        if (container != null) {
            container.getChildren().add(switchContainer);
        }

        store.getState().subscribe(stateRegistry -> {
            boolean darkModeValue = stateRegistry.getState(DarkModeState.class).isEnabled();
            changeNavbarColor(darkModeValue, navbar);
            changeRootPaneColor(darkModeValue, rootPane);
            changeTextColor(darkModeValue, isVerifiedLabel);
        });
    }

    @FXML
    private void closeVatDialog() {
        DialogUtil.closeDialog();
    }

    @FXML
    private void insert() {
        username.clearError();
        email.clearError();
        password.clearError();
        role.clearError();

        FieldValidator validator = new FieldValidator();

        validator.addRule(new ValidationRule<String>() {
            @Override
            public String getValue() {
                return username.getText().trim();
            }

            @Override
            public boolean validate(String value) {
                return !value.isEmpty();
            }

            @Override
            public String getErrorMessage() {
                return languageService.translate("input.username.empty");
            }

            @Override
            public ValidatableComponent getComponent() {
                return username;
            }
        });

        validator.addRule(new ValidationRule<String>() {
            @Override
            public String getValue() {
                return email.getText().trim();
            }

            @Override
            public boolean validate(String value) {
                return !value.isEmpty();
            }

            @Override
            public String getErrorMessage() {
                return languageService.translate("input.email.empty");
            }

            @Override
            public ValidatableComponent getComponent() {
                return email;
            }
        });

        validator.addRule(new ValidationRule<String>() {
            @Override
            public String getValue() {
                return email.getText().trim();
            }

            @Override
            public boolean validate(String value) {
                return value.isEmpty() || EMAIL_PATTERN.matcher(value).matches();
            }

            @Override
            public String getErrorMessage() {
                return languageService.translate("input.email.invalid");
            }

            @Override
            public ValidatableComponent getComponent() {
                return email;
            }
        });

        validator.addRule(new ValidationRule<String>() {
            @Override
            public String getValue() {
                return password.getText().trim();
            }

            @Override
            public boolean validate(String value) {
                return !value.isEmpty();
            }

            @Override
            public String getErrorMessage() {
                return languageService.translate("input.password.empty");
            }

            @Override
            public ValidatableComponent getComponent() {
                return password;
            }
        });

        validator.addRule(new ValidationRule<String>() {
            @Override
            public String getValue() {
                return password.getText().trim();
            }

            @Override
            public boolean validate(String value) {
                return value.isEmpty() || value.length() >= 6;
            }

            @Override
            public String getErrorMessage() {
                return languageService.translate("input.password.invalid");
            }

            @Override
            public ValidatableComponent getComponent() {
                return password;
            }
        });

        validator.addRule(new ValidationRule<String>() {
            @Override
            public String getValue() {
                return role.getText().trim();
            }

            @Override
            public boolean validate(String value) {
                return !value.isEmpty();
            }

            @Override
            public String getErrorMessage() {
                return languageService.translate("input.role.empty");
            }

            @Override
            public ValidatableComponent getComponent() {
                return role;
            }
        });

        validator.addRule(new ValidationRule<String>() {
            @Override
            public String getValue() {
                return role.getText().trim();
            }

            @Override
            public boolean validate(String value) {
                try {
                    Role.valueOf(value.toUpperCase());
                    return true;
                } catch (IllegalArgumentException e) {
                    return false;
                }
            }

            @Override
            public String getErrorMessage() {
                return languageService.translate("input.role.invalid");
            }

            @Override
            public ValidatableComponent getComponent() {
                return role;
            }
        });

        validator.onError(error -> error.getComponent().setError(error.getErrorDetail()));

        if (validator.runValidatorEngine().isEmpty()) {
            try {
                User entity = new User(
                        0,
                        username.getText().trim(),
                        email.getText().trim(),
                        password.getText().trim(),
                        Role.valueOf(role.getText().trim().toUpperCase()),
                        isVerifiedSwitch.getValue(),
                        false,
                        0
                );
                userService.save(entity);
                closeVatDialog();
            } catch (IllegalArgumentException e) {
                email.setError(languageService.translate("error.invalid.input"));
            }
        }
    }
}
