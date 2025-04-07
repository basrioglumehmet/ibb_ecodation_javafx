package org.example.ibb_ecodation_javafx.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.ibb_ecodation_javafx.model.UserPicture;
import org.example.ibb_ecodation_javafx.repository.UserPictureRepository;
import org.example.ibb_ecodation_javafx.repository.query.UserPictureQuery;
import org.example.ibb_ecodation_javafx.service.UserPictureService;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.List;
import java.util.function.Consumer;

@Service
@RequiredArgsConstructor
public class UserPictureServiceImpl implements UserPictureService {
    private final UserPictureRepository userPictureRepository;

    @Override
    public void create(UserPicture entity) {
           userPictureRepository.create(entity, UserPictureQuery.CREATE_USER_PICTURE, List.of(entity.getUserId(),entity.getImageData()));
    }

    @Override
    public UserPicture delete(int id) {
        return null;
    }

    @Override
    public void read(int id, Consumer<UserPicture> callback) {
        callback.accept(userPictureRepository.read(UserPicture.class,UserPictureQuery.READ_USER_PICTURE_BY_USER_ID,List.of(id)));
    }

    @Override
    public void update(UserPicture entity, Consumer<UserPicture> callback) {
        callback.accept(userPictureRepository.update(entity,UserPictureQuery.UPDATE_USER_PICTURE_BY_USER_ID, List.of(
                entity.getImageData(),
                entity.getUserId(),
                entity.getVersion()
        )));
    }
}
