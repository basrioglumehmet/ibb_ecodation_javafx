package org.example.ibb_ecodation_javafx.controller;

import javafx.fxml.FXML;
import javafx.scene.layout.VBox;
import org.example.ibb_ecodation_javafx.statemanagement.Store;
import org.example.ibb_ecodation_javafx.statemanagement.state.DarkModeState;
import org.example.ibb_ecodation_javafx.ui.navbar.ShadcnNavbar;

import static org.example.ibb_ecodation_javafx.utils.ThemeUtil.changeNavbarColor;
import static org.example.ibb_ecodation_javafx.utils.ThemeUtil.changeRootPaneColor;

public class VatPrinterDialogController {
    @FXML private ShadcnNavbar navbar;
    @FXML private VBox rootPane;

    private Store store;

    @FXML
    public void initialize() {
        store = Store.getInstance();
        store.getState().subscribe(stateRegistry -> {
            var darkModeValue = stateRegistry.getState(DarkModeState.class).isEnabled();
            changeNavbarColor(darkModeValue, navbar);
            changeRootPaneColor(darkModeValue, rootPane);
        });

    }


    private boolean isValidNumber(String text) {
        return text != null && !text.trim().isEmpty() && text.matches("\\d+(\\.\\d+)?");
    }

    @FXML
    private void closeDialog() {
        if (rootPane != null && rootPane.getScene() != null && rootPane.getScene().getWindow() != null) {
            rootPane.getScene().getWindow().hide();
        }
    }
}