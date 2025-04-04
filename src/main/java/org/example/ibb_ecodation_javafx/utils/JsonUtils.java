package org.example.ibb_ecodation_javafx.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.example.ibb_ecodation_javafx.controller.AdminDashboardController;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class JsonUtils {

    public static final ObjectMapper objectMapper = new ObjectMapper();

    // JSON dosyasını belirli bir klasörden okuma
    public static void readJsonFile( String fileName) throws IOException {
        try {
            // Dosya yolu ve klasör yolu
            URL resourceUrl = AdminDashboardController.class.getResource("/org/example/ibb_ecodation_javafx/jsons/" + fileName);

            // Eğer dosya bulunamazsa, oluştur
            if (resourceUrl == null) {
                createJsonFile("/org/example/ibb_ecodation_javafx/jsons/" + fileName);
            }
        } catch (Exception ex) {
            // Dosya yoksa, oluşturup yazalım
            createJsonFile("/org/example/ibb_ecodation_javafx/jsons/" + fileName);
        }
    }

    // JSON dosyasını oluşturma ve yazma (resources altına yazılacak)
    public static void createJsonFile(String resourceUrl) throws IOException {
        // Kaynak yolunun proje klasörüne uygun hale getirilmesi
        Path path = Paths.get("src/main/resources" + resourceUrl);
        File file = path.toFile();

        // Klasör yoksa oluştur
        if (!Files.exists(path.getParent())) {
            Files.createDirectories(path.getParent());
        }

        // Dosya yoksa oluştur
        if (!file.exists()) {
            file.createNewFile(); // Dosyayı oluştur

            // JSON Objesi oluştur
            JsonNode jsonNode = objectMapper.createObjectNode();
            objectMapper.writeValue(file, jsonNode); // Dosyaya JSON yaz
            System.out.println("JSON dosyası oluşturuldu" + file.getAbsolutePath());
        }
    }

    // JSON dosyasına veri ekleme (eğer dataHeader varsa, yeni veriyi içine ekler ve birleştirir)
    public static void insertDataToJsonFile(String resourceUrl, String dataHeader, JsonNode dataToInsert) throws IOException {
        // Kaynak yolunun proje klasörüne uygun hale getirilmesi
        Path path = Paths.get("src/main/resources" + resourceUrl);
        File file = path.toFile();

        // Eğer dosya yoksa, hata ver
        if (!file.exists()) {
            System.out.println("File does not exist.");
            return;
        }

        // JSON dosyasını oku
        JsonNode existingData = objectMapper.readTree(file);

        // Eğer mevcut dataHeader varsa ve bir nesne ise
        if (existingData.isObject()) {
            ObjectNode existingObject = (ObjectNode) existingData;

            // Eğer dataHeader zaten varsa, yeni veriyi içine ekleyin
            if (existingObject.has(dataHeader)) {
                JsonNode existingHeader = existingObject.get(dataHeader);

                // Eğer header bir nesne ise, veri ekleyelim
                if (existingHeader.isObject()) {
                    ((ObjectNode) existingHeader).setAll((ObjectNode) dataToInsert);  // Birleştiriyoruz
                } else {
                    // Eğer header bir nesne değilse, bu durumda yeni veri ekleyemeyiz
                    System.out.println("DataHeader is not an object. Cannot insert new data.");
                }
            } else {
                // Eğer dataHeader yoksa, onu oluşturun ve veriyi ekleyin
                existingObject.set(dataHeader, dataToInsert);
            }
        }

        // Güncellenmiş JSON'u dosyaya yaz
        objectMapper.writeValue(file, existingData);
        System.out.println("JSON dosyasına yeni veri eklendi: " + file.getAbsolutePath());
    }



}
