package org.example.ibb_ecodation_javafx.utils;

import javafx.scene.Node;
import lombok.experimental.UtilityClass;

@UtilityClass
public class ThemeUtil {
    public static void changeNavbarColor(boolean lightModeValue, Node navbar) {
        navbar.setStyle(String.format("-fx-background-color: %s;", lightModeValue ? "white":"#121214") +
                "-fx-padding: 10px 20px 10px 20px;");
    }

    public static void  changeRootPaneColor(boolean lightModeValue, Node rootPane) {
        rootPane.setStyle(String.format("-fx-background-color: %s;", lightModeValue ? "white":"#121214"));
    }
}
