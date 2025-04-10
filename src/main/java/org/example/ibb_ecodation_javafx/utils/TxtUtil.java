package org.example.ibb_ecodation_javafx.utils;

import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.example.ibb_ecodation_javafx.annotation.JdbcNamedField;
import org.example.ibb_ecodation_javafx.annotation.PdfDefinition;
import org.example.ibb_ecodation_javafx.model.Vat;
import org.example.ibb_ecodation_javafx.ui.table.DynamicTable;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TxtUtil {

    public static <T> void exportToTxt(DynamicTable<T> table) throws IOException {
        // Dosya seçici oluştur
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Metin Dosyasını Kaydet");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Metin Dosyaları", "*.txt")
        );
        fileChooser.setInitialFileName("table_data.txt");

        // Kaydetme diyaloğunu göster (bir Stage gerekiyor, basitlik için yeni bir tane oluşturuyoruz)
        Stage stage = new Stage(); // Not: İdeal olarak, mevcut stage kullanılmalı
        File file = fileChooser.showSaveDialog(stage);

        // Eğer kullanıcı diyaloğu iptal ederse, sessizce çık
        if (file == null) {
            return;
        }

        // @PdfDefinition anotasyonlarından başlıkları al
        List<String> headers = getPdfDefinitionHeaders();
        // Veritabanı alan adlarını (dbFieldName) Vat sınıfındaki alanların indeksleriyle eşleştir
        Map<String, Integer> dbFieldToIndex = getDbFieldToIndexMapping();

        // Başlıkların her biri için maksimum genişlikleri belirle
        int[] columnWidths = new int[headers.size()];
        for (int i = 0; i < headers.size(); i++) {
            columnWidths[i] = headers.get(i).length();
        }

        // Tablodan verileri al
        List<List<String>> data = table.getData();

        // Her satırdaki hücreler için en uzun değeri bul ve sütun genişliğini ayarla
        for (List<String> rowData : data) {
            for (int i = 0; i < rowData.size(); i++) {
                columnWidths[i] = Math.max(columnWidths[i], rowData.get(i).length());
            }
        }

        // Verileri metin dosyasına yaz
        try (FileWriter writer = new FileWriter(file)) {
            // Başlık satırını yaz
            for (int i = 0; i < headers.size(); i++) {
                writer.write(padString(headers.get(i), columnWidths[i]));
                if (i < headers.size() - 1) {
                    writer.write("\t"); // Başlıklar arasında sekme (tab) ekle
                }
            }
            writer.write("\n"); // Yeni satıra geç

            // Veri satırlarını yaz, dbFieldToIndex eşleştirmesine göre yeniden sırala
            for (List<String> rowData : data) {
                String[] reorderedRow = new String[headers.size()];
                // Her sütunu doğru alana eşleştir
                for (int j = 0; j < rowData.size(); j++) {
                    String dbFieldName = getDbFieldNameByIndex(j);
                    Integer targetIndex = dbFieldToIndex.get(dbFieldName);
                    if (targetIndex != null && targetIndex < reorderedRow.length) {
                        reorderedRow[targetIndex] = rowData.get(j);
                    }
                }
                // Yeniden sıralanmış satırı yaz
                for (int j = 0; j < headers.size(); j++) {
                    String cellValue = reorderedRow[j] != null ? reorderedRow[j] : "";
                    writer.write(padString(cellValue, columnWidths[j]));
                    if (j < headers.size() - 1) {
                        writer.write("\t"); // Sütunlar arasında sekme ekle
                    }
                }
                writer.write("\n"); // Yeni satıra geç
            }
        }
    }

    // Hücreyi, belirtilen genişlikte boşlukla dolduran yardımcı metod
    private static String padString(String value, int width) {
        StringBuilder paddedValue = new StringBuilder(value);
        while (paddedValue.length() < width) {
            paddedValue.append(" "); // Hücreyi boşlukla doldur
        }
        return paddedValue.toString();
    }
    // @PdfDefinition anotasyonlarından fieldName değerlerini alarak başlıkları döndüren yardımcı metod
    private static List<String> getPdfDefinitionHeaders() {
        List<String> headers = new ArrayList<>();
        Field[] fields = Vat.class.getDeclaredFields();

        // Vat sınıfındaki tüm alanları tara
        for (Field field : fields) {
            PdfDefinition pdfDefinition = field.getAnnotation(PdfDefinition.class);
            if (pdfDefinition != null) {
                headers.add(pdfDefinition.fieldName()); // fieldName değerini başlık olarak ekle
            }
        }
        return headers;
    }

    // Veritabanı alan adlarını (dbFieldName) Vat sınıfındaki alanların indeksleriyle eşleştiren yardımcı metod
    private static Map<String, Integer> getDbFieldToIndexMapping() {
        Map<String, Integer> dbFieldToIndex = new HashMap<>();
        Field[] fields = Vat.class.getDeclaredFields();

        int index = 0;
        // Vat sınıfındaki tüm alanları tara
        for (Field field : fields) {
            PdfDefinition pdfDefinition = field.getAnnotation(PdfDefinition.class);
            JdbcNamedField jdbcNamedField = field.getAnnotation(JdbcNamedField.class);
            if (pdfDefinition != null && jdbcNamedField != null) {
                // dbFieldName'i indeksiyle eşleştir
                dbFieldToIndex.put(jdbcNamedField.dbFieldName(), index);
                index++;
            }
        }
        return dbFieldToIndex;
    }

    // Sütun indeksine göre dbFieldName değerini döndüren yardımcı metod
    // (DynamicTable sütunlarının dbFieldName sırasına göre olduğunu varsayar)
    private static String getDbFieldNameByIndex(int index) {
        Field[] fields = Vat.class.getDeclaredFields();
        int currentIndex = 0;
        for (Field field : fields) {
            JdbcNamedField jdbcNamedField = field.getAnnotation(JdbcNamedField.class);
            if (jdbcNamedField != null) {
                if (currentIndex == index) {
                    return jdbcNamedField.dbFieldName();
                }
                currentIndex++;
            }
        }
        return null;
    }
}