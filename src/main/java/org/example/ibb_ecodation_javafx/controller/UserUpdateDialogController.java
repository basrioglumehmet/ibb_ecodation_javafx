package org.example.ibb_ecodation_javafx.controller;

import io.reactivex.rxjava3.disposables.Disposable;
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
import org.example.ibb_ecodation_javafx.statemanagement.state.UserState;
import org.example.ibb_ecodation_javafx.ui.button.ShadcnButton;
import org.example.ibb_ecodation_javafx.ui.button.ShadcnSwitchButton;
import org.example.ibb_ecodation_javafx.ui.combobox.ShadcnLanguageComboBox;
import org.example.ibb_ecodation_javafx.ui.input.ShadcnInput;
import org.example.ibb_ecodation_javafx.ui.navbar.ShadcnNavbar;
import org.example.ibb_ecodation_javafx.utils.DialogUtil;

import java.util.regex.Pattern;

import static org.example.ibb_ecodation_javafx.utils.ThemeUtil.*;

public class UserUpdateDialogController {
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
    private ShadcnButton close;
    @FXML
    private ShadcnButton update;

    private ShadcnSwitchButton isVerified;
    private ShadcnSwitchButton isLocked;
    private User selectedUser;
    private Label isVerifiedLabel;
    private Label isLockedLabel;
    private Disposable darkModeDisposable;

    private final UserService userService = SpringContext.getContext().getBean(UserService.class);
    private final LanguageService languageService = SpringContext.getContext().getBean(LanguageService.class);
    private final Store store = Store.getInstance();
    private final String languageCode = ShadcnLanguageComboBox.getCurrentLanguageCode();

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");

    public UserUpdateDialogController() {
    }

    @FXML
    public void initialize() {
        languageService.loadAll(languageCode);

        username.setHeader(languageService.translate("input.username"));
        email.setHeader(languageService.translate("input.email"));
        password.setHeader(languageService.translate("input.password"));
        role.setHeader(languageService.translate("input.role"));
        update.setText(languageService.translate("button.update"));
        close.setText(languageService.translate("button.close"));

        isVerifiedLabel = new Label(languageService.translate("label.isVerified"));
        isVerifiedLabel.setStyle("-fx-font-family: 'Poppins'; -fx-font-size: 20px; -fx-font-weight: bold;");
        isVerified = new ShadcnSwitchButton();

        isLockedLabel = new Label(languageService.translate("label.isLocked"));
        isLockedLabel.setStyle("-fx-font-family: 'Poppins'; -fx-font-size: 20px; -fx-font-weight: bold;");
        isLocked = new ShadcnSwitchButton();

        selectedUser = store.getCurrentState(UserState.class).getSelectedUser();
        if (selectedUser != null) {
            email.setText(selectedUser.getEmail());
            username.setText(selectedUser.getUsername());
            role.setText(selectedUser.getRole().toString());
            isVerified.setValue(selectedUser.isVerified());
            isLocked.setValue(selectedUser.isLocked());
        }

        HBox switchContainer = new HBox(20, isVerifiedLabel, isVerified);
        HBox switchContainer2 = new HBox(20, isLockedLabel, isLocked);

        if (container != null) {
            container.getChildren().addAll(switchContainer, switchContainer2);
        }

        isVerified.watchIsActive().subscribe(aBoolean -> {
            if (selectedUser != null) {
                selectedUser.setVerified(aBoolean);
            }
        });
        isLocked.watchIsActive().subscribe(aBoolean -> {
            if (selectedUser != null) {
                selectedUser.setLocked(aBoolean);
            }
        });
        username.setTextChangeListener(newValue -> {
            if (selectedUser != null) {
                selectedUser.setUsername(newValue != null ? newValue.trim() : "");
            }
        });
        email.setTextChangeListener(newValue -> {
            if (selectedUser != null) {
                selectedUser.setEmail(newValue != null ? newValue.trim() : "");
            }
        });
        password.setTextChangeListener(newValue -> {
            if (selectedUser != null) {
                selectedUser.setPassword(newValue != null ? newValue.trim() : "");
            }
        });
        role.setTextChangeListener(newValue -> {
            if (selectedUser != null && newValue != null) {
                try {
                    selectedUser.setRole(Role.valueOf(newValue.trim().toUpperCase()));
                } catch (IllegalArgumentException e) {
                }
            }
        });

        boolean initialDarkMode = store.getCurrentState(DarkModeState.class).isEnabled();
        updateDarkModeStyles(initialDarkMode);

        darkModeDisposable = store.getState().subscribe(stateRegistry -> {
            boolean darkModeValue = stateRegistry.getState(DarkModeState.class).isEnabled();
            updateDarkModeStyles(darkModeValue);
        });
    }

    private void updateDarkModeStyles(boolean darkModeValue) {
        changeNavbarColor(darkModeValue, navbar);
        changeRootPaneColor(darkModeValue, rootPane);
        changeTextColor(darkModeValue, isVerifiedLabel);
        changeTextColor(darkModeValue, isLockedLabel);
    }

    @FXML
    private void closeDialog() {
        dispose();
        DialogUtil.closeDialog();
    }

    @FXML
    private void updateHandler() {
        username.clearError();
        email.clearError();
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
            public ShadcnInput getInput() {
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
            public ShadcnInput getInput() {
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
            public ShadcnInput getInput() {
                return email;
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
            public ShadcnInput getInput() {
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
            public ShadcnInput getInput() {
                return role;
            }
        });

        validator.onError(error -> error.getInput().setError(error.getErrorDetail()));

        if (validator.runValidatorEngine().isEmpty() && selectedUser != null) {
            userService.update(selectedUser, user -> {
                closeDialog();
            });
        }
    }

    private void dispose() {
        if (darkModeDisposable != null && !darkModeDisposable.isDisposed()) {
            darkModeDisposable.dispose();
        }
    }
}