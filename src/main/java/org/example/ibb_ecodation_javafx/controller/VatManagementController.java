package org.example.ibb_ecodation_javafx.controller;

import io.reactivex.rxjava3.disposables.Disposable;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.layout.VBox;
import org.example.ibb_ecodation_javafx.core.context.SpringContext;
import org.example.ibb_ecodation_javafx.core.service.LanguageService;
import org.example.ibb_ecodation_javafx.service.MailService;
import org.example.ibb_ecodation_javafx.service.VatService;
import org.example.ibb_ecodation_javafx.statemanagement.Store;
import org.example.ibb_ecodation_javafx.statemanagement.state.VatTableState;
import org.example.ibb_ecodation_javafx.ui.chart.ShadcnBarChart;
import org.example.ibb_ecodation_javafx.ui.combobox.ShadcnLanguageComboBox;
import org.example.ibb_ecodation_javafx.ui.input.ShadcnInput;
import org.example.ibb_ecodation_javafx.ui.table.DynamicTable;
import org.example.ibb_ecodation_javafx.utils.DialogUtil;
import org.example.ibb_ecodation_javafx.utils.ExcelUtil;
import org.example.ibb_ecodation_javafx.utils.PdfExportUtil;
import org.example.ibb_ecodation_javafx.model.Vat;
import org.example.ibb_ecodation_javafx.utils.TxtUtil;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.File;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * KDV yönetim işlemlerini kontrol eden sınıf
 */
public class VatManagementController {

    @FXML private DynamicTable<Vat> vatTable;
    @FXML private ShadcnInput vatNumber;
    @FXML private ShadcnBarChart barChart;
    @FXML private VBox vatPane;
    private final VatService vatService = SpringContext.getContext().getBean(VatService.class);

    private Store store = Store.getInstance();
    private Map<String, String> comboItems;
    private List<Vat> originalTableData;
    private String vatNumberFilter = "";
    private MailService mailService = SpringContext.getContext().getBean(MailService.class);
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private final LanguageService languageService = SpringContext.getContext().getBean(LanguageService.class);
    private Disposable languageSubscription;

    public void initialize() {
        // FXML elemanlarının null kontrolü
        if (vatTable == null || vatNumber == null || barChart == null || vatPane == null) {
            System.err.println("FXML elemanları düzgün yüklenmedi!");
            return;
        }
        String languageCode = ShadcnLanguageComboBox.getCurrentLanguageCode();


        updateUIText(languageCode);
        // Subscribe to language changes
        languageSubscription = ShadcnLanguageComboBox.watchLanguageValue().subscribe(pair -> {
            String newLanguageCode = pair.getKey();
            Platform.runLater(() -> updateUIText(newLanguageCode));
        });

        originalTableData = new ArrayList<>();

        vatTable.setSingleSelection(true);



        // Vat sınıfındaki tüm alanlara uygun başlıklar
        List<String> headers = List.of(
                "ID",
                "Kullanıcı ID",
                "Tutar",
                "%",
                "Toplam",
                "Genel Toplam",
                "Fiş Numarası",
                "İşlem Tarihi",
                "Açıklama"
        );

        vatTable.setComboBoxTitle("Eylemler");

        vatNumber.setTextChangeListener(newValue -> {
            vatNumberFilter = (newValue != null) ? newValue.trim() : "";
            applyFilters();
        });

        vatTable.watchComboBox().subscribe(pair -> {
            switch (pair.getKey()) {
                case "add" -> {
                    DialogUtil.showHelpPopup("/org/example/ibb_ecodation_javafx/views/vat-create-dialog-view.fxml", "Vat Dialog");
                }
                case "delete" -> deleteSelectedRow();
                case "refresh" -> {
                    refreshData();
                }
                case "update" -> {
                    updateSelectedRow();
                }
                case "sendMail" -> {
                    store.dispatch(VatTableState.class, new VatTableState(originalTableData,
                            store.getCurrentState(VatTableState.class).getSelectedVatItem()));
                    DialogUtil.showHelpPopup("/org/example/ibb_ecodation_javafx/views/vat-mail-dialog-view.fxml", "Vat Dialog");
                }
                case "print" -> {
                    File pdf = PdfExportUtil.exportVatInvoiceFromList(
                            vatTable.getScene().getWindow(),
                            originalTableData,
                            headers,
                            "Bu fatura elektronik ortamda oluşturulmuştur."
                    );
                    if (pdf != null && pdf.exists()) {
                        PdfExportUtil.printPdfFromFile(pdf);
                    } else {
                        System.err.println("PDF dosyası oluşturulamadı!");
                    }
                }
                case "export_pdf" -> {
                    File pdf = PdfExportUtil.exportVatInvoiceFromList(
                            vatTable.getScene().getWindow(),
                            originalTableData,
                            headers,
                            "Bu fatura elektronik ortamda oluşturulmuştur."
                    );
                    if (pdf == null || !pdf.exists()) {
                        System.err.println("PDF dışa aktarma başarısız!");
                    }
                }
                case "export_excel" -> ExcelUtil.exportToExcel(originalTableData, Vat.class);
                case "export_txt" -> TxtUtil.exportToTxt(vatTable, Vat.class);
            }
        });

        vatTable.addHeaders(
                "ID",
                "Kullanıcı ID",
                "Tutar",
                "%",
                "Toplam",
                "Genel Toplam",
                "Fiş Numarası",
                "İşlem Tarihi",
                "Açıklama",
                "Dışa Aktarma Formatı",
                "Silindi mi",
                "Versiyon"
        );

        vatService.readAll(1).forEach(vat -> {
            originalTableData.add(vat);
            addTableData(vat);
        });
        updateBarChartFromTableData();

    }

    private void updateUIText(String languageCode) {
        ResourceBundle bundle = languageService.loadAll(languageCode);
        try {
            vatNumber.setHeader(bundle.getString("vat.receipt"));
            vatTable.setHeaderText(bundle.getString("vat.header"));
            vatTable.setDescriptionText(bundle.getString("vat.description"));

            comboItems = new HashMap<>() {{
                put("add", bundle.getString("vat.add"));
                put("delete", bundle.getString("vat.delete"));
                put("update", bundle.getString("vat.update"));
                put("print", bundle.getString("vat.print"));
                put("export_txt", bundle.getString("vat.export_txt"));
                put("export_pdf", bundle.getString("vat.export_pdf"));
                put("export_excel", bundle.getString("vat.export_excel"));
                put("sendMail", bundle.getString("vat.sendMail"));
                put("refresh", bundle.getString("vat.refresh"));
            }};
            vatTable.setComboBoxItems(comboItems);
//            username.setHeader(bundle.getString("auth.username"));
//            email.setHeader(bundle.getString("auth.email"));
//            password.setHeader(bundle.getString("auth.password"));
//            continueButton.setText(bundle.getString("register.button"));
//            backButton.setText(bundle.getString("register.back"));
//            termsLabel.setText(bundle.getString("gdpr.termsLabel"));
//            policyLabel.setText(bundle.getString("gdpr.policyLabel"));
        } catch (Exception e) {
            // Fallback to English defaults if keys are missing
//            signUpLabel.setText("Sign Up");
//            username.setHeader("Username");
//            email.setHeader("Email");
//            password.setHeader("Password");
//            continueButton.setText("Continue");
//            backButton.setText("Back");
//            termsLabel.setText("By clicking continue, you agree to our");
//            policyLabel.setText("Terms of Service and Privacy Policy.");
        }
    }

    private void updateSelectedRow() {
        List<List<String>> selectedData = vatTable.getSelectedData();
        if (selectedData.isEmpty()) {
            return;
        }

        String selectedId = selectedData.get(0).get(0);

        Optional<Vat> selectedVat = originalTableData.stream()
                .filter(vat -> String.valueOf(vat.getId()).equals(selectedId))
                .findFirst();

        selectedVat.ifPresent(vat -> {
            store.dispatch(VatTableState.class, new VatTableState(
                    store.getCurrentState(VatTableState.class).vatList(),
                    vat
            ));
            DialogUtil.showHelpPopup("/org/example/ibb_ecodation_javafx/views/vat-update-dialog-view.fxml", "Vat Dialog");
        });
        refreshData();
    }

    private void deleteSelectedRow() {
        List<List<String>> selectedData = vatTable.getSelectedData();
        if (selectedData.isEmpty()) {
            return;
        }

        String selectedId = selectedData.get(0).get(0);

        originalTableData.removeIf(vat -> String.valueOf(vat.getId()).equals(selectedId));
        vatService.delete(Integer.parseInt(selectedId));

        refreshData();
    }

    private void refreshData() {
        originalTableData.clear();
        vatTable.clearData();

        vatService.readAll(1).forEach(vat -> {
            originalTableData.add(vat);
            addTableData(vat);
            updateBarChartFromTableData();
            updateBarChartFromTableData();
            applyFilters();
        });
    }

    private void applyFilters() {
        List<Vat> filteredData = originalTableData.stream()
                .filter(vat -> vat.getReceiptNumber().contains(vatNumberFilter))
                .collect(Collectors.toList());
        vatTable.setTableData(filteredData, vat -> Arrays.asList(
                String.valueOf(vat.getId()),
                String.valueOf(vat.getUserId()),
                vat.getBaseAmount().toString(),
                vat.getRate().toString(),
                vat.getAmount().toString(),
                vat.getTotalAmount().toString(),
                vat.getReceiptNumber(),
                DATE_FORMAT.format(vat.getTransactionDate()),
                vat.getDescription(),
                vat.getExportFormat(),
                String.valueOf(vat.isDeleted()),
                String.valueOf(vat.getVersion())
        ));
    }

    private void updateBarChartFromTableData() {
        Map<String, BigDecimal> chartData = new LinkedHashMap<>();

        // Verileri tarih sırasına göre sıralıyoruz
        List<Vat> sortedData = originalTableData.stream()
                .sorted(Comparator.comparing(vat -> vat.getTransactionDate()))
                .collect(Collectors.toList());

        // Her veriyi chartData'ya ekliyoruz
        for (Vat vat : sortedData) {
            String date = DATE_FORMAT.format(vat.getTransactionDate());
            BigDecimal total = vat.getTotalAmount();
            chartData.put(date, chartData.getOrDefault(date, BigDecimal.ZERO).add(total));
        }

        // Bar chart'ı güncelliyoruz
        barChart.setMonthlyData(chartData);
    }

    private void addTableData(Vat vat) {
        vatTable.addData(
                String.valueOf(vat.getId()),
                String.valueOf(vat.getUserId()),
                vat.getBaseAmount().toString(),
                vat.getRate().toString(),
                vat.getAmount().toString(),
                vat.getTotalAmount().toString(),
                vat.getReceiptNumber(),
                vat.getTransactionDate().format(DATE_FORMAT),
                vat.getDescription(),
                vat.getExportFormat(),
                String.valueOf(vat.isDeleted()),
                String.valueOf(vat.getVersion())
        );
    }

    // Clean up subscription on controller destruction
    public void shutdown() {
        if (languageSubscription != null && !languageSubscription.isDisposed()) {
            languageSubscription.dispose();
        }
    }
}
