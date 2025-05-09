package org.example.ibb_ecodation_javafx.controller;

import io.reactivex.rxjava3.disposables.Disposable;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.layout.StackPane;
import lombok.RequiredArgsConstructor;
import org.example.ibb_ecodation_javafx.core.service.LanguageService;
import org.example.ibb_ecodation_javafx.model.UserNote;
import org.example.ibb_ecodation_javafx.service.UserNoteService;
import org.example.ibb_ecodation_javafx.statemanagement.Store;
import org.example.ibb_ecodation_javafx.statemanagement.state.TranslatorState;
import org.example.ibb_ecodation_javafx.statemanagement.state.UserState;
import org.example.ibb_ecodation_javafx.ui.combobox.ShadcnLanguageComboBox;
import org.example.ibb_ecodation_javafx.ui.listItem.ShadcnNoteList;
import org.example.ibb_ecodation_javafx.utils.DialogUtil;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class NoteController {

    private final UserNoteService userNoteService;
    private Disposable noteEventSubscription;

    @FXML
    private StackPane rootPane;

    private ShadcnNoteList noteList;

    private final Store store = Store.getInstance();

    private final LanguageService languageService;

    private final DialogUtil dialogUtil;

    public void initialize() {
        initializeNoteList();
        refreshNoteList();
        subscribeToNoteEvents();
        setupActions();
    }

    private void initializeNoteList() {
        noteList = new ShadcnNoteList(
                languageService,
                store.getCurrentState(TranslatorState.class).countryCode().getCode()
        );
        rootPane.getChildren().add(noteList);
    }

    private void refreshNoteList() {
        noteList.clearNotes();
        var userDetail = store.getCurrentState(UserState.class).getUserDetail();
        List<UserNote> data = userNoteService.findAllById(userDetail.getUserId());
        System.out.println("Not sayısı: " + data.size());
        for (UserNote userNote : data) {
            noteList.addNote(userNote);
        }
    }

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
                        }
                );
    }

    private void setupActions() {
        noteList.setPlusCardAction(event ->
                dialogUtil.showHelpPopup("/org/example/ibb_ecodation_javafx/views/note-create-dialog-view.fxml", "Not Oluştur"));

        noteList.setUpdateNoteAction(note -> {
            if (note.getId() != -1) {
                UserState currentState = store.getCurrentState(UserState.class);
                store.dispatch(UserState.class, new UserState(
                        currentState.getUserDetail(),
                        currentState.isLoggedIn(),
                        currentState.getSelectedUser(),
                        note
                ));
                dialogUtil.showHelpPopup("/org/example/ibb_ecodation_javafx/views/note-update-dialog-view.fxml", "Not Güncelle");
            } else {
                System.out.println("Notun ID'si yok, güncelleme yapılamaz.");
            }
        });

        noteList.setRemoveNoteAction(note -> {
            if (note.getId() != -1) { // Check note ID, not userId
                userNoteService.delete(note.getId());
            } else {
                System.out.println("Notun ID'si yok, silme yapılamaz.");
            }
        });
    }

    public void cleanup() {
        if (noteEventSubscription != null && !noteEventSubscription.isDisposed()) {
            noteEventSubscription.dispose();
            System.out.println("Not olayı aboneliği iptal edildi.");
        }
    }
}
