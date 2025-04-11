package org.example.ibb_ecodation_javafx.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.example.ibb_ecodation_javafx.core.context.SpringContext;
import org.example.ibb_ecodation_javafx.model.User;
import org.example.ibb_ecodation_javafx.model.enums.Role;
import org.example.ibb_ecodation_javafx.service.UserService;
import org.example.ibb_ecodation_javafx.statemanagement.Store;
import org.example.ibb_ecodation_javafx.statemanagement.state.DarkModeState;
import org.example.ibb_ecodation_javafx.statemanagement.state.UserState;
import org.example.ibb_ecodation_javafx.ui.button.ShadcnSwitchButton;
import org.example.ibb_ecodation_javafx.ui.input.ShadcnInput;
import org.example.ibb_ecodation_javafx.ui.navbar.ShadcnNavbar;
import org.example.ibb_ecodation_javafx.utils.DialogUtil;

import static org.example.ibb_ecodation_javafx.utils.ThemeUtil.changeNavbarColor;
import static org.example.ibb_ecodation_javafx.utils.ThemeUtil.changeRootPaneColor;

public class UserUpdateDialogController {
    @FXML
    private ShadcnNavbar navbar;
    @FXML
    private VBox rootPane;
    private Store store;

    @FXML
    private VBox container; // Ensure this is defined in your FXML file
    @FXML
    private ShadcnInput email;
    @FXML
    private ShadcnInput username;
    @FXML
    private ShadcnInput password;
    @FXML
    private ShadcnInput role;

    private ShadcnSwitchButton isVerified;

    private ShadcnSwitchButton isLocked;

    private User selectedUser;


    private final UserService userService  = SpringContext.getContext().getBean(UserService.class);

    public UserUpdateDialogController() {
        store = Store.getInstance();
    }

    public void initialize() {
        store.getState().subscribe(stateRegistry -> {
            var darkModeValue = stateRegistry.getState(DarkModeState.class).isEnabled();
            changeNavbarColor(darkModeValue, navbar);
            changeRootPaneColor(darkModeValue, rootPane);
        });

        Label label = new Label("Is Verified");
        label.setStyle("-fx-text-fill:white; -font-family:'Poppins'; -fx-font-size:20px; -fx-font-weight:bold;");
        isVerified = new ShadcnSwitchButton();

        Label label2 = new Label("Is Locked");
        label2.setStyle("-fx-text-fill:white; -font-family:'Poppins'; -fx-font-size:20px; -fx-font-weight:bold;");
        isLocked = new ShadcnSwitchButton();
        selectedUser = store.getCurrentState(UserState.class).getSelectedUser();
        email.setText(selectedUser.getEmail());
        username.setText(selectedUser.getUsername());
        role.setText(selectedUser.getRole().toString());
        isVerified.setValue(selectedUser.isVerified());
        isLocked.setValue(selectedUser.isLocked());

        System.out.println(selectedUser.isVerified());

        HBox switchContainer = new HBox();
        switchContainer.setSpacing(20);
        switchContainer.getChildren().add(label); // Fixed: Use add() for a single node
        switchContainer.getChildren().add(isVerified); // Add the switch button as well

        HBox switchContainer2 = new HBox();
        switchContainer2.setSpacing(20);
        switchContainer2.getChildren().add(label2); // Fixed: Use add() for a single node
        switchContainer2.getChildren().add(isLocked); // Add the switch button as well

        // Ensure container is not null before adding to it
        if (container != null) {
            container.getChildren().addAll(switchContainer,switchContainer2);
        } else {
            System.err.println("Error: 'container' VBox is not initialized. Ensure it is defined in the FXML file.");
        }

        isVerified.watchIsActive().subscribe(aBoolean -> {
            selectedUser.setVerified(aBoolean);
        });
        isLocked.watchIsActive().subscribe(aBoolean -> {
            selectedUser.setLocked(aBoolean);
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

    }

    @FXML
    private void closeDialog() {
        DialogUtil.closeDialog();
    }

    @FXML
    private void update() {

        userService.update(selectedUser, user -> {
            closeDialog();
        });
    }
}