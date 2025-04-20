package org.example.ibb_ecodation_javafx.service;

import lombok.RequiredArgsConstructor;
import org.example.ibb_ecodation_javafx.core.db.EntityFilter;
import org.example.ibb_ecodation_javafx.model.UserPicture;
import org.example.ibb_ecodation_javafx.repository.UserPictureRepository;
import org.example.ibb_ecodation_javafx.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserPictureServiceImpl implements UserPictureService{

    private final UserPictureRepository userRepository;

    @Override
    public UserPicture save(UserPicture entity) {
        return userRepository.save(entity);
    }

    @Override
    public void update(UserPicture entity) {
        userRepository.update(entity);
    }

    /**
     * DİKKAT! USER ID GÖRE İŞLEM YAPILIR
     * @param integer
     * @return List of User pictures
     */
    @Override
    public Optional<UserPicture> findById(Integer integer) {
        return userRepository.findById(integer);
    }

    @Override
    public List<UserPicture> findAll() {
        return userRepository.findAll();
    }

    /**
     * DİKKAT! USER ID GÖRE İŞLEM YAPILIR
     * @param id
     * @return List of User pictures
     */
    @Override
    public List<UserPicture> findAllById(Integer id) {
        return userRepository.findAllById(id);
    }

    @Override
    public List<UserPicture> findAllByFilter(List<EntityFilter> filters) {
        return List.of();
    }

    /**
     * DİKKAT! USER ID GÖRE İŞLEM YAPILIR
     * @param integer
     * @return
     */
    @Override
    public void delete(Integer integer) {
        userRepository.delete(integer);
    }

    @Override
    public Optional<UserPicture> findFirstByFilter(List<EntityFilter> filters) {
        return userRepository.findFirstByFilter(filters);
    }
}
