package org.example.ibb_ecodation_javafx.utils;

import javafx.scene.control.Alert;
import lombok.RequiredArgsConstructor;
import lombok.experimental.UtilityClass;
import org.example.ibb_ecodation_javafx.core.service.LanguageService;
import org.example.ibb_ecodation_javafx.ui.combobox.ShadcnLanguageComboBox;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component("operationSystemUtil")
@RequiredArgsConstructor
public class OperationSystemUtil {
    private  final LanguageService languageService;
    private  final String languageCode = ShadcnLanguageComboBox.getCurrentLanguageCode();

    public  void openCalculator() {
        languageService.loadAll(languageCode);
        String os = System.getProperty("os.name").toLowerCase();
        try {
            switch (getOsType(os)) {
                case WINDOWS -> Runtime.getRuntime().exec("calc");
                case MAC -> Runtime.getRuntime().exec("open -a Calculator");
                case LINUX -> Runtime.getRuntime().exec("gnome-calculator");
                default -> showAlert(
                        languageService.translate("alert.error.title"),
                        languageService.translate("alert.os.unsupported"),
                        Alert.AlertType.ERROR
                );
            }
        } catch (IOException e) {
            showAlert(
                    languageService.translate("alert.error.title"),
                    languageService.translate("alert.calculator.failed"),
                    Alert.AlertType.ERROR
            );
            e.printStackTrace();
        }
    }

    private static OsType getOsType(String os) {
        if (os.contains("win")) {
            return OsType.WINDOWS;
        } else if (os.contains("mac")) {
            return OsType.MAC;
        } else if (os.contains("nux")) {
            return OsType.LINUX;
        } else {
            return OsType.UNSUPPORTED;
        }
    }

    private static void showAlert(String title, String message, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private enum OsType {
        WINDOWS, MAC, LINUX, UNSUPPORTED
    }
}