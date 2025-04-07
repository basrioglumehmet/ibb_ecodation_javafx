package org.example.ibb_ecodation_javafx.statemanagement.action;

import org.example.ibb_ecodation_javafx.model.User;
import org.example.ibb_ecodation_javafx.model.dto.UserDetailDto;

public class LoginAction {
    private final UserDetailDto userDetail;

    public LoginAction(UserDetailDto userDetail) {
        this.userDetail = userDetail;
    }

    public UserDetailDto getUserDetail() {
        return userDetail;
    }
}
