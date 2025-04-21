package org.example.ibb_ecodation_javafx.controller;


import lombok.RequiredArgsConstructor;
import org.example.ibb_ecodation_javafx.statemanagement.state.TranslatorState;
import org.example.ibb_ecodation_javafx.ui.ValidatableComponent;
import org.example.ibb_ecodation_javafx.ui.datepicker.ShadcnDatePicker;
import org.springframework.stereotype.Controller;
import io.reactivex.rxjava3.disposables.Disposable;
import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import org.example.ibb_ecodation_javafx.core.service.LanguageService;
import org.example.ibb_ecodation_javafx.core.validation.FieldValidator;
import org.example.ibb_ecodation_javafx.core.validation.ValidationError;
import org.example.ibb_ecodation_javafx.core.validation.ValidationRule;
import org.example.ibb_ecodation_javafx.model.Vat;
import org.example.ibb_ecodation_javafx.service.VatService;
import org.example.ibb_ecodation_javafx.statemanagement.Store;
import org.example.ibb_ecodation_javafx.statemanagement.state.DarkModeState;
import org.example.ibb_ecodation_javafx.statemanagement.state.UserState;
import org.example.ibb_ecodation_javafx.ui.button.ShadcnButton;
import org.example.ibb_ecodation_javafx.ui.combobox.ShadcnLanguageComboBox;
import org.example.ibb_ecodation_javafx.ui.input.ShadcnInput;
import org.example.ibb_ecodation_javafx.ui.navbar.ShadcnNavbar;
import org.example.ibb_ecodation_javafx.utils.DialogUtil;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.regex.Pattern;

import static org.example.ibb_ecodation_javafx.utils.ThemeUtil.*;
@Controller
@RequiredArgsConstructor
public class VatCreateController {
    @FXML private ShadcnNavbar navbar;
    @FXML private VBox rootPane;
    @FXML private ShadcnInput amount;
    @FXML private ShadcnInput rate;
    @FXML private ShadcnInput receipt;
    @FXML private ShadcnInput description;
    @FXML private ShadcnDatePicker transactionDateField;
    @FXML private Label resultLabel;
    @FXML private ShadcnButton closeButton;
    @FXML private ShadcnButton insertButton;

    private Store store;
    private double totalAmount;
    private double vatAmount;
    private Disposable darkModeDisposable;

    private final VatService vatService;
    private final LanguageService languageService;

    // Sayısal format için düzenli ifade
    private static final Pattern NUMBER_PATTERN = Pattern.compile("\\d+(\\.\\d+)?");
    // Fiş numarası için düzenli ifade (alfanümerik ve 3-20 karakter)
    private static final Pattern RECEIPT_PATTERN = Pattern.compile("^[a-zA-Z0-9]{3,20}$");

    // Başlangıç ayarlarını yapar
    public void initialize() {
        store = Store.getInstance();

        // Dil kaynaklarını yükler
        languageService.loadAll(store.getCurrentState(TranslatorState.class).countryCode().getCode());

        // Giriş alanlarına çevirileri uygular
        amount.setHeader(languageService.translate("input.amount"));
        rate.setHeader(languageService.translate("input.rate"));
        receipt.setHeader(languageService.translate("input.receipt"));
        description.setHeader(languageService.translate("input.description"));
        transactionDateField.setHeader(languageService.translate("input.transactionDate"));

        // Butonlara çevirileri uygular
        if (closeButton != null) {
            closeButton.setText(languageService.translate("button.close"));
        }
        if (insertButton != null) {
            insertButton.setText(languageService.translate("button.insert"));
        }

        // Sonuç etiketinin stilini ayarlar
        resultLabel.setStyle("-fx-font-family: 'Poppins'; -fx-font-size: 16px;");
        resultLabel.setText(languageService.translate("label.result"));

        // Koyu mod başlangıç durumunu ayarlar
        boolean initialDarkMode = store.getCurrentState(DarkModeState.class).isEnabled();
        updateDarkModeStyles(initialDarkMode);

        // Koyu mod değişikliklerini izler
        darkModeDisposable = store.getState().subscribe(stateRegistry -> {
            boolean darkModeValue = stateRegistry.getState(DarkModeState.class).isEnabled();
            updateDarkModeStyles(darkModeValue);
        });

        // Giriş değişikliklerini izler
        amount.setTextChangeListener((newValue) -> calculateVat());
        rate.setTextChangeListener((newValue) -> calculateVat());
    }

    // Koyu mod stillerini günceller
    private void updateDarkModeStyles(boolean darkModeValue) {
        changeNavbarColor(darkModeValue, navbar);
        changeRootPaneColor(darkModeValue, rootPane);
        changeTextColor(darkModeValue, resultLabel);
    }

    // Diyalog penceresini kapatır
    @FXML
    private void closeVatDialog() {
        dispose();
        DialogUtil.closeDialog();
    }

    // KDV kaydını ekler
    @FXML
    private void insert() {
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
            public ValidatableComponent getComponent() {
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
            public ValidatableComponent getComponent() {
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
            public ValidatableComponent getComponent() {
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
            public ValidatableComponent getComponent() {
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
            public ValidatableComponent getComponent() {
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
            public ValidatableComponent getComponent() {
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
            public ValidatableComponent getComponent() {
                return description;
            }
        });

        // Açıklama uzunluk kuralı (örneğin, 5-200 karakter)
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
            public ValidatableComponent getComponent() {
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
            public ValidatableComponent getComponent() {
                return null; // DatePicker ShadcnInput kullanmaz
            }
        });

        // Hata geri çağrısını ayarlar
        validator.onError(error -> {
            if (error.getComponent() != null) {
                error.getComponent().setError(error.getErrorDetail());
            } else {
                resultLabel.setText(error.getErrorDetail());
            }
        });

        // Doğrulamayı çalıştırır
        if (validator.runValidatorEngine().isEmpty()) {
            double amountValue = Double.parseDouble(amount.getText().trim());
            double rateValue = Double.parseDouble(rate.getText().trim());
            LocalDate transactionDate = transactionDateField.getValue();
            var userDetail = store.getCurrentState(UserState.class).getUserDetail();

            Vat vat = new Vat(
                    0, // ID
                    userDetail.getUserId(),
                    BigDecimal.valueOf(amountValue),
                    BigDecimal.valueOf(rateValue),
                    BigDecimal.valueOf(vatAmount),
                    BigDecimal.valueOf(totalAmount),
                    receipt.getText().trim(),
                    Timestamp.valueOf(LocalDateTime.of(transactionDate, LocalTime.MIDNIGHT)),
                    description.getText().trim(),
                    "VARSAYILAN",
                    false,
                    1 // Sürüm
            );

            vatService.save(vat);
            resultLabel.setText(languageService.translate("label.vatAdded"));
            closeVatDialog();
        }
    }

    // KDV hesaplamasını yapar
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
                public ValidatableComponent getComponent() {
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
                public ValidatableComponent getComponent() {
                    return rate;
                }
            });

            validator.onError(error -> error.getComponent().setError(error.getErrorDetail()));

            if (validator.runValidatorEngine().isEmpty() && !amountText.isEmpty() && !rateText.isEmpty()) {
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

    // Kaynakları serbest bırakır
    private void dispose() {
        if (darkModeDisposable != null && !darkModeDisposable.isDisposed()) {
            darkModeDisposable.dispose();
        }
    }
}
