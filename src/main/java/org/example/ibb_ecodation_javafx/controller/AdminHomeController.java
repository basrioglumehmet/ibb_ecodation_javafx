package org.example.ibb_ecodation_javafx.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.VBox;
import org.example.ibb_ecodation_javafx.ui.splitpane.ShadcnSplitPane;
import org.example.ibb_ecodation_javafx.ui.table.DynamicTable;
import org.example.ibb_ecodation_javafx.utils.SceneUtil;

import java.io.IOException;

public class AdminHomeController {
    @FXML
    private ShadcnSplitPane splitPane;

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
