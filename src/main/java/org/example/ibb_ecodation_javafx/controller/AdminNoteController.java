package org.example.ibb_ecodation_javafx.controller;

import javafx.fxml.FXML;
import javafx.scene.layout.StackPane;
import org.example.ibb_ecodation_javafx.core.context.SpringContext;
import org.example.ibb_ecodation_javafx.core.logger.SecurityLogger;
import org.example.ibb_ecodation_javafx.core.service.LanguageService;
import org.example.ibb_ecodation_javafx.model.UserNote;
import org.example.ibb_ecodation_javafx.service.UserNoteService;
import org.example.ibb_ecodation_javafx.ui.combobox.ShadcnLanguageComboBox;
import org.example.ibb_ecodation_javafx.ui.listItem.ShadcnNoteList;

import java.time.LocalDateTime;
import java.util.List;

public class AdminNoteController {
    private final SecurityLogger securityLogger;
    @FXML
    private StackPane rootPane;

    private final UserNoteService userNoteService;

    public AdminNoteController() {
        this.securityLogger = SpringContext.getContext().getBean(SecurityLogger.class);
        this.userNoteService = SpringContext.getContext().getBean(UserNoteService.class);
        securityLogger.logOperation("Notlar açıldı");
    }

    public void initialize() {
        // ShadcnNoteList bileşenini oluşturuyoruz
        ShadcnNoteList noteList = new ShadcnNoteList(
                SpringContext.getContext().getBean(LanguageService.class),
                ShadcnLanguageComboBox.getCurrentLanguageCode()
        );
        rootPane.getChildren().add(noteList);

        // Veritabanından gelen UserNote verileri
        List<UserNote> data = userNoteService.readAll(1);  // Örnek kullanıcı ID: 1
        System.out.println(data.size());

        // Gelen her bir notu ShadcnNoteList'e ekliyoruz
        for (UserNote note : data) {
            // Veritabanından alınan 'note' objesinden tarih, başlık ve içeriği alıp, bunları kullanıyoruz
            String date = note.getReportAt().toString();  // Formatlayabilirsin
            String title = note.getHeader();  // Başlık alınıyor
            String content = note.getDescription();  // İçerik alınıyor

            // ShadcnNoteList'e yeni notu ekliyoruz
            noteList.addNote(date, title, content);
        }

        noteList.getPlusCard().setOnAction(actionEvent -> {
            var entity = new UserNote();
            entity.setId(0);
            entity.setUserId(1);
            entity.setHeader("Hello world");
            entity.setDescription("Hello world");
            entity.setReportAt(LocalDateTime.now());
            noteList.addNote(
                    entity.getReportAt().toString(),
                    entity.getHeader(),
                    entity.getDescription()
            );
        });
    }
}
