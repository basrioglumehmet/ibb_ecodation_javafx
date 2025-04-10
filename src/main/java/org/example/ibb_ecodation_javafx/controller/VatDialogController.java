package org.example.ibb_ecodation_javafx.controller;

import javafx.fxml.FXML;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import org.example.ibb_ecodation_javafx.statemanagement.Store;
import org.example.ibb_ecodation_javafx.statemanagement.state.DarkModeState;
import org.example.ibb_ecodation_javafx.ui.navbar.ShadcnNavbar;
import org.example.ibb_ecodation_javafx.utils.DialogUtil;

public class VatDialogController {
    @FXML
    private ShadcnNavbar navbar;
    @FXML
    private VBox rootPane;

    private Store store;

    public void initialize(){
        store = Store.getInstance();
        store.getState().subscribe(stateRegistry -> {
            var darkModeValue = stateRegistry.getState(DarkModeState.class).isEnabled();
            changeNavbarColor(darkModeValue);
            changeContentColor(darkModeValue);
        });
    }

    @FXML
    private void closeVatDialog(){
        DialogUtil.closeDialog();
    }

    private void changeNavbarColor(boolean lightModeValue) {
        this.navbar.setStyle(String.format("-fx-background-color: %s;", lightModeValue ? "white":"#121214") +
                "-fx-padding: 10px 20px 10px 20px;");
    }

    private void changeContentColor(boolean lightModeValue) {
        this.rootPane.setStyle(String.format("-fx-background-color: %s;", lightModeValue ? "white":"#121214"));

    }
}
