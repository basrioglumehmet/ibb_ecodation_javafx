package org.example.ibb_ecodation_javafx.utils;

import lombok.experimental.UtilityClass;

import java.awt.*;

@UtilityClass
public class TrayUtil {
    public static void showTrayNotification(String message, String title) {
        if (SystemTray.isSupported()) {
            SystemTray systemTray = SystemTray.getSystemTray();
            TrayIcon trayIcon = new TrayIcon(Toolkit.getDefaultToolkit().getImage("icon.png"), "Tray Icon");
            try {
                systemTray.add(trayIcon);
                trayIcon.displayMessage(title, message, TrayIcon.MessageType.INFO);
            } catch (AWTException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("SystemTray is not supported on your system.");
        }
    }
}
