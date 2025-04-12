package org.example.ibb_ecodation_javafx.utils;

import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import lombok.experimental.UtilityClass;
import org.example.ibb_ecodation_javafx.statemanagement.state.DarkModeState;
import org.example.ibb_ecodation_javafx.ui.avatar.ShadcnAvatar;

@UtilityClass
public class ThemeUtil {

    public static void changeNavbarColor(boolean isDarkMode, Node navbar) {
        navbar.setStyle(String.format("-fx-background-color: %s;", !isDarkMode ? "white":"#121214") +
                "-fx-padding: 10px 20px 10px 20px;");
    }
    public static void changeLanguageComboBoxBackground(boolean isDarkMode, Node node) {
        node.setStyle(String.format("%s -fx-background-color: %s; -fx-border-color: %s; -fx-text-fill: %s;",
                node.getStyle(),!isDarkMode ? "#fbfbfb":"#121214",!isDarkMode ? "#e5e5e8":"#2e2e2e",
                !isDarkMode ? "#000":"#fff")+
                "-fx-background-radius: 6px;" +
                "-fx-border-radius: 6px;   " +
                "-fx-border-width:1px;   " +
                "-fx-padding:5;"
        );
    }

    public static void changeContextMenuBackground(boolean isDarkMode, ContextMenu node) {
        node.setStyle(String.format("%s -fx-background-color: %s; -fx-border-color: %s; -fx-text-fill: %s;",
                node.getStyle(),!isDarkMode ? "#fbfbfb":"#121214",!isDarkMode ? "#e5e5e8":"#2e2e2e",
                !isDarkMode ? "#000":"#fff")+
                "-fx-background-radius: 6px;" +
                "-fx-border-radius: 6px;   " +
                "-fx-border-width:1px;   " +
                "-fx-padding:5;"
        );
    }


    public static void changeBackground(boolean isDarkMode, Node node) {
        node.setStyle(String.format(
                "-fx-background-color: %s; " +
                        "-fx-background-radius: 10px; " +
                        "-fx-padding: 10px; " +
                        "-fx-text-fill: %s; " +
                        "-fx-transition: -fx-background-color 0.3s ease-in-out; -fx-background-radius: 8;" +
                        " -fx-border-radius: 8; -fx-border-width: 1; -fx-border-color: %s;",

                !isDarkMode ? "#fbfbfb" : "#202024",
                !isDarkMode ? "#000000" : "#ffffff" ,
                !isDarkMode ? "#e4e4e7" : "#2c2c30"
        ));
    }
    public static void changeContextMenuTextColor(boolean isDarkMode, Node node) {
        node.setStyle(String.format("%s -fx-text-fill: %s;",
                node.getStyle(),!isDarkMode ? "#000":"#fff"));
    }
    public static void changeTextColorPrimary(boolean isDarkMode, Node node) {
        node.setStyle(String.format("%s -fx-text-fill: %s;",
                node.getStyle(),!isDarkMode ? "#000":"#000"));
    }
    public static void changeTextColor(boolean isDarkMode, Node node) {
        node.setStyle(String.format("%s -fx-text-fill: %s;",
                node.getStyle(),!isDarkMode ? "#000":"#fff"));
    }
    public static void changeThirdBackground(boolean isDarkMode, Node node) {
        node.setStyle(String.format("%s -fx-background-color: %s;",
                node.getStyle(),!isDarkMode ? "#fff":"#202024"));
    }
    public static void changeSecondaryBackground(boolean isDarkMode, Node node) {
        node.setStyle(String.format("%s -fx-background-color: %s;",
                node.getStyle(),!isDarkMode ? "#f3f3f4":"#121214"));
    }


    public static void changeFilterColor(boolean isDarkMode, Node node) {
        if (node != null) {
            node.setStyle(String.format(
                    "-fx-background-color: %s; " +
                            "-fx-background-radius: 10px; " +
                            "-fx-padding: 10px; " +
                            "-fx-text-fill: %s; " +
                            "-fx-transition: -fx-background-color 0.3s ease-in-out; -fx-background-radius: 8;" +
                            " -fx-border-radius: 8; -fx-border-width: 1; -fx-border-color: %s;",

                    !isDarkMode ? "#fbfbfb" : "#202024",
                    !isDarkMode ? "#000000" : "#ffffff" ,
                    !isDarkMode ? "#e4e4e7" : "#2c2c30"
            ));
        }
    }

    public static void changeSidebarColor(boolean isDarkMode, Node sidebar, Node sidebarBottom, Node sidebarBottomInsideContainer,
                                   ShadcnAvatar shadcnAvatar) {
        sidebar.setStyle(String.format("-fx-background-color: %s;", !isDarkMode ? "white" : "#121214"));
        sidebarBottom.setStyle("-fx-padding: 10;");
        sidebarBottomInsideContainer.setStyle("-fx-background-radius: 14px; -fx-padding: 10;" +
                String.format("-fx-background-color: %s;", !isDarkMode ? "#f2f2f3" : "#202024"));
        shadcnAvatar.setAvatarBorder(!isDarkMode ? javafx.scene.paint.Color.web("#f2f2f3") : javafx.scene.paint.Color.web("#202024"));
    }

    public static void  changeRootPaneColor(boolean isDarkMode, Node rootPane) {
        rootPane.setStyle(String.format("-fx-background-color: %s;", !isDarkMode ? "white":"#121214"));
    }
}
