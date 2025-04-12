package org.example.ibb_ecodation_javafx.service.impl;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.subjects.PublishSubject;
import lombok.RequiredArgsConstructor;
import org.example.ibb_ecodation_javafx.model.UserNote;
import org.example.ibb_ecodation_javafx.model.enums.NoteEventType;
import org.example.ibb_ecodation_javafx.model.event.NoteEvent;
import org.example.ibb_ecodation_javafx.repository.UserNoteRepository;
import org.example.ibb_ecodation_javafx.repository.query.UserNoteQuery;
import org.example.ibb_ecodation_javafx.service.UserNoteService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.function.Consumer;

/**
 * Kullanıcı notlarıyla ilgili servis işlemlerini gerçekleştiren sınıf.
 * Not oluşturma, güncelleme, silme ve okuma işlemlerini yönetir.
 * RxJava ile olay tabanlı yayın yapar.
 */
@RequiredArgsConstructor
@Service
public class UserNoteServiceImpl implements UserNoteService {

    private final UserNoteRepository userNoteRepository;
    private final PublishSubject<NoteEvent> noteSubject = PublishSubject.create();

    public Observable<NoteEvent> getNoteObservable() {
        return noteSubject.hide();
    }

    @Override
    public UserNote create(UserNote entity) {
        UserNote createdNote = userNoteRepository.create(entity, UserNoteQuery.CREATE_NOTE, List.of(
                entity.getUserId(),
                entity.getReportAt(),
                entity.getHeader(),
                entity.getDescription(),
                entity.getVersion()
        ));
        noteSubject.onNext(new NoteEvent(NoteEventType.CREATE, createdNote));
        return createdNote;
    }

    @Override
    public void delete(int id) {
        UserNote note = userNoteRepository.read(UserNote.class, UserNoteQuery.READ_NOTE_BY_ID, List.of(id));
        userNoteRepository.delete(UserNoteQuery.DELETE_NOTE_BY_ID, List.of(id));
        if (note != null) {
            noteSubject.onNext(new NoteEvent(NoteEventType.DELETE, note));
        }
    }

    @Override
    public void read(int id, Consumer<UserNote> callback) {
        UserNote note = userNoteRepository.read(UserNote.class, UserNoteQuery.READ_NOTE_BY_ID, List.of(id));
        callback.accept(note);
    }

    @Override
    public List<UserNote> readAll(int userId) {
        return userNoteRepository.readAll(UserNote.class, UserNoteQuery.READ_ALL_NOTES_BY_USER_ID, List.of(userId));
    }

    @Override
    public void update(UserNote entity, Consumer<UserNote> callback) {
        UserNote updated = userNoteRepository.update(entity, UserNoteQuery.UPDATE_NOTE_BY_ID, List.of(
                entity.getUserId(),
                entity.getReportAt(),
                entity.getHeader(),
                entity.getDescription(),
                entity.getId(),
                entity.getVersion()
        ));
        noteSubject.onNext(new NoteEvent(NoteEventType.UPDATE, updated));
        callback.accept(updated);
    }
}