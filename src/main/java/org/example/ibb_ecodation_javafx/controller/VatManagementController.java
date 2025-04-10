package org.example.ibb_ecodation_javafx.controller;

import javafx.fxml.FXML;
import javafx.scene.layout.VBox;
import org.example.ibb_ecodation_javafx.ui.chart.ShadcnBarChart;
import org.example.ibb_ecodation_javafx.ui.input.ShadcnInput;
import org.example.ibb_ecodation_javafx.ui.table.DynamicTable;
import org.example.ibb_ecodation_javafx.utils.DialogUtil;
import org.example.ibb_ecodation_javafx.utils.ExcelUtil;
import org.example.ibb_ecodation_javafx.utils.PdfExportUtil;
import org.example.ibb_ecodation_javafx.model.Vat;

import java.io.File;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

public class VatManagementController {

    @FXML private DynamicTable<String> vatTable;
    @FXML private ShadcnInput vatNumber;
    @FXML private ShadcnBarChart barChart;
    @FXML private VBox vatPane;

    private Map<String, String> comboItems;
    private List<Vat> originalTableData;
    private String vatNumberFilter = "";

    public void initialize() {
        if (vatTable == null) return;

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

        vatTable.setComboBoxTitle("Eylemler");
        vatTable.setComboBoxItems(comboItems);

        vatNumber.setTextChangeListener(newValue -> {
            vatNumberFilter = (newValue != null) ? newValue.trim() : "";
            applyFilters();
        });

        vatTable.watchComboBox().subscribe(pair -> {
            switch (pair.getKey()) {
                case "add" -> DialogUtil.showHelpPopup("/org/example/ibb_ecodation_javafx/views/vat-create-dialog-view.fxml", "Vat Dialog");
                case "remove" -> DialogUtil.showHelpPopup("/org/example/ibb_ecodation_javafx/views/vat-remove-dialog-view.fxml", "Vat Dialog");
                case "update" -> DialogUtil.showHelpPopup("/org/example/ibb_ecodation_javafx/views/vat-update-dialog-view.fxml", "Vat Dialog");
                case "sendMail" -> DialogUtil.showHelpPopup("/org/example/ibb_ecodation_javafx/views/vat-mail-dialog-view.fxml", "Vat Dialog");
                case "print" -> {
                    File pdf = PdfExportUtil.exportVatInvoiceFromDynamicTable(vatTable.getScene().getWindow(), vatTable);
                    PdfExportUtil.printPdfFromFile(pdf);
                }
                case "export_pdf" -> {
                    File pdf = PdfExportUtil.exportVatInvoiceFromDynamicTable(vatTable.getScene().getWindow(), vatTable);
                }
                case "export_excel" -> {
                    ExcelUtil.exportToExcel(vatTable);
                }
            }
        });

        vatTable.addHeaders("ID", "N. Tutar", "%", "KDV Tutarı", "Toplam", "Fiş No", "Tarih", "Açıklama");

        // Replace mock data with correct Vat objects
        List<Vat> mockData = Arrays.asList(
                createVat(1, new BigDecimal("1000"), new BigDecimal("18"), new BigDecimal("180"), new BigDecimal("1180"), "F123", parseDate("2025-01-05"), "Mal Alımı", "PDF"),
                createVat(2, new BigDecimal("2500"), new BigDecimal("20"), new BigDecimal("500"), new BigDecimal("3000"), "F124", parseDate("2025-01-15"), "Hizmet Bedeli", "EXCEL"),
                createVat(3, new BigDecimal("800"), new BigDecimal("10"), new BigDecimal("80"), new BigDecimal("880"), "F125", parseDate("2025-01-25"), "Ofis Malzemesi", "PDF")
        );

        mockData.forEach(this::addTableData);
        updateBarChartFromTableData();
    }

    private Vat createVat(int id, BigDecimal baseAmount, BigDecimal rate, BigDecimal amount, BigDecimal totalAmount, String receiptNumber, Date transactionDate, String description, String exportFormat) {
        Vat vat = new Vat();
        vat.setId(id);
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
            return new SimpleDateFormat("yyyy-MM-dd").parse(dateStr);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private void applyFilters() {
        List<Vat> filteredData = originalTableData.stream()
                .filter(vat -> vat.getReceiptNumber().contains(vatNumberFilter))
                .collect(Collectors.toList());
        vatTable.setTableData(filteredData);
    }

    private void updateBarChartFromTableData() {
        // Use BigDecimal to avoid precision issues
        Map<String, BigDecimal> chartData = new LinkedHashMap<>();

        for (Vat vat : originalTableData) {
            String date = new SimpleDateFormat("yyyy-MM-dd").format(vat.getTransactionDate());
            BigDecimal total = vat.getTotalAmount();
            chartData.put(date, chartData.getOrDefault(date, BigDecimal.ZERO).add(total));
        }

        barChart.setMonthlyData(chartData);
    }

    private void addTableData(Vat vat) {
        vatTable.addData(
                String.valueOf(vat.getId()),
                vat.getBaseAmount().toString(),
                vat.getRate().toString(),
                vat.getAmount().toString(),
                vat.getTotalAmount().toString(),
                vat.getReceiptNumber(),
                new SimpleDateFormat("yyyy-MM-dd").format(vat.getTransactionDate()),
                vat.getDescription()
        );
        originalTableData.add(vat);
    }
}
