package org.example.ibb_ecodation_javafx.controller;

import io.reactivex.rxjava3.disposables.Disposable;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.example.ibb_ecodation_javafx.core.context.SpringContext;
import org.example.ibb_ecodation_javafx.core.service.LanguageService;
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

    public UserUpdateDialogController() {
        // Constructor remains empty as dependencies are initialized as fields
    }

    @FXML
    public void initialize() {
        // Load language resources
        languageService.loadAll(languageCode);

        // Apply translations to ShadcnInput headers
        username.setHeader(languageService.translate("input.username"));
        email.setHeader(languageService.translate("input.email"));
        password.setHeader(languageService.translate("input.password"));
        role.setHeader(languageService.translate("input.role"));
        update.setText(languageService.translate("button.update"));
        close.setText(languageService.translate("button.close"));

        // Initialize switch buttons and labels with translations
        isVerifiedLabel = new Label(languageService.translate("label.isVerified"));
        isVerifiedLabel.setStyle("-fx-font-family: 'Poppins'; -fx-font-size: 20px; -fx-font-weight: bold;");
        isVerified = new ShadcnSwitchButton();

        isLockedLabel = new Label(languageService.translate("label.isLocked"));
        isLockedLabel.setStyle("-fx-font-family: 'Poppins'; -fx-font-size: 20px; -fx-font-weight: bold;");
        isLocked = new ShadcnSwitchButton();

        // Load selected user from store
        selectedUser = store.getCurrentState(UserState.class).getSelectedUser();
        if (selectedUser != null) {
            email.setText(selectedUser.getEmail());
            username.setText(selectedUser.getUsername());
            role.setText(selectedUser.getRole().toString());
            isVerified.setValue(selectedUser.isVerified());
            isLocked.setValue(selectedUser.isLocked());
        } else {
            System.err.println("Seçili kullanıcı bulunamadı.");
        }

        // Create switch containers
        HBox switchContainer = new HBox(20, isVerifiedLabel, isVerified);
        HBox switchContainer2 = new HBox(20, isLockedLabel, isLocked);

        // Add switch containers to VBox
        if (container != null) {
            container.getChildren().addAll(switchContainer, switchContainer2);
        } else {
            System.err.println("Error: 'container' VBox is not initialized. Ensure it is defined in the FXML file.");
        }

        // Set up listeners
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
                    System.err.println("Invalid role: " + newValue);
                }
            }
        });

        // Dark mode subscription
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
        if (selectedUser != null) {
            userService.update(selectedUser, user -> {
                closeDialog();
            });
        } else {
            System.err.println("Güncellenecek kullanıcı yok.");
        }
    }

    private void dispose() {
        if (darkModeDisposable != null && !darkModeDisposable.isDisposed()) {
            darkModeDisposable.dispose();
        }
    }
}