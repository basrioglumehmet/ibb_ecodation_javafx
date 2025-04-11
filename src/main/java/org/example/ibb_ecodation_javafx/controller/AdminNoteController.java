package org.example.ibb_ecodation_javafx.controller;

import javafx.fxml.FXML;
import javafx.scene.layout.StackPane;
import org.example.ibb_ecodation_javafx.core.context.SpringContext;
import org.example.ibb_ecodation_javafx.core.logger.SecurityLogger;
import org.example.ibb_ecodation_javafx.core.service.LanguageService;
import org.example.ibb_ecodation_javafx.ui.combobox.ShadcnLanguageComboBox;
import org.example.ibb_ecodation_javafx.ui.listItem.ShadcnNoteList;

public class AdminNoteController {
    private final SecurityLogger securityLogger;
    @FXML
    private StackPane rootPane;


    public AdminNoteController() {
        this.securityLogger = SpringContext.getContext().getBean(SecurityLogger.class);
        securityLogger.logOperation("Notlar açıldı");
    }

    public void initialize(){

        ShadcnNoteList noteList = new ShadcnNoteList(
                SpringContext.getContext().getBean(LanguageService.class),
                ShadcnLanguageComboBox.getCurrentLanguageCode()
        );
        rootPane.getChildren().add(noteList);
    }
}
