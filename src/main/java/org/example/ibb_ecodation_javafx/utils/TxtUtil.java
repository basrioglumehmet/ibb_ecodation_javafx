package org.example.ibb_ecodation_javafx.utils;

import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.example.ibb_ecodation_javafx.ui.table.DynamicTable;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class TxtUtil {

    public static <T> void exportToTxt(DynamicTable<T> table) throws IOException {
        // Create a FileChooser
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Text File");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Text Files", "*.txt")
        );
        fileChooser.setInitialFileName("table_data.txt");

        // Show save dialog (requires a Stage, using a new one for simplicity)
        Stage stage = new Stage(); // Note: Ideally, this should be the current stage
        File file = fileChooser.showSaveDialog(stage);

        if (file == null) {
            return; // User canceled the dialog, exit silently
        }

        // Write data to the text file
        try (FileWriter writer = new FileWriter(file)) {
            // Get headers and data from the table
            List<String> headers = table.getHeaders();
            List<List<String>> data = table.getData();

            // Write header row
            for (int i = 0; i < headers.size(); i++) {
                writer.write(headers.get(i));
                if (i < headers.size() - 1) {
                    writer.write("\t");
                }
            }
            writer.write("\n");

            // Write data rows
            for (List<String> rowData : data) {
                for (int j = 0; j < headers.size(); j++) {
                    String cellValue = j < rowData.size() ? rowData.get(j) : "";
                    writer.write(cellValue);
                    if (j < headers.size() - 1) {
                        writer.write("\t");
                    }
                }
                writer.write("\n");
            }
        }
    }
}