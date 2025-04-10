package org.example.ibb_ecodation_javafx.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.layout.VBox;
import org.example.ibb_ecodation_javafx.statemanagement.Store;
import org.example.ibb_ecodation_javafx.statemanagement.state.DarkModeState;
import org.example.ibb_ecodation_javafx.statemanagement.state.VatTableState;
import org.example.ibb_ecodation_javafx.ui.navbar.ShadcnNavbar;
import org.example.ibb_ecodation_javafx.utils.DialogUtil;
import org.example.ibb_ecodation_javafx.utils.PdfExportUtil;

import java.io.File;
import java.util.List;

import static org.example.ibb_ecodation_javafx.utils.ThemeUtil.changeNavbarColor;
import static org.example.ibb_ecodation_javafx.utils.ThemeUtil.changeRootPaneColor;

public class VatDialogMailController {
    @FXML
    private ShadcnNavbar navbar;
    @FXML
    private VBox rootPaneMail;

    private Store store;

    public void initialize() {
        store = Store.getInstance();

        // Karanlık mod değişikliklerini dinle
        store.getState().subscribe(stateRegistry -> {
            var darkModeValue = stateRegistry.getState(DarkModeState.class).isEnabled();
            changeNavbarColor(darkModeValue, navbar);
            changeRootPaneColor(darkModeValue, rootPaneMail);
        });

        // PDF export işlemini sahne hazır olduğunda çalıştır
        Platform.runLater(this::exportVatDataToPdf);
    }

    private void exportVatDataToPdf() {
        VatTableState vatTableState = store.getCurrentState(VatTableState.class);

        if (vatTableState == null || vatTableState.vatList() == null || vatTableState.vatList().isEmpty()) {
            System.err.println("VatTableState is not properly initialized or vatList is empty.");

            return;
        }

        System.out.println("Vat list size: " + vatTableState.vatList().size());

        // Vat sınıfındaki @PdfDefinition ile uyumlu başlıklar
        List<String> headers = List.of(
                "id",           // "ID"
                "baseAmount",   // "Temel Tutar"
                "rate",         // "%"
                "amount",       // "Toplam"
                "totalAmount",  // "Genel Toplam"
                "receiptNumber", // "Fiş Numarası"
                "transactionDate", // "İşlem Tarihi"
                "description"   // "Açıklama"
        );

        // PDF'i dışa aktar
        File pdf = PdfExportUtil.exportVatInvoiceFromList(rootPaneMail.getScene().getWindow(), vatTableState.vatList(), headers);

        if (pdf != null) {
            System.out.println("PDF exported successfully: " + pdf.getAbsolutePath());
            // E-posta gönderme (yorumdan çıkarılacaksa)
            // mailService.sendMailWithAttachment("basrioglumehmet@gmail.com", "Mail DENEME", pdf.toPath(), "PDF Dosyası");
        } else {
            System.err.println("PDF export failed.");

        }
    }

    @FXML
    private void closeVatDialog() {
        DialogUtil.closeDialog();
    }
}