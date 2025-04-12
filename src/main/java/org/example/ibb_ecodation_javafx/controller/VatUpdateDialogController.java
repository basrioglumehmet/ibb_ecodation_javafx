package org.example.ibb_ecodation_javafx.controller;

import io.reactivex.rxjava3.disposables.Disposable;
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
import org.example.ibb_ecodation_javafx.statemanagement.state.VatTableState;
import org.example.ibb_ecodation_javafx.ui.button.ShadcnButton;
import org.example.ibb_ecodation_javafx.ui.combobox.ShadcnLanguageComboBox;
import org.example.ibb_ecodation_javafx.ui.input.ShadcnInput;
import org.example.ibb_ecodation_javafx.ui.navbar.ShadcnNavbar;
import org.example.ibb_ecodation_javafx.utils.DialogUtil;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.example.ibb_ecodation_javafx.utils.ThemeUtil.*;

public class VatUpdateDialogController {
    @FXML private ShadcnNavbar navbar;
    @FXML private VBox rootPaneUpdate;
    @FXML private ShadcnInput amount;
    @FXML private ShadcnInput rate;
    @FXML private ShadcnInput receipt;
    @FXML private ShadcnInput description;
    @FXML private DatePicker transactionDateField;
    @FXML private Label resultLabel;
    @FXML private ShadcnButton close;
    @FXML private ShadcnButton update;

    private Store store;
    private Vat selectedItemData;
    private Disposable darkModeDisposable;
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
        if (close != null) {
            close.setText(languageService.translate("button.close"));
        }
        if (update != null) {
            update.setText(languageService.translate("button.update"));
        }

        // Style result label
        resultLabel.setStyle("-fx-font-family: 'Poppins'; -fx-font-size: 16px;");
        resultLabel.setText(languageService.translate("label.result"));

        // Load selected VAT item
        selectedItemData = store.getCurrentState(VatTableState.class).getSelectedVatItem();
        if (selectedItemData != null) {
            amount.setText(selectedItemData.getBaseAmount().toPlainString());
            rate.setText(selectedItemData.getRate().toPlainString());
            receipt.setText(selectedItemData.getReceiptNumber());
            description.setText(selectedItemData.getDescription());
            transactionDateField.setValue(selectedItemData.getTransactionDate() != null ? selectedItemData.getTransactionDate().toLocalDate() : null);
        }

        // Set up listeners
        amount.setTextChangeListener((newValue) -> calculateVat());
        rate.setTextChangeListener((newValue) -> calculateVat());

        // Initialize dark mode
        boolean initialDarkMode = store.getCurrentState(DarkModeState.class).isEnabled();
        updateDarkModeStyles(initialDarkMode);

        // Dark mode subscription
        darkModeDisposable = store.getState().subscribe(stateRegistry -> {
            boolean darkModeValue = stateRegistry.getState(DarkModeState.class).isEnabled();
            updateDarkModeStyles(darkModeValue);
        });

        calculateVat();
    }

    private void updateDarkModeStyles(boolean darkModeValue) {
        changeNavbarColor(darkModeValue, navbar);
        changeRootPaneColor(darkModeValue, rootPaneUpdate);
        changeTextColor(darkModeValue, resultLabel);
    }

    @FXML
    private void closeVatDialog() {
        dispose();
        DialogUtil.closeDialog();
    }

    private BigDecimal calculateVatAmount(BigDecimal amountValue, BigDecimal rateValue) {
        BigDecimal vatAmount = amountValue.multiply(rateValue).divide(new BigDecimal("100"));
        return amountValue.add(vatAmount); // Returns totalAmount
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

                String resultText = languageService.translate("label.vatResult");
                resultLabel.setText(String.format(resultText, vatAmount.doubleValue(), totalAmount.doubleValue()));
            } else {
                resultLabel.setText(languageService.translate("label.result"));
            }
        } catch (Exception e) {
            resultLabel.setText(languageService.translate("label.invalidInput"));
        }
    }

    @FXML
    private void updateData() {
        try {
            String amountText = amount.getText();
            String rateText = rate.getText();
            String receiptText = receipt.getText();
            String descriptionText = description.getText();
            LocalDate transactionDate = transactionDateField.getValue();

            if (!isValidNumber(amountText) || !isValidNumber(rateText)) {
                resultLabel.setText(languageService.translate("label.invalidInput"));
                return;
            }
            if (transactionDate == null) {
                resultLabel.setText(languageService.translate("label.invalidDate"));
                return;
            }

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
            selectedItemData.setTransactionDate(LocalDateTime.of(transactionDate, selectedItemData.getTransactionDate() != null ? selectedItemData.getTransactionDate().toLocalTime() : LocalDateTime.now().toLocalTime()));

            vatService.update(selectedItemData, vat -> {
                resultLabel.setText(languageService.translate("label.vatUpdated"));
                closeVatDialog();
            });
        } catch (Exception e) {
            resultLabel.setText(languageService.translate("label.errorUpdating"));
        }
    }

    private boolean isValidNumber(String text) {
        return text != null && !text.trim().isEmpty() && text.matches("\\d+(\\.\\d+)?");
    }

    private void dispose() {
        if (darkModeDisposable != null && !darkModeDisposable.isDisposed()) {
            darkModeDisposable.dispose();
        }
    }
}