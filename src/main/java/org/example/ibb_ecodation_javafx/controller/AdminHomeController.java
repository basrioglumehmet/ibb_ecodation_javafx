package org.example.ibb_ecodation_javafx.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.VBox;
import org.example.ibb_ecodation_javafx.core.context.SpringContext;
import org.example.ibb_ecodation_javafx.core.logger.SecurityLogger;
import org.example.ibb_ecodation_javafx.model.UserNotification;
import org.example.ibb_ecodation_javafx.service.UserNotificationService;
import org.example.ibb_ecodation_javafx.ui.splitpane.ShadcnSplitPane;
import org.example.ibb_ecodation_javafx.ui.table.DynamicTable;
import org.example.ibb_ecodation_javafx.utils.SceneUtil;

import java.io.IOException;

public class AdminHomeController {
    @FXML
    private ShadcnSplitPane splitPane;

    @FXML
    private UserNotificationService userNotificationService;
    private final SecurityLogger securityLogger;

    public AdminHomeController(){
        userNotificationService = SpringContext.getContext().getBean(UserNotificationService.class);
        this.securityLogger = SpringContext.getContext().getBean(SecurityLogger.class);
        securityLogger.logOperation("Anasayfa açıldı");
    }

    public void initialize() {
        try {
            // Sol paneli yükle
            FXMLLoader userLoader = new FXMLLoader(SceneUtil.class.getResource("/org/example/ibb_ecodation_javafx/views/user-management-view.fxml"));
            Parent userPane = userLoader.load();

            // Sağ paneli yükle (varsa)
            FXMLLoader kdvLoader = new FXMLLoader(SceneUtil.class.getResource("/org/example/ibb_ecodation_javafx/views/vat-management-view.fxml"));
            Parent kdvPane = kdvLoader.load();

            // Panellere yerleştir
            splitPane.setLeftContent(userPane);
            splitPane.setRightContent(kdvPane);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
