package org.example.ibb_ecodation_javafx.utils;

import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import lombok.experimental.UtilityClass;
import org.example.ibb_ecodation_javafx.statemanagement.state.DarkModeState;

@UtilityClass
public class ThemeUtil {

    public static void changeNavbarColor(boolean lightModeValue, Node navbar) {
        navbar.setStyle(String.format("-fx-background-color: %s;", lightModeValue ? "white":"#121214") +
                "-fx-padding: 10px 20px 10px 20px;");
    }
    public static void changeLanguageComboBoxBackground(boolean lightModeValue, Node node) {
        node.setStyle(String.format("%s -fx-background-color: %s; -fx-border-color: %s; -fx-text-fill: %s;",
                node.getStyle(),lightModeValue ? "#fbfbfb":"#121214",lightModeValue ? "#e5e5e8":"#2e2e2e",
                lightModeValue ? "#000":"#fff")+
                "-fx-background-radius: 6px;" +
                "-fx-border-radius: 6px;   " +
                "-fx-border-width:1px;   " +
                "-fx-padding:5;"
        );
    }

    public static void changeContextMenuBackground(boolean lightModeValue, ContextMenu node) {
        node.setStyle(String.format("%s -fx-background-color: %s; -fx-border-color: %s; -fx-text-fill: %s;",
                node.getStyle(),lightModeValue ? "#fbfbfb":"#121214",lightModeValue ? "#e5e5e8":"#2e2e2e",
                lightModeValue ? "#000":"#fff")+
                "-fx-background-radius: 6px;" +
                "-fx-border-radius: 6px;   " +
                "-fx-border-width:1px;   " +
                "-fx-padding:5;"
        );
    }

    public static void changeBackgroundPrimary(boolean lightModeValue, Node node) {
        node.setStyle(String.format("%s -fx-background-color: %s;",
                node.getStyle(),lightModeValue ? "#f27a1a":"#2c2c30"));
    }
    public static void changeBackground(boolean lightModeValue, Node node) {
        node.setStyle(String.format(
                "-fx-background-color: %s; " +
                        "-fx-background-radius: 10px; " +
                        "-fx-padding: 10px; " +
                        "-fx-text-fill: %s; " +
                        "-fx-transition: -fx-background-color 0.3s ease-in-out; -fx-background-radius: 8;" +
                        " -fx-border-radius: 8; -fx-border-width: 1; -fx-border-color: %s;",

                lightModeValue ? "#fbfbfb" : "#202024",
                lightModeValue ? "#000000" : "#ffffff" ,
                lightModeValue ? "#e4e4e7" : "#2c2c30"
        ));
    }
    public static void changeContextMenuTextColor(boolean lightModeValue, Node node) {
        node.setStyle(String.format("%s -fx-text-fill: %s;",
                node.getStyle(),lightModeValue ? "#000":"#fff"));
    }
    public static void changeTextColorPrimary(boolean lightModeValue, Node node) {
        node.setStyle(String.format("%s -fx-text-fill: %s;",
                node.getStyle(),lightModeValue ? "#fff":"#fff"));
    }
    public static void changeTextColor(boolean lightModeValue, Node node) {
        node.setStyle(String.format("%s -fx-text-fill: %s;",
                node.getStyle(),lightModeValue ? "#000":"#fff"));
    }
    public static void changeThirdBackground(boolean lightModeValue, Node node) {
        node.setStyle(String.format("%s -fx-background-color: %s;",
                node.getStyle(),lightModeValue ? "#fff":"#202024"));
    }
    public static void changeSecondaryBackground(boolean lightModeValue, Node node) {
        node.setStyle(String.format("%s -fx-background-color: %s;",
                node.getStyle(),lightModeValue ? "#f3f3f4":"#121214"));
    }


    public static void changeFilterColor(boolean lightModeValue, Node node) {
        if (node != null) {
            node.setStyle(String.format(
                    "-fx-background-color: %s; " +
                            "-fx-background-radius: 10px; " +
                            "-fx-padding: 10px; " +
                            "-fx-text-fill: %s; " +
                            "-fx-transition: -fx-background-color 0.3s ease-in-out; -fx-background-radius: 8;" +
                            " -fx-border-radius: 8; -fx-border-width: 1; -fx-border-color: %s;",

                    lightModeValue ? "#fbfbfb" : "#202024",
                    lightModeValue ? "#000000" : "#ffffff" ,
                    lightModeValue ? "#e4e4e7" : "#2c2c30"
            ));
        }
    }

    public static void  changeRootPaneColor(boolean lightModeValue, Node rootPane) {
        rootPane.setStyle(String.format("-fx-background-color: %s;", lightModeValue ? "white":"#121214"));
    }
}
