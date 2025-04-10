package org.example.ibb_ecodation_javafx.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import org.example.ibb_ecodation_javafx.statemanagement.Store;
import org.example.ibb_ecodation_javafx.statemanagement.state.DarkModeState;
import org.example.ibb_ecodation_javafx.ui.input.ShadcnInput;
import org.example.ibb_ecodation_javafx.ui.navbar.ShadcnNavbar;
import org.example.ibb_ecodation_javafx.utils.DialogUtil;

import static org.example.ibb_ecodation_javafx.utils.ThemeUtil.changeNavbarColor;
import static org.example.ibb_ecodation_javafx.utils.ThemeUtil.changeRootPaneColor;

public class VatUpdateDialogController {
    @FXML
    private ShadcnNavbar navbar;
    @FXML
    private VBox rootPaneUpdate;
    @FXML
    private ShadcnInput amount;
    @FXML
    private ShadcnInput rate;
    @FXML
    private ShadcnInput receipt;
    @FXML
    private ShadcnInput description;
    @FXML
    private Label resultLabel;

    private Store store;

    public void initialize(){
        store = Store.getInstance();
        store.getState().subscribe(stateRegistry -> {
            var darkModeValue = stateRegistry.getState(DarkModeState.class).isEnabled();
            changeNavbarColor(darkModeValue,navbar);
            changeRootPaneColor(darkModeValue,rootPaneUpdate);
        });

        amount.setTextChangeListener((newValue) -> calculateVat());
        rate.setTextChangeListener((newValue) -> calculateVat());
    }

    @FXML
    private void closeVatDialog(){
        DialogUtil.closeDialog();
    }


    private void calculateVat() {
        try {
            // Get the amount and rate from the inputs
            String amountText = amount.getText();
            String rateText = rate.getText();

            if (isValidNumber(amountText) && isValidNumber(rateText)) {
                double amountValue = Double.parseDouble(amountText);
                double rateValue = Double.parseDouble(rateText);

                double vatAmount = amountValue * (rateValue / 100);
                double totalAmount = amountValue + vatAmount;

                resultLabel.setText(String.format("Result: %.2f TL(KDV) ile %.2f TL", vatAmount, totalAmount));
            } else {
                resultLabel.setText("Result:");
            }
        } catch (Exception e) {
            resultLabel.setText("Invalid input");
        }
    }

    private boolean isValidNumber(String text) {
        // Check if the text is not null and is a valid number (it can be empty or zero)
        return text != null && !text.trim().isEmpty() && text.matches("\\d+(\\.\\d+)?");
    }
}
