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
import org.example.ibb_ecodation_javafx.ui.button.ShadcnSwitchButton;
import org.example.ibb_ecodation_javafx.ui.input.ShadcnInput;
import org.example.ibb_ecodation_javafx.ui.navbar.ShadcnNavbar;
import org.example.ibb_ecodation_javafx.utils.DialogUtil;

import static org.example.ibb_ecodation_javafx.utils.ThemeUtil.changeNavbarColor;
import static org.example.ibb_ecodation_javafx.utils.ThemeUtil.changeRootPaneColor;

public class UserDialogController {
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

    private ShadcnSwitchButton button;


    private final UserService userService  = SpringContext.getContext().getBean(UserService.class);

    public UserDialogController() {
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
        button = new ShadcnSwitchButton();

        HBox switchContainer = new HBox();
        switchContainer.setSpacing(20);
        switchContainer.getChildren().add(label); // Fixed: Use add() for a single node
        switchContainer.getChildren().add(button); // Add the switch button as well

        // Ensure container is not null before adding to it
        if (container != null) {
            container.getChildren().add(switchContainer);
        } else {
            System.err.println("Error: 'container' VBox is not initialized. Ensure it is defined in the FXML file.");
        }
    }

    @FXML
    private void closeVatDialog() {
        DialogUtil.closeDialog();
    }

    @FXML
    private void insert() {
        var entity = new User(0,
                username.getText(),
                email.getText(),
                password.getText(),
                Role.valueOf(role.getText()),
                button.getValue(),
                false,
                0
                );
        userService.create(entity);
    }
}