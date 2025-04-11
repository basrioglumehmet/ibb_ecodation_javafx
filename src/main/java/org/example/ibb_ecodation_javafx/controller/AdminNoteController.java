package org.example.ibb_ecodation_javafx.controller;

import io.reactivex.rxjava3.disposables.Disposable;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.layout.StackPane;
import org.example.ibb_ecodation_javafx.core.context.SpringContext;
import org.example.ibb_ecodation_javafx.core.logger.SecurityLogger;
import org.example.ibb_ecodation_javafx.core.service.LanguageService;
import org.example.ibb_ecodation_javafx.model.UserNote;
import org.example.ibb_ecodation_javafx.service.UserNoteService;
import org.example.ibb_ecodation_javafx.ui.combobox.ShadcnLanguageComboBox;
import org.example.ibb_ecodation_javafx.ui.listItem.ShadcnNoteList;
import org.example.ibb_ecodation_javafx.utils.DialogUtil;

import java.util.List;

/**
 * Admin notlarını yöneten kontrolör sınıfı.
 */
public class AdminNoteController {
    private final SecurityLogger securityLogger;
    private final UserNoteService userNoteService;
    private Disposable noteEventSubscription;

    @FXML
    private StackPane rootPane;

    private ShadcnNoteList noteList;

    /**
     * AdminNoteController yapıcısı.
     * Spring bağlamından gerekli bağımlılıkları alır.
     */
    public AdminNoteController() {
        this.securityLogger = SpringContext.getContext().getBean(SecurityLogger.class);
        this.userNoteService = SpringContext.getContext().getBean(UserNoteService.class);
        securityLogger.logOperation("Notlar açıldı");
    }

    /**
     * JavaFX bileşenlerini başlatır ve not listesini doldurur.
     */
    public void initialize() {
        // ShadcnNoteList bileşenini oluşturuyoruz
        noteList = new ShadcnNoteList(
                SpringContext.getContext().getBean(LanguageService.class),
                ShadcnLanguageComboBox.getCurrentLanguageCode()
        );
        rootPane.getChildren().add(noteList);

        // Not listesini başlangıçta doldur
        refreshNoteList();

        // Not olaylarına abone ol
        subscribeToNoteEvents();

        // Artı kartına dialog açma olayını bağla
        noteList.setPlusCardAction(event -> {
            DialogUtil.showHelpPopup("/org/example/ibb_ecodation_javafx/views/note-create-dialog-view.fxml",
                    "Not Oluştur");
        });
    }

    /**
     * Not listesini veritabanından alınan en son verilerle yeniler.
     */
    private void refreshNoteList() {
        // Mevcut notları temizle
        noteList.clearNotes();

        // Veritabanından notları al (Örnek kullanıcı ID: 1)
        List<UserNote> data = userNoteService.readAll(1);
        System.out.println("Not sayısı: " + data.size());

        // Her bir notu ShadcnNoteList'e ekle
        for (UserNote note : data) {
            String date = note.getReportAt().toString();
            String title = note.getHeader();
            String content = note.getDescription();
            noteList.addNote(date, title, content);
        }
    }

    /**
     * Not olaylarına abone olur ve değişikliklerde listeyi yeniler.
     */
    private void subscribeToNoteEvents() {
        noteEventSubscription = userNoteService.getNoteObservable()
                .observeOn(io.reactivex.rxjava3.schedulers.Schedulers.from(Platform::runLater))
                .subscribe(
                        event -> {
                            refreshNoteList();
                            System.out.println("Not olayı alındı: " + event.eventType());
                        },
                        error -> {
                            System.err.println("Not olayı hatası: " + error.getMessage());
                            securityLogger.logOperation("Not olayı hatası: " + error.getMessage());
                        }
                );
    }

    /**
     * Kontrolör kapatıldığında kaynakları temizler.
     */
    public void cleanup() {
        if (noteEventSubscription != null && !noteEventSubscription.isDisposed()) {
            noteEventSubscription.dispose();
            System.out.println("Not olayı aboneliği iptal edildi.");
        }
    }
}