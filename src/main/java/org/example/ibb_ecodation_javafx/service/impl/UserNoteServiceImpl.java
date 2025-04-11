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

    /**
     * Not verilerini saklayan ve erişen repository.
     */
    private final UserNoteRepository userNoteRepository;

    /**
     * Not olaylarını yayınlamak için kullanılan RxJava PublishSubject.
     */
    private final PublishSubject<NoteEvent> noteSubject = PublishSubject.create();

    /**
     * Not olaylarını gözlemlemek için bir Observable döndürür.
     *
     * @return Not olaylarını yayınlayan Observable.
     */
    public Observable<NoteEvent> getNoteObservable() {
        return noteSubject.hide();
    }

    /**
     * Yeni bir kullanıcı notu oluşturur ve olay olarak yayınlar.
     *
     * @param entity Oluşturulacak not nesnesi.
     * @return Oluşturulan not nesnesi.
     */
    @Override
    public UserNote create(UserNote entity) {
        entity.setReportAt(LocalDateTime.now());
        UserNote createdNote = userNoteRepository.create(entity, UserNoteQuery.CREATE_NOTE, List.of(
                entity.getUserId(),
                entity.getReportAt(),
                entity.getHeader(),
                entity.getDescription(),
                entity.getVersion()
        ));
        noteSubject.onNext(new NoteEvent(NoteEventType.CREATE, createdNote)); // Oluşturma olayını yayınla
        return createdNote;
    }

    /**
     * Belirtilen kimlik numarasına sahip notu siler ve olay olarak yayınlar.
     *
     * @param id Silinecek notun kimlik numarası.
     */
    @Override
    public void delete(int id) {
        UserNote note = userNoteRepository.read(UserNote.class, UserNoteQuery.READ_NOTE_BY_ID, List.of(id));
        userNoteRepository.delete(UserNoteQuery.DELETE_NOTE_BY_ID, List.of(id));
        if (note != null) {
            noteSubject.onNext(new NoteEvent(NoteEventType.DELETE, note)); // Silme olayını yayınla
        }
    }

    /**
     * Belirtilen kimlik numarasına sahip notu okur ve geri çağırım ile iletir.
     *
     * @param id       Okunacak notun kimlik numarası.
     * @param callback Notun iletileceği geri çağırım fonksiyonu.
     */
    @Override
    public void read(int id, Consumer<UserNote> callback) {
        UserNote note = userNoteRepository.read(UserNote.class, UserNoteQuery.READ_NOTE_BY_ID, List.of(id));
        callback.accept(note);
    }

    /**
     * Belirtilen kullanıcıya ait tüm notları okur.
     *
     * @param userId Kullanıcı kimlik numarası.
     * @return Kullanıcıya ait notların listesi.
     */
    @Override
    public List<UserNote> readAll(int userId) {
        return userNoteRepository.readAll(UserNote.class, UserNoteQuery.READ_ALL_NOTES_BY_USER_ID, List.of(userId));
    }

    /**
     * Mevcut bir notu günceller, olay olarak yayınlar ve geri çağırım ile iletir.
     *
     * @param entity   Güncellenecek not nesnesi.
     * @param callback Güncellenen notun iletileceği geri çağırım fonksiyonu.
     */
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
        noteSubject.onNext(new NoteEvent(NoteEventType.UPDATE, updated)); // Güncelleme olayını yayınla
        callback.accept(updated);
    }
}