package org.example.ibb_ecodation_javafx.controller;

import javafx.fxml.FXML;
import javafx.scene.layout.VBox;
import org.example.ibb_ecodation_javafx.core.context.SpringContext;
import org.example.ibb_ecodation_javafx.service.MailService;
import org.example.ibb_ecodation_javafx.service.VatService;
import org.example.ibb_ecodation_javafx.statemanagement.Store;
import org.example.ibb_ecodation_javafx.statemanagement.state.VatTableState;
import org.example.ibb_ecodation_javafx.ui.chart.ShadcnBarChart;
import org.example.ibb_ecodation_javafx.ui.input.ShadcnInput;
import org.example.ibb_ecodation_javafx.ui.table.DynamicTable;
import org.example.ibb_ecodation_javafx.utils.DialogUtil;
import org.example.ibb_ecodation_javafx.utils.ExcelUtil;
import org.example.ibb_ecodation_javafx.utils.PdfExportUtil;
import org.example.ibb_ecodation_javafx.model.Vat;
import org.example.ibb_ecodation_javafx.utils.TxtUtil;

import java.io.File;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
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
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm");

    static {
        DATE_FORMAT.setTimeZone(TimeZone.getTimeZone("Europe/Istanbul")); // TRT (UTC+03:00)
    }

    public void initialize() {
        // FXML elemanlarının null kontrolü
        if (vatTable == null || vatNumber == null || barChart == null || vatPane == null) {
            System.err.println("FXML elemanları düzgün yüklenmedi!");
            return;
        }

        originalTableData = new ArrayList<>();

        vatTable.setHeaderText("KDV Yönetimi");
        vatTable.setDescriptionText("Kdv ile ilgili tüm işlemleri yapabilirsiniz.");

        comboItems = new HashMap<>() {{
            put("add", "KDV Girişi Ekle");
            put("remove", "KDV Girişi Sil");
            put("update", "KDV Girişi Güncelle");
            put("print", "Yazıcıya Yazdır");
            put("export_txt", "TXT Olarak Dışa Aktar");
            put("export_pdf", "PDF Olarak Dışa Aktar");
            put("export_excel", "EXCEL Olarak Dışa Aktar");
            put("sendMail", "E-posta Gönder");
        }};

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
        vatTable.setComboBoxItems(comboItems);

        vatNumber.setTextChangeListener(newValue -> {
            vatNumberFilter = (newValue != null) ? newValue.trim() : "";
            applyFilters();
        });

        vatTable.watchComboBox().subscribe(pair -> {
            switch (pair.getKey()) {
                case "add" -> DialogUtil.showHelpPopup("/org/example/ibb_ecodation_javafx/views/vat-create-dialog-view.fxml", "Vat Dialog");
                case "remove" -> removeSelectedRows();
                case "update" -> DialogUtil.showHelpPopup("/org/example/ibb_ecodation_javafx/views/vat-update-dialog-view.fxml", "Vat Dialog");
                case "sendMail" -> {
                    store.dispatch(VatTableState.class, new VatTableState(originalTableData));
                    DialogUtil.showHelpPopup("/org/example/ibb_ecodation_javafx/views/vat-mail-dialog-view.fxml", "Vat Dialog");
                }
                case "print" -> {
                    File pdf = PdfExportUtil.exportVatInvoiceFromList(
                            vatTable.getScene().getWindow(),
                            originalTableData,
                            headers
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
                            headers
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



        vatService.readAll(1).stream().forEach(vat -> {
            originalTableData.add(vat);
            addTableData(vat);

        });
        updateBarChartFromTableData();
    }

    private void removeSelectedRows() {
        List<List<String>> selectedData = vatTable.getSelectedData();
        if (selectedData.isEmpty()) {
            return;
        }

        List<String> selectedIds = selectedData.stream()
                .map(row -> row.get(0))
                .collect(Collectors.toList());

        originalTableData.removeIf(vat -> selectedIds.contains(String.valueOf(vat.getId())));

        vatTable.clearData();
        originalTableData.forEach(this::addTableData);

        updateBarChartFromTableData();
        applyFilters();
    }

    private Vat createVat(int id, BigDecimal baseAmount, BigDecimal rate, BigDecimal amount,
                          BigDecimal totalAmount, String receiptNumber, Date transactionDate,
                          String description, String exportFormat) {
        Vat vat = new Vat();
        vat.setId(id);
        vat.setUserId(id + 1); // Mock user ID
        vat.setBaseAmount(baseAmount);
        vat.setRate(rate);
        vat.setAmount(amount);
        vat.setTotalAmount(totalAmount);
        vat.setReceiptNumber(receiptNumber);
        vat.setTransactionDate(transactionDate);
        vat.setDescription(description);
        vat.setExportFormat(exportFormat);
        return vat;
    }

    private Date parseDate(String dateStr) {
        try {
            return DATE_FORMAT.parse(dateStr);
        } catch (Exception e) {
            System.err.println("Tarih ayrıştırma hatası: " + e.getMessage());
            return new Date(); // Varsayılan olarak şu anki tarih
        }
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

        for (Vat vat : originalTableData) {
            String date = DATE_FORMAT.format(vat.getTransactionDate());
            BigDecimal total = vat.getTotalAmount();
            chartData.put(date, chartData.getOrDefault(date, BigDecimal.ZERO).add(total));
        }

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
                DATE_FORMAT.format(vat.getTransactionDate()),
                vat.getDescription(),
                vat.getExportFormat(),
                String.valueOf(vat.isDeleted()),
                String.valueOf(vat.getVersion())
        );
    }
}