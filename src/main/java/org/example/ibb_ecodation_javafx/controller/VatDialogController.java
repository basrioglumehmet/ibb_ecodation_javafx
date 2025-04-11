package org.example.ibb_ecodation_javafx.controller;

import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import org.example.ibb_ecodation_javafx.core.context.SpringContext;
import org.example.ibb_ecodation_javafx.core.service.LanguageService;
import org.example.ibb_ecodation_javafx.model.Vat;
import org.example.ibb_ecodation_javafx.service.VatService;
import org.example.ibb_ecodation_javafx.statemanagement.Store;
import org.example.ibb_ecodation_javafx.statemanagement.state.DarkModeState;
import org.example.ibb_ecodation_javafx.ui.button.ShadcnButton;
import org.example.ibb_ecodation_javafx.ui.combobox.ShadcnLanguageComboBox;
import org.example.ibb_ecodation_javafx.ui.input.ShadcnInput;
import org.example.ibb_ecodation_javafx.ui.navbar.ShadcnNavbar;
import org.example.ibb_ecodation_javafx.utils.DialogUtil;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

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
    private DatePicker transactionDateField; // For transaction date (createdAt)
    @FXML
    private Label resultLabel;
    @FXML
    private ShadcnButton closeButton; // For close button
    @FXML
    private ShadcnButton insertButton; // For insert button

    private Store store;
    private double totalAmount;
    private double vatAmount;

    private final VatService vatService = SpringContext.getContext().getBean(VatService.class);
    private final LanguageService languageService = SpringContext.getContext().getBean(LanguageService.class);
    private final String languageCode = ShadcnLanguageComboBox.getCurrentLanguageCode();

    public void initialize() {
        store = Store.getInstance();

        // Load language resources
        languageService.loadAll(languageCode);

        // Apply translations to ShadcnInput headers
        amount.setHeader(languageService.translate("input.amount"));
        rate.setHeader(languageService.translate("input.rate"));
        receipt.setHeader(languageService.translate("input.receipt"));
        description.setHeader(languageService.translate("input.description"));
        transactionDateField.setPromptText(languageService.translate("input.transactionDate"));

        // Apply translations to buttons
        if (closeButton != null) {
            closeButton.setText(languageService.translate("button.close"));
        }
        if (insertButton != null) {
            insertButton.setText(languageService.translate("button.insert"));
        }

        // Set initial result label text
        resultLabel.setText(languageService.translate("label.result"));

        // Dark mode subscription
        store.getState().subscribe(stateRegistry -> {
            boolean darkModeValue = stateRegistry.getState(DarkModeState.class).isEnabled();
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
        LocalDate transactionDate = transactionDateField.getValue();

        if (!isValidNumber(amountText) || !isValidNumber(rateText)) {
            resultLabel.setText(languageService.translate("label.invalidInput"));
            return;
        }

        if (transactionDate == null) {
            resultLabel.setText(languageService.translate("label.invalidDate"));
            return;
        }

        double amountValue = Double.parseDouble(amountText);
        double rateValue = Double.parseDouble(rateText);

        Vat vat = new Vat(
                0, // ID
                1, // User ID (hardcoded; adjust as needed)
                BigDecimal.valueOf(amountValue),
                BigDecimal.valueOf(rateValue),
                BigDecimal.valueOf(vatAmount),
                BigDecimal.valueOf(totalAmount),
                receipt.getText(),
                LocalDateTime.of(transactionDate, LocalTime.MIDNIGHT), // Use midnight for selected date
                description.getText(),
                "VARSAYILAN",
                false,
                1 // Version
        );

        vatService.create(vat);
        resultLabel.setText(languageService.translate("label.vatAdded"));
        closeVatDialog();
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

                String resultText = languageService.translate("label.vatResult");
                resultLabel.setText(String.format(resultText, vatAmount, totalAmount));
            } else {
                resultLabel.setText(languageService.translate("label.result"));
            }
        } catch (Exception e) {
            resultLabel.setText(languageService.translate("label.invalidInput"));
        }
    }

    private boolean isValidNumber(String text) {
        return text != null && !text.trim().isEmpty() && text.matches("\\d+(\\.\\d+)?");
    }
}