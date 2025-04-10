package org.example.ibb_ecodation_javafx.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import org.example.ibb_ecodation_javafx.core.context.SpringContext;
import org.example.ibb_ecodation_javafx.model.Vat;
import org.example.ibb_ecodation_javafx.service.VatService;
import org.example.ibb_ecodation_javafx.statemanagement.Store;
import org.example.ibb_ecodation_javafx.statemanagement.state.DarkModeState;
import org.example.ibb_ecodation_javafx.statemanagement.state.VatTableState;
import org.example.ibb_ecodation_javafx.ui.input.ShadcnInput;
import org.example.ibb_ecodation_javafx.ui.navbar.ShadcnNavbar;
import org.example.ibb_ecodation_javafx.utils.DialogUtil;

import java.math.BigDecimal;

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
    private Vat selectedItemData;
    private VatService vatService = SpringContext.getContext().getBean(VatService.class);

    public void initialize() {
        store = Store.getInstance();
        store.getState().subscribe(stateRegistry -> {
            var darkModeValue = stateRegistry.getState(DarkModeState.class).isEnabled();
            changeNavbarColor(darkModeValue, navbar);
            changeRootPaneColor(darkModeValue, rootPaneUpdate);
        });

        selectedItemData = store.getCurrentState(VatTableState.class).getSelectedVatItem();
        amount.setText(selectedItemData.getAmount().toPlainString());
        rate.setText(selectedItemData.getRate().toPlainString());
        receipt.setText(selectedItemData.getReceiptNumber());
        description.setText(selectedItemData.getDescription());
        amount.setTextChangeListener((newValue) -> calculateVat());
        rate.setTextChangeListener((newValue) -> calculateVat());
        calculateVat();
    }

    @FXML
    private void closeVatDialog() {
        DialogUtil.closeDialog();
    }


    private BigDecimal calculateVatAmount(BigDecimal amountValue, BigDecimal rateValue) {
        BigDecimal vatAmount = amountValue.multiply(rateValue).divide(new BigDecimal("100"));
        BigDecimal totalAmount = amountValue.add(vatAmount);
        return totalAmount;
    }

    private void calculateVat() {
        try {

            String amountText = amount.getText();
            String rateText = rate.getText();

            if (isValidNumber(amountText) && isValidNumber(rateText)) {
                BigDecimal amountValue = new BigDecimal(amountText);
                BigDecimal rateValue = new BigDecimal(rateText);

                BigDecimal totalAmount = calculateVatAmount(amountValue, rateValue);

                BigDecimal vatAmount = totalAmount.subtract(amountValue);
                resultLabel.setText(String.format("Result: %.2f TL (KDV) ile %.2f TL", vatAmount, totalAmount));
            } else {
                resultLabel.setText("Result:");
            }
        } catch (Exception e) {
            resultLabel.setText("Invalid input");
        }
    }

    @FXML
    private void updateData() {
        try {
            String amountText = amount.getText();
            String rateText = rate.getText();
            String receiptText = receipt.getText();
            String descriptionText = description.getText();

            if (isValidNumber(amountText) && isValidNumber(rateText)) {
                BigDecimal amountValue = new BigDecimal(amountText);
                BigDecimal rateValue = new BigDecimal(rateText);

                BigDecimal vatAmount = amountValue.multiply(rateValue).divide(new BigDecimal("100"));
                BigDecimal totalAmount = amountValue.add(vatAmount);

                selectedItemData.setBaseAmount(amountValue);
                selectedItemData.setAmount(vatAmount);
                selectedItemData.setTotalAmount(totalAmount);
                selectedItemData.setRate(rateValue);
                selectedItemData.setReceiptNumber(receiptText);
                selectedItemData.setDescription(descriptionText);

                vatService.update(selectedItemData, vat -> {
                    resultLabel.setText("Vat updated successfully");
                });
            } else {
                resultLabel.setText("Invalid amount or rate input");
            }
        } catch (Exception e) {
            resultLabel.setText("Error updating data");
        }
    }

    private boolean isValidNumber(String text) {
        return text != null && !text.trim().isEmpty() && text.matches("\\d+(\\.\\d+)?");
    }
}
