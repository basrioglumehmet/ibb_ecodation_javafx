package org.example.ibb_ecodation_javafx.service;

import lombok.RequiredArgsConstructor;
import org.example.ibb_ecodation_javafx.model.User;
import org.example.ibb_ecodation_javafx.model.UserPicture;
import org.example.ibb_ecodation_javafx.model.dto.UserDetailDto;
import org.example.ibb_ecodation_javafx.model.enums.Role;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class UserProfileServiceImpl implements UserProfileService {

    private final UserService userService;
    private final UserPictureService userPictureService;

    @Override
    public void updateProfile(UserDetailDto userDetail, UserPicture userPicture) {
        userService.findById(userDetail.getUserId()).ifPresent(user -> {
            if(userPicture != null){
                updateProfilePicture(userPicture);
            }
            updateUserDetails(user, userDetail);
            userService.update(user);
        });
    }

    private void updateProfilePicture(UserPicture userPicture) {
        if (userPicture.getVersion() != 1) {
            userPictureService.update(userPicture);
        }
        else{
            userPictureService.save(userPicture);
        }
    }

    private void updateUserDetails(User user, UserDetailDto userDetail) {
        user.setUsername(userDetail.getUsername());
        user.setEmail(userDetail.getEmail());
        user.setPassword(userDetail.getPassword());
        user.setRole(Role.fromString(userDetail.getRole()));
    }
}