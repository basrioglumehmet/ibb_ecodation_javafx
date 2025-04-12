package org.example.ibb_ecodation_javafx.controller;

import io.reactivex.rxjava3.disposables.Disposable;
import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import org.example.ibb_ecodation_javafx.core.context.SpringContext;
import org.example.ibb_ecodation_javafx.core.service.LanguageService;
import org.example.ibb_ecodation_javafx.core.validation.FieldValidator;
import org.example.ibb_ecodation_javafx.core.validation.ValidationError;
import org.example.ibb_ecodation_javafx.core.validation.ValidationRule;
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
import java.time.LocalTime;
import java.util.regex.Pattern;

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

    // Sayısal format için düzenli ifade
    private static final Pattern NUMBER_PATTERN = Pattern.compile("\\d+(\\.\\d+)?");
    // Fiş numarası için düzenli ifade (alfanümerik ve 3-20 karakter)
    private static final Pattern RECEIPT_PATTERN = Pattern.compile("^[a-zA-Z0-9]{3,20}$");

    // Başlangıç ayarlarını yapar
    public void initialize() {
        store = Store.getInstance();

        // Dil kaynaklarını yükler
        languageService.loadAll(languageCode);

        // Giriş alanlarına çevirileri uygular
        amount.setHeader(languageService.translate("input.amount"));
        rate.setHeader(languageService.translate("input.rate"));
        receipt.setHeader(languageService.translate("input.receipt"));
        description.setHeader(languageService.translate("input.description"));
        transactionDateField.setPromptText(languageService.translate("input.transactionDate"));

        // Butonlara çevirileri uygular
        if (close != null) {
            close.setText(languageService.translate("button.close"));
        }
        if (update != null) {
            update.setText(languageService.translate("button.update"));
        }

        // Sonuç etiketinin stilini ayarlar
        resultLabel.setStyle("-fx-font-family: 'Poppins'; -fx-font-size: 16px;");
        resultLabel.setText(languageService.translate("label.result"));

        // Seçili KDV öğesini yükler
        selectedItemData = store.getCurrentState(VatTableState.class).getSelectedVatItem();
        if (selectedItemData != null) {
            amount.setText(selectedItemData.getBaseAmount().toPlainString());
            rate.setText(selectedItemData.getRate().toPlainString());
            receipt.setText(selectedItemData.getReceiptNumber());
            description.setText(selectedItemData.getDescription());
            transactionDateField.setValue(selectedItemData.getTransactionDate() != null ? selectedItemData.getTransactionDate().toLocalDate() : null);
        }

        // Giriş değişikliklerini izler
        amount.setTextChangeListener((newValue) -> calculateVat());
        rate.setTextChangeListener((newValue) -> calculateVat());

        // Koyu mod başlangıç durumunu ayarlar
        boolean initialDarkMode = store.getCurrentState(DarkModeState.class).isEnabled();
        updateDarkModeStyles(initialDarkMode);

        // Koyu mod değişikliklerini izler
        darkModeDisposable = store.getState().subscribe(stateRegistry -> {
            boolean darkModeValue = stateRegistry.getState(DarkModeState.class).isEnabled();
            updateDarkModeStyles(darkModeValue);
        });

        calculateVat();
    }

    // Koyu mod stillerini günceller
    private void updateDarkModeStyles(boolean darkModeValue) {
        changeNavbarColor(darkModeValue, navbar);
        changeRootPaneColor(darkModeValue, rootPaneUpdate);
        changeTextColor(darkModeValue, resultLabel);
    }

    // Diyalog penceresini kapatır
    @FXML
    private void closeVatDialog() {
        dispose();
        DialogUtil.closeDialog();
    }

    // KDV hesaplamasını yapar ve toplam tutarı döndürür
    private BigDecimal calculateVatAmount(BigDecimal amountValue, BigDecimal rateValue) {
        BigDecimal vatAmount = amountValue.multiply(rateValue).divide(new BigDecimal("100"));
        return amountValue.add(vatAmount); // Toplam tutarı döndürür
    }

    // KDV hesaplamasını günceller ve sonucu gösterir
    private void calculateVat() {
        try {
            String amountText = amount.getText().trim();
            String rateText = rate.getText().trim();

            amount.clearError();
            rate.clearError();

            FieldValidator validator = new FieldValidator();

            // Tutar format kuralı
            validator.addRule(new ValidationRule<String>() {
                @Override
                public String getValue() {
                    return amountText;
                }

                @Override
                public boolean validate(String value) {
                    return value.isEmpty() || NUMBER_PATTERN.matcher(value).matches();
                }

                @Override
                public String getErrorMessage() {
                    return languageService.translate("input.amount.invalid");
                }

                @Override
                public ShadcnInput getInput() {
                    return amount;
                }
            });

            // Oran format kuralı
            validator.addRule(new ValidationRule<String>() {
                @Override
                public String getValue() {
                    return rateText;
                }

                @Override
                public boolean validate(String value) {
                    return value.isEmpty() || NUMBER_PATTERN.matcher(value).matches();
                }

                @Override
                public String getErrorMessage() {
                    return languageService.translate("input.rate.invalid");
                }

                @Override
                public ShadcnInput getInput() {
                    return rate;
                }
            });

            validator.onError(error -> error.getInput().setError(error.getErrorDetail()));

            if (validator.runValidatorEngine().isEmpty() && !amountText.isEmpty() && !rateText.isEmpty()) {
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

    // KDV verisini günceller
    @FXML
    private void updateData() {
        // Önceki hataları temizler
        amount.clearError();
        rate.clearError();
        receipt.clearError();
        description.clearError();
        resultLabel.setText(languageService.translate("label.result"));

        // Doğrulayıcıyı oluşturur
        FieldValidator validator = new FieldValidator();

        // Tutar boş olmama kuralı
        validator.addRule(new ValidationRule<String>() {
            @Override
            public String getValue() {
                return amount.getText().trim();
            }

            @Override
            public boolean validate(String value) {
                return !value.isEmpty();
            }

            @Override
            public String getErrorMessage() {
                return languageService.translate("input.amount.empty");
            }

            @Override
            public ShadcnInput getInput() {
                return amount;
            }
        });

        // Tutar format kuralı
        validator.addRule(new ValidationRule<String>() {
            @Override
            public String getValue() {
                return amount.getText().trim();
            }

            @Override
            public boolean validate(String value) {
                return value.isEmpty() || NUMBER_PATTERN.matcher(value).matches();
            }

            @Override
            public String getErrorMessage() {
                return languageService.translate("input.amount.invalid");
            }

            @Override
            public ShadcnInput getInput() {
                return amount;
            }
        });

        // Oran boş olmama kuralı
        validator.addRule(new ValidationRule<String>() {
            @Override
            public String getValue() {
                return rate.getText().trim();
            }

            @Override
            public boolean validate(String value) {
                return !value.isEmpty();
            }

            @Override
            public String getErrorMessage() {
                return languageService.translate("input.rate.empty");
            }

            @Override
            public ShadcnInput getInput() {
                return rate;
            }
        });

        // Oran format kuralı
        validator.addRule(new ValidationRule<String>() {
            @Override
            public String getValue() {
                return rate.getText().trim();
            }

            @Override
            public boolean validate(String value) {
                return value.isEmpty() || NUMBER_PATTERN.matcher(value).matches();
            }

            @Override
            public String getErrorMessage() {
                return languageService.translate("input.rate.invalid");
            }

            @Override
            public ShadcnInput getInput() {
                return rate;
            }
        });

        // Fiş numarası boş olmama kuralı
        validator.addRule(new ValidationRule<String>() {
            @Override
            public String getValue() {
                return receipt.getText().trim();
            }

            @Override
            public boolean validate(String value) {
                return !value.isEmpty();
            }

            @Override
            public String getErrorMessage() {
                return languageService.translate("input.receipt.empty");
            }

            @Override
            public ShadcnInput getInput() {
                return receipt;
            }
        });

        // Fiş numarası format kuralı
        validator.addRule(new ValidationRule<String>() {
            @Override
            public String getValue() {
                return receipt.getText().trim();
            }

            @Override
            public boolean validate(String value) {
                return value.isEmpty() || RECEIPT_PATTERN.matcher(value).matches();
            }

            @Override
            public String getErrorMessage() {
                return languageService.translate("input.receipt.invalid");
            }

            @Override
            public ShadcnInput getInput() {
                return receipt;
            }
        });

        // Açıklama boş olmama kuralı
        validator.addRule(new ValidationRule<String>() {
            @Override
            public String getValue() {
                return description.getText().trim();
            }

            @Override
            public boolean validate(String value) {
                return !value.isEmpty();
            }

            @Override
            public String getErrorMessage() {
                return languageService.translate("input.description.empty");
            }

            @Override
            public ShadcnInput getInput() {
                return description;
            }
        });

        // Açıklama uzunluk kuralı (5-200 karakter)
        validator.addRule(new ValidationRule<String>() {
            @Override
            public String getValue() {
                return description.getText().trim();
            }

            @Override
            public boolean validate(String value) {
                return value.isEmpty() || (value.length() >= 5 && value.length() <= 200);
            }

            @Override
            public String getErrorMessage() {
                return languageService.translate("input.description.invalid");
            }

            @Override
            public ShadcnInput getInput() {
                return description;
            }
        });

        // İşlem tarihi boş olmama kuralı
        validator.addRule(new ValidationRule<LocalDate>() {
            @Override
            public LocalDate getValue() {
                return transactionDateField.getValue();
            }

            @Override
            public boolean validate(LocalDate value) {
                return value != null;
            }

            @Override
            public String getErrorMessage() {
                return languageService.translate("label.invalidDate");
            }

            @Override
            public ShadcnInput getInput() {
                return null; // DatePicker ShadcnInput kullanmaz
            }
        });

        // Hata geri çağrısını ayarlar
        validator.onError(error -> {
            if (error.getInput() != null) {
                error.getInput().setError(error.getErrorDetail());
            } else {
                resultLabel.setText(error.getErrorDetail());
            }
        });

        // Doğrulamayı çalıştırır
        if (validator.runValidatorEngine().isEmpty()) {
            try {
                BigDecimal amountValue = new BigDecimal(amount.getText().trim());
                BigDecimal rateValue = new BigDecimal(rate.getText().trim());
                BigDecimal vatAmount = amountValue.multiply(rateValue).divide(new BigDecimal("100"));
                BigDecimal totalAmount = amountValue.add(vatAmount);
                LocalDate transactionDate = transactionDateField.getValue();

                selectedItemData.setBaseAmount(amountValue);
                selectedItemData.setAmount(vatAmount);
                selectedItemData.setTotalAmount(totalAmount);
                selectedItemData.setRate(rateValue);
                selectedItemData.setReceiptNumber(receipt.getText().trim());
                selectedItemData.setDescription(description.getText().trim());
                selectedItemData.setTransactionDate(LocalDateTime.of(
                        transactionDate,
                        selectedItemData.getTransactionDate() != null ? selectedItemData.getTransactionDate().toLocalTime() : LocalTime.MIDNIGHT
                ));

                vatService.update(selectedItemData, vat -> {
                    resultLabel.setText(languageService.translate("label.vatUpdated"));
                    closeVatDialog();
                });
            } catch (Exception e) {
                resultLabel.setText(languageService.translate("label.errorUpdating"));
            }
        }
    }


    private void dispose() {
        if (darkModeDisposable != null && !darkModeDisposable.isDisposed()) {
            darkModeDisposable.dispose();
        }
    }
}