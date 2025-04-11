package org.example.ibb_ecodation_javafx.utils;

import javafx.stage.FileChooser;
import javafx.stage.Window;
import org.example.ibb_ecodation_javafx.annotation.PdfDefinition;
import org.example.ibb_ecodation_javafx.core.service.LanguageService;
import org.example.ibb_ecodation_javafx.ui.table.DynamicTable;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

@Component
public class TxtUtil {

    private final LanguageService languageService;

    public TxtUtil(LanguageService languageService) {
        this.languageService = languageService;
    }

    public <T> void exportToTxt(DynamicTable<T> table, Class<T> clazz, String languageCode, Window ownerWindow) throws IOException {
        languageService.loadAll(languageCode);

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(languageService.translate("dialog.save.txt.title"));
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter(
                        languageService.translate("filter.txt.files"),
                        "*.txt"
                )
        );
        fileChooser.setInitialFileName(languageService.translate("default.txt.filename"));

        File file = fileChooser.showSaveDialog(ownerWindow);

        if (file == null) {
            System.out.println(languageService.translate("info.file.save.cancelled"));
            return;
        }

        // Get translated headers from PdfDefinition annotations
        List<String> headers = getPdfDefinitionHeaders(clazz, languageCode);

        // Get data directly from the table
        List<List<String>> data = table.getData();

        // Calculate column widths based on translated headers
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
            System.out.println(languageService.translate("info.txt.exported") + ": " + file.getAbsolutePath());
        } catch (IOException e) {
            System.err.println(languageService.translate("error.txt.export") + ": " + e.getMessage());
            throw e;
        }
    }

    private static String pad(String text, int width) {
        return String.format("%-" + width + "s", text); // Left-align text
    }

    private <T> List<String> getPdfDefinitionHeaders(Class<T> clazz, String languageCode) {
        List<String> headers = new ArrayList<>();
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            PdfDefinition pdfDefinition = field.getAnnotation(PdfDefinition.class);
            if (pdfDefinition != null) {
                String translatedHeader = languageService.translate(pdfDefinition.fieldName());
                headers.add(translatedHeader);
            }
        }
        return headers;
    }
}