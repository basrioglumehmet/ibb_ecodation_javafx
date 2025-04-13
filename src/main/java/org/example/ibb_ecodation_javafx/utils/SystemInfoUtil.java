package org.example.ibb_ecodation_javafx.utils;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class SystemInfoUtil {

    public static String getComputerName() {
        try {
            return InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            return "Unknown";
        }
    }

    public static String getIpAddress() {
        try {
            return InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            return "Unknown";
        }
    }
}