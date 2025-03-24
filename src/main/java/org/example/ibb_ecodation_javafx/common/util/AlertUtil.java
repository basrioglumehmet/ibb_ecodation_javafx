package org.example.ibb_ecodation_javafx.common.util;

import javafx.scene.control.Alert;
import lombok.experimental.UtilityClass;

@UtilityClass
public class AlertUtil {
    public static void showAlert(Alert.AlertType alertType,String title, String message){
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(message);
        alert.showAndWait();
    }
}
