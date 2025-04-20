package org.example.ibb_ecodation_javafx.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import com.fasterxml.jackson.core.type.TypeReference;
import javafx.fxml.FXML;
import javafx.scene.layout.VBox;
import org.apache.logging.log4j.core.util.JsonUtils;
import org.example.ibb_ecodation_javafx.core.context.SpringContext;
import org.example.ibb_ecodation_javafx.core.service.LanguageService;
import org.example.ibb_ecodation_javafx.model.JsonBackup;
import org.example.ibb_ecodation_javafx.model.User;
import org.example.ibb_ecodation_javafx.service.JsonBackupService;
import org.example.ibb_ecodation_javafx.statemanagement.Store;
import org.example.ibb_ecodation_javafx.statemanagement.state.TranslatorState;
import org.example.ibb_ecodation_javafx.ui.listItem.ShadcnBackupList;
import org.example.ibb_ecodation_javafx.utils.JsonBackupUtil;

import java.time.LocalDateTime;
import java.util.List;
@RequiredArgsConstructor
@Controller
public class AdminBackupView {
    @FXML
    private VBox mainContainer;

    private final LanguageService languageService;

    private final Store store = Store.getInstance();

    private final JsonBackupService backupService;


    @FXML
    private void initialize() {
        languageService.loadAll(store.getCurrentState(TranslatorState.class).countryCode().getCode());
        // Yedek listesini oluştur
        ShadcnBackupList backupList = new ShadcnBackupList(
                languageService.translate("data_backups"),
                languageService.translate("new_backup"),
                languageService.translate("button.download"),
                languageService.translate("button.remove"));

        // Veritabanından tüm yedekleri oku ve listeye ekle
        List<JsonBackup> backups = backupService.findAll();
        for (JsonBackup backup : backups) {
            backupList.addBackup(backup);
        }
        backupList.setDownloadButtonAction(backup -> {
            System.out.println(backup.getJsonData());
            JsonBackupUtil.exportRawDataToJsonWithDialog(
                    backup.getJsonData(),
                    mainContainer.getScene().getWindow(),
                    new TypeReference<List<User>>() {}
            );
        });

        backupList.setRemoveBackupAction(backup -> {
            backupService.delete(backup.getId());
            backupList.clearBackups();
            backupService.findAll().forEach(backupList::addBackup);
            System.out.println("Removed: " + backup.getHeader());
        });

        // Yedek listesini ana konteynere ekle
        mainContainer.getChildren().add(backupList);
    }
}
