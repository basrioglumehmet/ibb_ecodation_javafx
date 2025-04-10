package org.example.ibb_ecodation_javafx.utils;

import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.example.ibb_ecodation_javafx.annotation.PdfDefinition;
import org.example.ibb_ecodation_javafx.ui.table.DynamicTable;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class TxtUtil {

    public static <T> void exportToTxt(DynamicTable<T> table, Class<T> clazz) throws IOException {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Metin Dosyasını Kaydet");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Metin Dosyaları", "*.txt")
        );
        fileChooser.setInitialFileName("table_data.txt");

        Stage stage = new Stage();
        File file = fileChooser.showSaveDialog(stage);

        if (file == null) {
            return;
        }

        // Get headers from PdfDefinition annotations
        List<String> headers = getPdfDefinitionHeaders(clazz);

        // Get data directly from the table
        List<List<String>> data = table.getData();

        // Calculate column widths
        int columnCount = headers.size();
        int[] columnWidths = new int[columnCount];
        for (int i = 0; i < columnCount; i++) {
            columnWidths[i] = headers.get(i).length();
        }

        for (List<String> row : data) {
            for (int i = 0; i < Math.min(row.size(), columnCount); i++) {
                columnWidths[i] = Math.max(columnWidths[i], row.get(i).length());
            }
        }

        // Write to file
        try (FileWriter writer = new FileWriter(file)) {
            // Write headers
            for (int i = 0; i < columnCount; i++) {
                writer.write(pad(headers.get(i), columnWidths[i]));
                if (i < columnCount - 1) writer.write(" | ");
            }
            writer.write("\n");

            // Write separator line
            for (int i = 0; i < columnCount; i++) {
                writer.write("-".repeat(columnWidths[i]));
                if (i < columnCount - 1) writer.write("-+-");
            }
            writer.write("\n");

            // Write data rows
            for (List<String> row : data) {
                for (int i = 0; i < columnCount; i++) {
                    String value = i < row.size() ? row.get(i) : "";
                    writer.write(pad(value, columnWidths[i]));
                    if (i < columnCount - 1) writer.write(" | ");
                }
                writer.write("\n");
            }
        }
    }

    private static String pad(String text, int width) {
        return String.format("%-" + width + "s", text); // Left-align text
    }

    private static <T> List<String> getPdfDefinitionHeaders(Class<T> clazz) {
        List<String> headers = new ArrayList<>();
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            PdfDefinition pdfDefinition = field.getAnnotation(PdfDefinition.class);
            if (pdfDefinition != null) {
                headers.add(pdfDefinition.fieldName());
            }
        }
        return headers;
    }
}