package org.example.ibb_ecodation_javafx.statemanagement.state;

import org.example.ibb_ecodation_javafx.model.User;
import org.example.ibb_ecodation_javafx.model.dto.UserDetailDto;
import org.example.ibb_ecodation_javafx.statemanagement.AppState;

public class UserState extends AppState {
    private final UserDetailDto userDetail;
    private final boolean isLoggedIn;

    public UserState(UserDetailDto userDetail, boolean isLoggedIn) {
        this.userDetail = userDetail;
        this.isLoggedIn = isLoggedIn;
    }

    public UserDetailDto getUserDetail() {
        return userDetail;
    }

    public boolean isLoggedIn() {
        return isLoggedIn;
    }

    public UserState login(UserDetailDto userDetail) {
        return new UserState(userDetail, true);
    }

    public UserState logout() {
        return new UserState(null, false);
    }

    @Override
    public String toString() {
        return isLoggedIn ? "User: " + userDetail.toString() + " (Logged In)" : "No user logged in.";
    }
}
