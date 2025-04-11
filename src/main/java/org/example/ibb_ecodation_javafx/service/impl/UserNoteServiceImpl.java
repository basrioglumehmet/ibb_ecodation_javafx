package org.example.ibb_ecodation_javafx.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.ibb_ecodation_javafx.model.UserNote;
import org.example.ibb_ecodation_javafx.repository.UserNoteRepository;
import org.example.ibb_ecodation_javafx.repository.query.UserNoteQuery;
import org.example.ibb_ecodation_javafx.service.UserNoteService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.function.Consumer;

@RequiredArgsConstructor
@Service
public class UserNoteServiceImpl implements UserNoteService {

    private final UserNoteRepository userNoteRepository;

    @Override
    public UserNote create(UserNote entity) {
        entity.setReportAt(LocalDateTime.now()); // notun yazıldığı zaman
        return userNoteRepository.create(entity, UserNoteQuery.CREATE_NOTE, List.of(
                entity.getUserId(),
                entity.getReportAt(),
                entity.getHeader(),
                entity.getDescription(),
                entity.getVersion()
        ));
    }

    @Override
    public void delete(int id) {
        userNoteRepository.delete(UserNoteQuery.DELETE_NOTE_BY_ID, List.of(id));
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
        callback.accept(updated);
    }
}
