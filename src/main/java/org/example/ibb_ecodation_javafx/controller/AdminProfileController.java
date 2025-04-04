package org.example.ibb_ecodation_javafx.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import org.example.ibb_ecodation_javafx.statemanagement.Store;
import org.example.ibb_ecodation_javafx.statemanagement.state.DarkModeState;
import org.example.ibb_ecodation_javafx.ui.avatar.ShadcnAvatar;

import static org.example.ibb_ecodation_javafx.utils.LabelUtil.updateLabelStyles;


public class AdminProfileController {
    @FXML
    private Label header;
    @FXML
    private ShadcnAvatar shadcnAvatar;

    private final Store store = Store.getInstance();
    public void initialize(){
        updateLabelStyles(header.getParent(), store.getCurrentState(DarkModeState.class).isEnabled() ? "black" : "white");
        setAvatarImageSource();
    }

    private void setAvatarImageSource() {
        try {
            shadcnAvatar.setAvatarSize(80);
            shadcnAvatar.setImage(AdminProfileController.class.getResource("/org/example/ibb_ecodation_javafx/assets/avatar.jpg"));
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }
}
