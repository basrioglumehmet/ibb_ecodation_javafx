package org.example.ibb_ecodation_javafx.statemanagement.state;

import org.example.ibb_ecodation_javafx.statemanagement.AppState;

public class UserState extends AppState {
    private final String username;
    private final boolean isLoggedIn;

    public UserState(String username, boolean isLoggedIn) {
        this.username = username;
        this.isLoggedIn = isLoggedIn;
    }

    public String getUsername() {
        return username;
    }

    public boolean isLoggedIn() {
        return isLoggedIn;
    }

    public UserState login(String username) {
        return new UserState(username, true);
    }

    public UserState logout() {
        return new UserState("", false);
    }

    @Override
    public String toString() {
        return isLoggedIn ? "User: " + username + " (Logged In)" : "No user logged in.";
    }
}
