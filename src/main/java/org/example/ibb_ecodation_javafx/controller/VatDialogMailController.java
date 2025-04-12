package org.example.ibb_ecodation_javafx.controller;

import io.reactivex.rxjava3.disposables.Disposable;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.layout.VBox;
import org.example.ibb_ecodation_javafx.core.context.SpringContext;
import org.example.ibb_ecodation_javafx.core.service.LanguageService;
import org.example.ibb_ecodation_javafx.core.validation.FieldValidator;
import org.example.ibb_ecodation_javafx.core.validation.ValidationError;
import org.example.ibb_ecodation_javafx.core.validation.ValidationRule;
import org.example.ibb_ecodation_javafx.service.MailService;
import org.example.ibb_ecodation_javafx.statemanagement.Store;
import org.example.ibb_ecodation_javafx.statemanagement.state.DarkModeState;
import org.example.ibb_ecodation_javafx.statemanagement.state.VatTableState;
import org.example.ibb_ecodation_javafx.ui.button.ShadcnButton;
import org.example.ibb_ecodation_javafx.ui.combobox.ShadcnLanguageComboBox;
import org.example.ibb_ecodation_javafx.ui.input.ShadcnInput;
import org.example.ibb_ecodation_javafx.ui.navbar.ShadcnNavbar;
import org.example.ibb_ecodation_javafx.utils.DialogUtil;
import org.example.ibb_ecodation_javafx.utils.PdfExportUtil;

import java.io.File;
import java.util.List;
import java.util.regex.Pattern;

import static org.example.ibb_ecodation_javafx.utils.ThemeUtil.*;

public class VatDialogMailController {
    @FXML private ShadcnNavbar navbar;
    @FXML private VBox rootPaneMail;
    @FXML private ShadcnInput input;
    @FXML private ShadcnButton closeButton;
    @FXML private ShadcnButton sendButton;

    private Store store;
    private Disposable darkModeDisposable;
    private final MailService mailService = SpringContext.getContext().getBean(MailService.class);
    private final PdfExportUtil pdfExportUtil = SpringContext.getContext().getBean(PdfExportUtil.class);
    private final LanguageService languageService = SpringContext.getContext().getBean(LanguageService.class);
    private final String languageCode = ShadcnLanguageComboBox.getCurrentLanguageCode();

    // E-posta formatı için regexp
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");

    // Başlangıç ayarlarını yapar
    public void initialize() {
        store = Store.getInstance();

        // Dil kaynaklarını yükler
        languageService.loadAll(languageCode);

        // UI elemanlarına çevirileri uygular
        input.setHeader(languageService.translate("input.email"));
        if (closeButton != null) {
            closeButton.setText(languageService.translate("button.close"));
        }
        if (sendButton != null) {
            sendButton.setText(languageService.translate("button.send"));
        }

        // Koyu mod başlangıç durumunu ayarlar
        boolean initialDarkMode = store.getCurrentState(DarkModeState.class).isEnabled();
        updateDarkModeStyles(initialDarkMode);

        // Koyu mod değişikliklerini izler
        darkModeDisposable = store.getState().subscribe(stateRegistry -> {
            boolean darkModeValue = stateRegistry.getState(DarkModeState.class).isEnabled();
            updateDarkModeStyles(darkModeValue);
        });
    }

    // Koyu mod stillerini günceller
    private void updateDarkModeStyles(boolean darkModeValue) {
        changeNavbarColor(darkModeValue, navbar);
        changeRootPaneColor(darkModeValue, rootPaneMail);
    }

    // KDV verilerini PDF olarak dışa aktarır ve e-posta ile gönderir
    private void exportVatDataToPdf() {
        VatTableState vatTableState = store.getCurrentState(VatTableState.class);

        // VatTableState veya vatList boşsa hata mesajı gösterir
        if (vatTableState == null || vatTableState.vatList() == null || vatTableState.vatList().isEmpty()) {
            System.err.println("VatTableState düzgün başlatılmadı veya vatList boş.");
            return;
        }

        System.out.println("Vat liste boyutu: " + vatTableState.vatList().size());

        List<String> headers = List.of(
                "vat.id", "vat.amount", "%", "vat.total", "vat.generalTotal",
                "vat.receiptNumber", "vat.transactionDate", "description"
        );

        File pdf = pdfExportUtil.exportToPdf(
                rootPaneMail.getScene().getWindow(),
                vatTableState.vatList(),
                headers,
                "footer.message",
                languageCode
        );

        // PDF oluşturulduysa ve e-posta alanı doluysa e-posta gönderir
        if (pdf != null && !input.getText().isEmpty()) {
            System.out.println("PDF başarıyla dışa aktarıldı: " + pdf.getAbsolutePath());
            String emailSubject = languageService.translate("invoice.email.subject");
            String attachmentName = languageService.translate("invoice.attachment.name");
            System.out.println("Çevrilen konu: " + emailSubject);
            System.out.println("Çevrilen ek: " + attachmentName);
            mailService.sendMailWithAttachment(
                    input.getText().trim(),
                    emailSubject,
                    pdf.toPath(),
                    attachmentName
            );
        } else {
            System.err.println("PDF dışa aktarma başarısız oldu veya e-posta alanı boş.");
        }
    }

    @FXML
    private void send() {
        // Önceki hataları temizler
        input.clearError();

        // Doğrulayıcıyı oluşturur
        FieldValidator validator = new FieldValidator();

        // E-posta boş olmama kuralı
        validator.addRule(new ValidationRule<String>() {
            @Override
            public String getValue() {
                return input.getText().trim();
            }

            @Override
            public boolean validate(String value) {
                return !value.isEmpty();
            }

            @Override
            public String getErrorMessage() {
                return languageService.translate("input.email.empty");
            }

            @Override
            public ShadcnInput getInput() {
                return input;
            }
        });

        // E-posta format kuralı
        validator.addRule(new ValidationRule<String>() {
            @Override
            public String getValue() {
                return input.getText().trim();
            }

            @Override
            public boolean validate(String value) {
                return value.isEmpty() || EMAIL_PATTERN.matcher(value).matches();
            }

            @Override
            public String getErrorMessage() {
                return languageService.translate("input.email.invalid");
            }

            @Override
            public ShadcnInput getInput() {
                return input;
            }
        });


        validator.onError(error -> error.getInput().setError(error.getErrorDetail()));


        if (validator.runValidatorEngine().isEmpty()) {
            Platform.runLater(this::exportVatDataToPdf);
        }
    }


    @FXML
    private void closeVatDialog() {
        dispose();
        DialogUtil.closeDialog();
    }

    private void dispose() {
        if (darkModeDisposable != null && !darkModeDisposable.isDisposed()) {
            darkModeDisposable.dispose();
        }
    }
}