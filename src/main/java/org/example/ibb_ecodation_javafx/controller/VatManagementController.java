package org.example.ibb_ecodation_javafx.controller;

import javafx.fxml.FXML;
import javafx.scene.layout.VBox;
import org.example.ibb_ecodation_javafx.ui.chart.ShadcnBarChart;
import org.example.ibb_ecodation_javafx.ui.input.ShadcnInput;
import org.example.ibb_ecodation_javafx.ui.table.DynamicTable;
import org.example.ibb_ecodation_javafx.utils.DialogUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class VatManagementController {

    @FXML
    private DynamicTable<String> vatTable;

    @FXML
    private ShadcnInput vatNumber;

    @FXML
    private ShadcnBarChart barChart;

    @FXML
    private VBox vatPane;

    private Map<String, String> comboItems;
    private List<List<String>> originalTableData;
    private String vatNumberFilter = "";

    public void initialize() {
        if (vatTable != null) {
            originalTableData = new ArrayList<>();

            vatTable.setHeaderText("KDV Yönetimi");
            vatTable.setDescriptionText("Kdv ile ilgili tüm işlemleri yapabilirsiniz.");

            comboItems = new HashMap<>();
            comboItems.put("add", "KDV Girişi Ekle");
            comboItems.put("remove", "KDV Girişi Sil");
            comboItems.put("update", "KDV Girişi Güncelle");
            comboItems.put("print", "Yazıcıya Yazdır");
            comboItems.put("export_txt", "TXT Olarak Dışa Aktar");
            comboItems.put("export_pdf", "PDF Olarak Dışa Aktar");
            comboItems.put("export_excel", "EXCEL Olarak Dışa Aktar");
            comboItems.put("sendMail", "E-posta Gönder");

            vatTable.setComboBoxTitle("Eylemler");
            vatTable.setComboBoxItems(comboItems);

            vatNumber.setTextChangeListener(newValue -> {
                vatNumberFilter = newValue != null ? newValue.trim() : "";
                applyFilters();
            });

            vatTable.watchComboBox().subscribe(pair -> {
                String key = pair.getKey();
                String value = pair.getValue();
                System.out.println("Seçilen combo eylemi: " + key + " = " + value);
                switch (key) {
                    case "add":
                        // KDV girişi ekleme işlemini gerçekleştir
                        DialogUtil.showHelpPopup("/org/example/ibb_ecodation_javafx/views/vat-create-dialog-view.fxml","Vat Dialog");
                        break;
                    case "remove":
                        // KDV girişi silme işlemini gerçekleştir
                        break;
                    case "update":
                        // KDV girişi güncelleme işlemini gerçekleştir
                        break;
                }
            });

            vatTable.addHeaders("ID", "N. Tutar", "%", "KDV Tutarı", "Toplam", "Fiş No", "Tarih", "Açıklama");

            // 20 örnek veri ekleniyor
            addTableData("1", "1000", "18", "180", "1180", "F123", "2025-01-05", "Mal Alımı");
            addTableData("2", "2500", "20", "500", "3000", "F124", "2025-01-15", "Hizmet Bedeli");
            addTableData("3", "800", "10", "80", "880", "F125", "2025-01-25", "Ofis Malzemesi");
            addTableData("4", "3000", "18", "540", "3540", "F126", "2025-02-02", "Ekipman Satın Alma");
            addTableData("5", "1200", "20", "240", "1440", "F127", "2025-02-10", "Danışmanlık");
            addTableData("6", "5000", "10", "500", "5500", "F128", "2025-02-20", "Proje Gideri");
            addTableData("7", "750", "18", "135", "885", "F129", "2025-03-03", "Kırtasiye");
            addTableData("8", "2000", "20", "400", "2400", "F130", "2025-03-12", "Bakım Onarım");
            addTableData("9", "1500", "10", "150", "1650", "F131", "2025-03-22", "Yazılım Lisansı");
            addTableData("10", "4000", "18", "720", "4720", "F132", "2025-04-01", "Makine Alımı");
            addTableData("11", "900", "20", "180", "1080", "F133", "2025-04-10", "Eğitim Gideri");
            addTableData("12", "3200", "10", "320", "3520", "F134", "2025-04-20", "Reklam Gideri");
            addTableData("13", "1800", "18", "324", "2124", "F135", "2025-05-05", "Seyahat Masrafı");
            addTableData("14", "2700", "20", "540", "3240", "F136", "2025-05-15", "Yemek Organizasyonu");
            addTableData("15", "600", "10", "60", "660", "F137", "2025-05-25", "Küçük Alımlar");
            addTableData("16", "3500", "18", "630", "4130", "F138", "2025-06-02", "Yedek Parça");
            addTableData("17", "2200", "20", "440", "2640", "F139", "2025-06-12", "Teknik Servis");
            addTableData("18", "4500", "10", "450", "4950", "F140", "2025-06-22", "Yazılım Geliştirme");
            addTableData("19", "1300", "18", "234", "1534", "F141", "2025-07-01", "Temizlik Malzemesi");
            addTableData("20", "2800", "20", "560", "3360", "F142", "2025-07-10", "Pazarlama Gideri");

            updateBarChartFromTableData();
        }
    }

    private void addTableData(String... data) {
        // Tabloya veri ekle
        vatTable.addData(data);
        originalTableData.add(new ArrayList<>(List.of(data)));
        updateBarChartFromTableData(); // Yeni veri eklendiğinde grafiği güncelle
    }

    private void applyFilters() {
        // Tabloyu temizle
        vatTable.clearData();

        // Filtreleri uygula
        List<List<String>> filteredData = originalTableData.stream()
                .filter(row -> {
                    boolean vatNumberMatch = vatNumberFilter.isEmpty() ||
                            row.get(5).toLowerCase().contains(vatNumberFilter.toLowerCase());
                    return vatNumberMatch;
                })
                .collect(Collectors.toList());

        // Filtrelenmiş verileri tabloya ekle
        filteredData.forEach(data -> vatTable.addData(data.toArray(new String[0])));
        updateBarChartFromTableData(); // Filtreleme sonrası grafiği güncelle
    }

    private void updateBarChartFromTableData() {
        // Çubuk grafik için veri haritası oluştur
        Map<String, Double> chartData = new LinkedHashMap<>();

        // Tablo üzerindeki filtrelenmiş verileri kullan
        List<List<String>> currentData = vatTable.getData();

        for (List<String> row : currentData) {
            try {
                String date = row.get(6); // "Tarih" sütunu (indeks 6)
                double total = Double.parseDouble(row.get(4)); // "Toplam" sütunu (indeks 4)
                chartData.put(date, total);
            } catch (NumberFormatException e) {
                System.err.println("Toplam değeri ayrıştırılırken hata: " + e.getMessage());
            }
        }

        // Eğer filtrelenmiş veri yoksa orijinal verileri kullan
        if (chartData.isEmpty()) {
            for (List<String> row : originalTableData) {
                try {
                    String date = row.get(6);
                    double total = Double.parseDouble(row.get(4));
                    chartData.put(date, total);
                } catch (NumberFormatException e) {
                    System.err.println("Toplam değeri ayrıştırılırken hata: " + e.getMessage());
                }
            }
        }

        // Grafiği güncelle
        barChart.setMonthlyData(chartData);
    }
}