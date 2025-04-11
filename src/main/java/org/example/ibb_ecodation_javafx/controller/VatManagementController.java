package org.example.ibb_ecodation_javafx.controller;

import io.reactivex.rxjava3.disposables.Disposable;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.example.ibb_ecodation_javafx.core.context.SpringContext;
import org.example.ibb_ecodation_javafx.core.service.LanguageService;
import org.example.ibb_ecodation_javafx.model.Vat;
import org.example.ibb_ecodation_javafx.service.MailService;
import org.example.ibb_ecodation_javafx.service.VatService;
import org.example.ibb_ecodation_javafx.statemanagement.Store;
import org.example.ibb_ecodation_javafx.statemanagement.state.DarkModeState;
import org.example.ibb_ecodation_javafx.statemanagement.state.VatTableState;
import org.example.ibb_ecodation_javafx.ui.chart.ShadcnBarChart;
import org.example.ibb_ecodation_javafx.ui.combobox.ShadcnLanguageComboBox;
import org.example.ibb_ecodation_javafx.ui.input.ShadcnInput;
import org.example.ibb_ecodation_javafx.ui.table.DynamicTable;
import org.example.ibb_ecodation_javafx.utils.DialogUtil;
import org.example.ibb_ecodation_javafx.utils.ExcelUtil;
import org.example.ibb_ecodation_javafx.utils.PdfExportUtil;
import org.example.ibb_ecodation_javafx.utils.TxtUtil;

import java.io.File;
import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import static org.example.ibb_ecodation_javafx.utils.ThemeUtil.*;
import static org.example.ibb_ecodation_javafx.utils.ThemeUtil.changeTextColor;

/**
 * Controller for managing VAT-related operations
 */
public class VatManagementController {

    @FXML private DynamicTable<Vat> vatTable;
    @FXML private ShadcnInput vatNumberInput;
    @FXML private ShadcnBarChart barChart;
    @FXML private VBox vatPane;
    @FXML private HBox chartContainer;
    @FXML private HBox searchBar;

    private final VatService vatService = SpringContext.getContext().getBean(VatService.class);
    private final MailService mailService = SpringContext.getContext().getBean(MailService.class);
    private final LanguageService languageService = SpringContext.getContext().getBean(LanguageService.class);
    private final PdfExportUtil pdfExportUtil = SpringContext.getContext().getBean(PdfExportUtil.class);
    private final Store store = Store.getInstance();

    private List<Vat> originalTableData = new ArrayList<>();
    private String vatNumberFilter = "";
    private List<String> pdfHeaders;
    private Map<String, String> comboBoxItems;
    private Disposable languageSubscription;
    TxtUtil txtUtil = SpringContext.getContext().getBean(TxtUtil.class);
    ExcelUtil excelUtil = SpringContext.getContext().getBean(ExcelUtil.class);

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public void initialize() {
        if (!validateFxmlElements()) {
            return;
        }

        configureVatTable();
        setupLanguageListener();
        setupVatNumberFilter();
        setupComboBoxActions();
        loadInitialData();

        store.getState().subscribe(stateRegistry -> {
            var state = stateRegistry.getState(DarkModeState.class).isEnabled();
            changeBackground(state,chartContainer);
            changeBackground(state,searchBar);
        });
    }

    private boolean validateFxmlElements() {
        if (vatTable == null || vatNumberInput == null || barChart == null || vatPane == null) {
            System.err.println("FXML elements failed to load properly!");
            return false;
        }
        return true;
    }

    private void configureVatTable() {
        vatTable.setSingleSelection(true);
        updateUIText(ShadcnLanguageComboBox.getCurrentLanguageCode());
    }

    private void setupLanguageListener() {
        languageSubscription = ShadcnLanguageComboBox.watchLanguageValue()
                .subscribe(pair -> Platform.runLater(() -> updateUIText(pair.getKey())));
    }

    private void setupVatNumberFilter() {
        vatNumberInput.setTextChangeListener(newValue -> {
            vatNumberFilter = newValue != null ? newValue.trim() : "";
            applyFilters();
        });
    }

    private void setupComboBoxActions() {
        vatTable.watchComboBox().subscribe(pair -> {
            String languageCode = ShadcnLanguageComboBox.getCurrentLanguageCode();
            switch (pair.getKey()) {
                case "add" -> showDialog("/org/example/ibb_ecodation_javafx/views/vat-create-dialog-view.fxml", "Vat Dialog");
                case "delete" -> deleteSelectedRow();
                case "refresh" -> refreshData();
                case "update" -> updateSelectedRow();
                case "sendMail" -> sendMail(languageCode);
                case "print" -> printPdf(languageCode);
                case "export_pdf" -> exportPdf(languageCode);
                case "export_excel" -> excelUtil.exportToExcel(originalTableData, Vat.class, languageCode, vatPane.getScene().getWindow());
                case "export_txt" -> txtUtil.exportToTxt(vatTable, Vat.class, languageCode, vatPane.getScene().getWindow());
            }
        });
    }

    private void loadInitialData() {
        refreshData();
    }

    private void updateUIText(String languageCode) {
        ResourceBundle bundle = languageService.loadAll(ShadcnLanguageComboBox.getCurrentLanguageCode());
        try {
            configureInputAndTableHeaders(bundle);
            configureComboBoxItems(bundle);
            configurePdfHeaders();
        } catch (Exception e) {
            System.err.println("Failed to load translations: " + e.getMessage());

        }
    }

    private void configureInputAndTableHeaders(ResourceBundle bundle) {
        vatNumberInput.setHeader(bundle.getString("vat.receipt"));
        vatTable.setHeaderText(bundle.getString("vat.header"));
        vatTable.setDescriptionText(bundle.getString("vat.description"));
        vatTable.addHeaders(
                bundle.getString("vat.id"),
                bundle.getString("vat.userId"),
                bundle.getString("vat.amount"),
                bundle.getString("%"),
                bundle.getString("vat.total"),
                bundle.getString("vat.generalTotal"),
                bundle.getString("vat.receiptNumber"),
                bundle.getString("vat.transactionDate"),
                bundle.getString("description"),
                bundle.getString("vat.exportFormat"),
                bundle.getString("vat.isDeleted"),
                bundle.getString("vat.version")
        );
    }

    private void configureComboBoxItems(ResourceBundle bundle) {
        comboBoxItems = new HashMap<>();
        comboBoxItems.put("add", bundle.getString("vat.add"));
        comboBoxItems.put("delete", bundle.getString("vat.delete"));
        comboBoxItems.put("update", bundle.getString("vat.update"));
        comboBoxItems.put("print", bundle.getString("vat.print"));
        comboBoxItems.put("export_txt", bundle.getString("vat.export_txt"));
        comboBoxItems.put("export_pdf", bundle.getString("vat.export_pdf"));
        comboBoxItems.put("export_excel", bundle.getString("vat.export_excel"));
        comboBoxItems.put("sendMail", bundle.getString("vat.sendMail"));
        comboBoxItems.put("refresh", bundle.getString("vat.refresh"));
        vatTable.setComboBoxItems(comboBoxItems);
        vatTable.setComboBoxTitle(bundle.getString("vat.actions"));
    }

    private void configurePdfHeaders() {
        pdfHeaders = List.of(
                "vat.id", "vat.userId", "vat.amount", "%", "vat.total",
                "vat.generalTotal", "vat.receiptNumber", "vat.transactionDate", "description"
        );
    }

    private void showDialog(String fxmlPath, String title) {
        DialogUtil.showHelpPopup(fxmlPath, title);
    }

    private void sendMail(String languageCode) {
        store.dispatch(VatTableState.class, new VatTableState(
                originalTableData, store.getCurrentState(VatTableState.class).getSelectedVatItem()
        ));
        showDialog("/org/example/ibb_ecodation_javafx/views/vat-mail-dialog-view.fxml", "Vat Dialog");
    }

    private void printPdf(String languageCode) {
        File pdf = pdfExportUtil.exportToPdf(vatTable.getScene().getWindow(), originalTableData, pdfHeaders, "footer.message", languageCode);
        if (pdf != null && pdf.exists()) {
            pdfExportUtil.printPdfFromFile(pdf, languageCode);
        } else {
            showError("error.pdf.creation.failed");
        }
    }

    private void exportPdf(String languageCode) {
        File pdf = pdfExportUtil.exportToPdf(vatTable.getScene().getWindow(), originalTableData, pdfHeaders, "footer.message", languageCode);
        if (pdf == null || !pdf.exists()) {
            showError("error.pdf.export.failed");
        }
    }

    private void showError(String errorKey) {
        String errorMessage = languageService.translate(errorKey);
        System.err.println(errorMessage);
        //DialogUtil.showErrorDialog(errorMessage);
    }

    private void updateSelectedRow() {
        var selectedData = vatTable.getSelectedData();
        if (selectedData.isEmpty()) return;

        String selectedId = selectedData.get(0).get(0);
        originalTableData.stream()
                .filter(vat -> String.valueOf(vat.getId()).equals(selectedId))
                .findFirst()
                .ifPresent(vat -> {
                    store.dispatch(VatTableState.class, new VatTableState(
                            store.getCurrentState(VatTableState.class).vatList(), vat
                    ));
                    showDialog("/org/example/ibb_ecodation_javafx/views/vat-update-dialog-view.fxml", "Vat Dialog");
                    refreshData();
                });
    }

    private void deleteSelectedRow() {
        var selectedData = vatTable.getSelectedData();
        if (selectedData.isEmpty()) return;

        String selectedId = selectedData.get(0).get(0);
        originalTableData.removeIf(vat -> String.valueOf(vat.getId()).equals(selectedId));
        vatService.delete(Integer.parseInt(selectedId));
        refreshData();
    }

    private void refreshData() {
        originalTableData.clear();
        vatTable.clearData();
        vatService.readAll(1).forEach(this::addVatData);
        updateBarChartFromTableData();
        applyFilters();
    }

    private void addVatData(Vat vat) {
        originalTableData.add(vat);
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

    private void applyFilters() {
        var filteredData = originalTableData.stream()
                .filter(vat -> vat.getReceiptNumber().contains(vatNumberFilter))
                .toList();
        vatTable.setTableData(filteredData, this::mapVatToRow);
    }

    private List<String> mapVatToRow(Vat vat) {
        return Arrays.asList(
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

    private void updateBarChartFromTableData() {
        Map<String, BigDecimal> chartData = new LinkedHashMap<>();
        originalTableData.stream()
                .sorted(Comparator.comparing(Vat::getTransactionDate))
                .forEach(vat -> {
                    String date = DATE_FORMAT.format(vat.getTransactionDate());
                    BigDecimal total = vat.getTotalAmount();
                    chartData.merge(date, total, BigDecimal::add);
                });
        barChart.setMonthlyData(chartData);
    }

    public void shutdown() {
        if (languageSubscription != null && !languageSubscription.isDisposed()) {
            languageSubscription.dispose();
        }
    }
}