package org.example.ibb_ecodation_javafx.controller;

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
import org.example.ibb_ecodation_javafx.ui.button.ShadcnButton;
import org.example.ibb_ecodation_javafx.ui.button.ShadcnSwitchButton;
import org.example.ibb_ecodation_javafx.ui.combobox.ShadcnLanguageComboBox;
import org.example.ibb_ecodation_javafx.ui.input.ShadcnInput;
import org.example.ibb_ecodation_javafx.ui.navbar.ShadcnNavbar;
import org.example.ibb_ecodation_javafx.utils.DialogUtil;

import static org.example.ibb_ecodation_javafx.utils.ThemeUtil.*;

public class UserDialogController {
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
    private ShadcnButton closeButton; // For close button
    @FXML
    private ShadcnButton insertButton; // For insert button
    private Label isVerifiedLabel;
    private ShadcnSwitchButton isVerifiedSwitch;

    private final UserService userService = SpringContext.getContext().getBean(UserService.class);
    private final LanguageService languageService = SpringContext.getContext().getBean(LanguageService.class);
    private final Store store = Store.getInstance();
    private final String languageCode = ShadcnLanguageComboBox.getCurrentLanguageCode();

    public UserDialogController() {
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

        // Apply translations to buttons
        if (closeButton != null) {
            closeButton.setText(languageService.translate("button.close"));
        }
        if (insertButton != null) {
            insertButton.setText(languageService.translate("button.insert"));
        }



        // Initialize switch button and label with translation
         isVerifiedLabel = new Label(languageService.translate("label.isVerified"));
        isVerifiedLabel.setStyle("-fx-font-family:'Poppins'; -fx-font-size:20px; -fx-font-weight:bold;");

        isVerifiedSwitch = new ShadcnSwitchButton();

        HBox switchContainer = new HBox(20, isVerifiedLabel, isVerifiedSwitch);

        // Add switch container to VBox
        if (container != null) {
            container.getChildren().add(switchContainer);
        } else {
            System.err.println("Error: 'container' VBox is not initialized. Ensure it is defined in the FXML file.");
        }
        // Dark mode subscription
        store.getState().subscribe(stateRegistry -> {
            boolean darkModeValue = stateRegistry.getState(DarkModeState.class).isEnabled();
            changeNavbarColor(darkModeValue, navbar);
            changeRootPaneColor(darkModeValue, rootPane);
            changeTextColor(darkModeValue,isVerifiedLabel);
        });
    }

    @FXML
    private void closeVatDialog() {
        DialogUtil.closeDialog();
    }

    @FXML
    private void insert() {
        try {
            User entity = new User(
                    0, // ID is auto-generated
                    username.getText(),
                    email.getText(),
                    password.getText(),
                    Role.valueOf(role.getText().trim().toUpperCase()),
                    isVerifiedSwitch.getValue(),
                    false, // isLocked defaults to false for new user
                    0 // Version defaults to 0
            );
            userService.create(entity);
            closeVatDialog();
        } catch (IllegalArgumentException e) {
            System.err.println("Invalid role: " + role.getText());
        }
    }
}