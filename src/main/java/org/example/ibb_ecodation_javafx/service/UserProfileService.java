package org.example.ibb_ecodation_javafx.service;

import org.example.ibb_ecodation_javafx.model.UserPicture;
import org.example.ibb_ecodation_javafx.model.dto.UserDetailDto;

public interface UserProfileService {
    void  updateProfile(UserDetailDto userDetailDto, UserPicture userPicture);
}
