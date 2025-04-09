package org.example.ibb_ecodation_javafx.controller;


import javafx.fxml.FXML;
import javafx.scene.layout.VBox;
import org.example.ibb_ecodation_javafx.ui.chart.ShadcnBarChart;
import org.example.ibb_ecodation_javafx.ui.splitpane.ShadcnSplitPane;
import org.example.ibb_ecodation_javafx.ui.table.DynamicTable;

import java.util.LinkedHashMap;
import java.util.Map;

public class VatManagementController {

    @FXML
    private DynamicTable vatTable;

    @FXML
    private ShadcnBarChart barChart;

    public void initialize() {

            if(vatTable != null){
                vatTable.setHeaderText("KDV Yönetimi");
                vatTable.setDescriptionText("Kdv ile ilgili tüm işlemleri yapabilirsiniz.");
                vatTable.addHeaders("ID","N. Tutar","%","KDV Tutarı","Toplam","Fiş No","Tarih","Açıklama");
                vatTable.addData("1","Deneme","Deneme","Deneme","Deneme","Deneme","Deneme","Deneme");
                Map<String, Double> veri = new LinkedHashMap<>();
                veri.put("Ocak", 12000.0);
                veri.put("Şubat", 13000.5);
                veri.put("Mart", 9400.75);

                barChart.setMonthlyData(veri);
            }
    }
}
