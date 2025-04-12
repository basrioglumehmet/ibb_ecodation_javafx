package org.example.ibb_ecodation_javafx.controller;

import io.reactivex.rxjava3.disposables.Disposable;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.layout.VBox;
import org.example.ibb_ecodation_javafx.core.context.SpringContext;
import org.example.ibb_ecodation_javafx.core.service.LanguageService;
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

    public void initialize() {
        store = Store.getInstance();

        // Load language resources
        languageService.loadAll(languageCode);

        // Apply translations to UI elements
        input.setHeader(languageService.translate("input.email"));
        if (closeButton != null) {
            closeButton.setText(languageService.translate("button.close"));
        }
        if (sendButton != null) {
            sendButton.setText(languageService.translate("button.send"));
        }

        // Initialize dark mode
        boolean initialDarkMode = store.getCurrentState(DarkModeState.class).isEnabled();
        updateDarkModeStyles(initialDarkMode);

        // Dark mode subscription
        darkModeDisposable = store.getState().subscribe(stateRegistry -> {
            boolean darkModeValue = stateRegistry.getState(DarkModeState.class).isEnabled();
            updateDarkModeStyles(darkModeValue);
        });
    }

    private void updateDarkModeStyles(boolean darkModeValue) {
        changeNavbarColor(darkModeValue, navbar);
        changeRootPaneColor(darkModeValue, rootPaneMail);
    }

    private void exportVatDataToPdf() {
        VatTableState vatTableState = store.getCurrentState(VatTableState.class);

        if (vatTableState == null || vatTableState.vatList() == null || vatTableState.vatList().isEmpty()) {
            System.err.println("VatTableState is not properly initialized or vatList is empty.");
            return;
        }

        System.out.println("Vat list size: " + vatTableState.vatList().size());

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

        if (pdf != null && !input.getText().isEmpty()) {
            System.out.println("PDF exported successfully: " + pdf.getAbsolutePath());
            String emailSubject = languageService.translate("invoice.email.subject");
            String attachmentName = languageService.translate("invoice.attachment.name");
            System.out.println("Translated subject: " + emailSubject);
            System.out.println("Translated attachment: " + attachmentName);
            mailService.sendMailWithAttachment(
                    input.getText(),
                    emailSubject,
                    pdf.toPath(),
                    attachmentName
            );
        } else {
            System.err.println("PDF export failed or email input is empty.");
        }
    }

    @FXML
    private void send() {
        Platform.runLater(this::exportVatDataToPdf);
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