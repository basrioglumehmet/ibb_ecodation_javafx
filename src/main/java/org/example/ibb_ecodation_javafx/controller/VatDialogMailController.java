package org.example.ibb_ecodation_javafx.controller;

import javafx.fxml.FXML;
import javafx.scene.layout.VBox;
import org.example.ibb_ecodation_javafx.statemanagement.Store;
import org.example.ibb_ecodation_javafx.statemanagement.state.DarkModeState;
import org.example.ibb_ecodation_javafx.ui.navbar.ShadcnNavbar;
import org.example.ibb_ecodation_javafx.utils.DialogUtil;

import static org.example.ibb_ecodation_javafx.utils.ThemeUtil.changeNavbarColor;
import static org.example.ibb_ecodation_javafx.utils.ThemeUtil.changeRootPaneColor;

public class VatDialogMailController {
    @FXML
    private ShadcnNavbar navbar;
    @FXML
    private VBox rootPaneMail;

    private Store store;
    public void initialize(){
        store = Store.getInstance();
        store.getState().subscribe(stateRegistry -> {
            var darkModeValue = stateRegistry.getState(DarkModeState.class).isEnabled();
            changeNavbarColor(darkModeValue,navbar);
            changeRootPaneColor(darkModeValue,rootPaneMail);
        });
    }

    @FXML
    private void closeVatDialog(){
        DialogUtil.closeDialog();
    }
}
