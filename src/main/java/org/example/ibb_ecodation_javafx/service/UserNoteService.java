package org.example.ibb_ecodation_javafx.service;

import io.reactivex.rxjava3.core.Observable;
import org.example.ibb_ecodation_javafx.core.service.GenericService;
import org.example.ibb_ecodation_javafx.model.UserNote;
import org.example.ibb_ecodation_javafx.model.event.NoteEvent;

public interface UserNoteService extends GenericService<UserNote,Integer> {
    Observable<NoteEvent> getNoteObservable();
}
