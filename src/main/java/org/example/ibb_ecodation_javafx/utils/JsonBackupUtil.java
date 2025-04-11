package org.example.ibb_ecodation_javafx.utils;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.stage.FileChooser;
import javafx.stage.Window;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class JsonBackupUtil {

    private static final ObjectMapper mapper = new ObjectMapper();

    static {

        mapper.enable(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_USING_DEFAULT_VALUE);
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    // FileChooser ile JSON dosyasına export
    public static <T> void exportToJsonWithDialog(List<T> list, Window parentWindow) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Export to JSON");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("JSON files", "*.json"));

        File file = fileChooser.showSaveDialog(parentWindow);
        if (file != null) {
            try {
                String json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(list);
                FileUtils.writeStringToFile(file, json, StandardCharsets.UTF_8);
                System.out.println("Exported to: " + file.getAbsolutePath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // FileChooser ile JSON dosyasından import
    public static <T> List<T> importFromJsonWithDialog(Window parentWindow, Class<T> clazz) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Import from JSON");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("JSON files", "*.json"));

        File file = fileChooser.showOpenDialog(parentWindow);
        if (file != null) {
            try {
                String json = FileUtils.readFileToString(file, StandardCharsets.UTF_8);
                return mapper.readValue(json, mapper.getTypeFactory().constructCollectionType(List.class, clazz));
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }
        return null;
    }
}