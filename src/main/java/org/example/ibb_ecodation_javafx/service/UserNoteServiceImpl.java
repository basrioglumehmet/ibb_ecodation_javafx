package org.example.ibb_ecodation_javafx.service;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.subjects.PublishSubject;
import lombok.RequiredArgsConstructor;
import org.example.ibb_ecodation_javafx.core.db.EntityFilter;
import org.example.ibb_ecodation_javafx.model.UserNote;
import org.example.ibb_ecodation_javafx.model.enums.NoteEventType;
import org.example.ibb_ecodation_javafx.model.event.NoteEvent;
import org.example.ibb_ecodation_javafx.repository.UserNoteRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class UserNoteServiceImpl implements UserNoteService {

    private final UserNoteRepository userNoteRepository;
    private static final PublishSubject<NoteEvent> NOTE_EVENT_PUBLISH_SUBJECT = PublishSubject.create();
    
    @Override
    public UserNote save(UserNote entity) {
        NOTE_EVENT_PUBLISH_SUBJECT.onNext(new NoteEvent(NoteEventType.CREATE));
        return userNoteRepository.save(entity);

    }

    @Override
    public void update(UserNote entity) {
        NOTE_EVENT_PUBLISH_SUBJECT.onNext(new NoteEvent(NoteEventType.UPDATE));
        userNoteRepository.update(entity);
    }

    @Override
    public Optional<UserNote> findById(Integer integer) {
        return userNoteRepository.findById(integer);
    }

    @Override
    public List<UserNote> findAll() {
        return userNoteRepository.findAll();
    }

    @Override
    public List<UserNote> findAllById(Integer id) {
        return userNoteRepository.findAllById(id);
    }

    @Override
    public List<UserNote> findAllByFilter(List<EntityFilter> filters) {
        return List.of();
    }

    @Override
    public void delete(Integer integer) {
        userNoteRepository.delete(integer);
        NOTE_EVENT_PUBLISH_SUBJECT.onNext(new NoteEvent(NoteEventType.DELETE));
    }

    @Override
    public Optional<UserNote> findFirstByFilter(List<EntityFilter> filters) {
        return userNoteRepository.findFirstByFilter(filters);
    }

    public Observable<NoteEvent> getNoteObservable(){
        return NOTE_EVENT_PUBLISH_SUBJECT.hide();
    }
}
