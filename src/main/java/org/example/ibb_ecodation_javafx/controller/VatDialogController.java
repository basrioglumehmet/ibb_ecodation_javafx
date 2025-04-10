package org.example.ibb_ecodation_javafx.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import org.example.ibb_ecodation_javafx.core.context.SpringContext;
import org.example.ibb_ecodation_javafx.model.Vat;
import org.example.ibb_ecodation_javafx.service.VatService;
import org.example.ibb_ecodation_javafx.statemanagement.Store;
import org.example.ibb_ecodation_javafx.statemanagement.state.DarkModeState;
import org.example.ibb_ecodation_javafx.ui.input.ShadcnInput;
import org.example.ibb_ecodation_javafx.ui.navbar.ShadcnNavbar;
import org.example.ibb_ecodation_javafx.utils.DialogUtil;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;

import static org.example.ibb_ecodation_javafx.utils.ThemeUtil.changeNavbarColor;
import static org.example.ibb_ecodation_javafx.utils.ThemeUtil.changeRootPaneColor;

public class VatDialogController {
    @FXML
    private ShadcnNavbar navbar;
    @FXML
    private VBox rootPane;
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

    private double totalAmount;
    private double vatAmount;

    private final VatService vatService = SpringContext.getContext().getBean(VatService.class);

    public void initialize() {
        store = Store.getInstance();
        store.getState().subscribe(stateRegistry -> {
            var darkModeValue = stateRegistry.getState(DarkModeState.class).isEnabled();
            changeNavbarColor(darkModeValue, navbar);
            changeRootPaneColor(darkModeValue, rootPane);
        });

        amount.setTextChangeListener((newValue) -> calculateVat());
        rate.setTextChangeListener((newValue) -> calculateVat());
    }

    @FXML
    private void closeVatDialog() {
        DialogUtil.closeDialog();
    }

    @FXML
    private void insert() {
        String amountText = amount.getText();
        String rateText = rate.getText();
        if (isValidNumber(amountText) && isValidNumber(rateText)) {
            double amountValue = Double.parseDouble(amountText);
            double rateValue = Double.parseDouble(rateText);

            Vat vat = new Vat(
                    0,
                    1,
                    BigDecimal.valueOf(amountValue),
                    BigDecimal.valueOf(rateValue),
                    BigDecimal.valueOf(vatAmount),
                    BigDecimal.valueOf(totalAmount),
                    this.receipt.getText(),
                    LocalDateTime.now(),
                    this.description.getText(),
                    "VARSAYILAN",
                    false,
                    1
            );

            vatService.create(vat);
            resultLabel.setText("Vat Added! Click to refresh button");
        } else {
            resultLabel.setText("Invalid input. Please check the values.");
        }
    }

    private void calculateVat() {
        try {
            String amountText = amount.getText();
            String rateText = rate.getText();

            if (isValidNumber(amountText) && isValidNumber(rateText)) {
                double amountValue = Double.parseDouble(amountText);
                double rateValue = Double.parseDouble(rateText);

                vatAmount = amountValue * (rateValue / 100);
                totalAmount = amountValue + vatAmount;

                resultLabel.setText(String.format("Result: %.2f TL(KDV) ile %.2f TL", vatAmount, totalAmount));
            } else {
                resultLabel.setText("Result:");
            }
        } catch (Exception e) {
            resultLabel.setText("Invalid input");
        }
    }

    private boolean isValidNumber(String text) {
        return text != null && !text.trim().isEmpty() && text.matches("\\d+(\\.\\d+)?");
    }
}
