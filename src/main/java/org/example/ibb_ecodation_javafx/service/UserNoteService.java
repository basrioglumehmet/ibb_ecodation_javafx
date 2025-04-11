package org.example.ibb_ecodation_javafx.service;

import io.reactivex.rxjava3.core.Observable;
import org.example.ibb_ecodation_javafx.core.service.Crud;
import org.example.ibb_ecodation_javafx.model.UserNote;
import org.example.ibb_ecodation_javafx.model.event.NoteEvent;

public interface UserNoteService extends Crud<UserNote> {
    /**
     * Not olaylarını gözlemlemek için bir Observable döndürür.
     *
     * @return Not olaylarını yayınlayan Observable.
     */
    Observable<NoteEvent> getNoteObservable();
}
