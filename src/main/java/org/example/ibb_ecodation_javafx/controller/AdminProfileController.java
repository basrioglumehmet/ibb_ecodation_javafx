package org.example.ibb_ecodation_javafx.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import org.example.ibb_ecodation_javafx.core.context.SpringContext;
import org.example.ibb_ecodation_javafx.core.logger.SecurityLogger;
import org.example.ibb_ecodation_javafx.core.service.LanguageService;
import org.example.ibb_ecodation_javafx.service.UserPictureService;
import org.example.ibb_ecodation_javafx.service.UserService;
import org.example.ibb_ecodation_javafx.statemanagement.Store;
import org.example.ibb_ecodation_javafx.statemanagement.state.UserState;
import org.example.ibb_ecodation_javafx.ui.avatar.ShadcnAvatar;
import org.example.ibb_ecodation_javafx.ui.button.ShadcnButton;
import org.example.ibb_ecodation_javafx.ui.combobox.ShadcnLanguageComboBox;
import org.example.ibb_ecodation_javafx.ui.dragndrop.Upload;
import org.example.ibb_ecodation_javafx.ui.input.ShadcnInput;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

import static org.example.ibb_ecodation_javafx.utils.ImageUtil.convertImageToByteArray;

public class AdminProfileController {

    @FXML private ShadcnAvatar shadcnAvatar;
    @FXML private ShadcnInput email;
    @FXML private ShadcnInput username;
    @FXML private ShadcnInput password;
    @FXML private ShadcnInput role;
    @FXML private ShadcnButton update;

    private final UserService userService;
    private final UserPictureService userPictureService;
    private final SecurityLogger securityLogger;
    private final LanguageService languageService;
    private final Store store = Store.getInstance();

    @FXML private Upload upload;

    public AdminProfileController() {
        this.userService = SpringContext.getContext().getBean(UserService.class);
        this.userPictureService = SpringContext.getContext().getBean(UserPictureService.class);
        this.securityLogger = SpringContext.getContext().getBean(SecurityLogger.class);
        this.languageService = SpringContext.getContext().getBean(LanguageService.class);
    }

    public void initialize() {

        String languageCode = ShadcnLanguageComboBox.getCurrentLanguageCode();


        securityLogger.logOperation(languageService.translate("log.profile.opened"));


        email.setHeader(languageService.translate("input.email.placeholder"));
        username.setHeader(languageService.translate("input.username.placeholder"));
        password.setHeader(languageService.translate("input.password.placeholder"));
        role.setHeader(languageService.translate("input.role.placeholder"));
        update.setText(languageService.translate("update"));
        upload.setLabelText(languageService.translate("dragndrop"));

        setAvatarImageSource();


        userService.read(1, user -> {
            username.setText(user.getUsername());
            email.setText(user.getEmail());
            role.setText(languageService.translate("role." + user.getRole().toString().toLowerCase()));
            password.setText("");
        });
    }

    public void updateProfile(ActionEvent actionEvent) {
        try {
            File file = new File(this.upload.getDroppedImagePath());
            if (file.exists()) {
                shadcnAvatar.setImage(ImageIO.read(file));
                userPictureService.read(1, callback -> {
                    try {
                        callback.setImageData(convertImageToByteArray(ImageIO.read(file)));
                        userPictureService.update(callback, updateCallback -> {
                            var userState = store.getCurrentState(UserState.class).getUserDetail();
                            try {
                                userState.setProfilePicture(convertImageToByteArray(ImageIO.read(file)));
                                store.dispatch(UserState.class, new UserState(
                                        userState,
                                        store.getCurrentState(UserState.class).isLoggedIn(),
                                        null,
                                        store.getCurrentState(UserState.class).getSelectedUserNote()
                                ));
                                securityLogger.logOperation(languageService.translate("log.profile.updated"));
                            } catch (IOException e) {
                                throw new RuntimeException(languageService.translate("error.profile.picture.update") + ": " + e.getMessage());
                            }
                        });
                    } catch (IOException e) {
                        throw new RuntimeException(languageService.translate("error.picture.read") + ": " + e.getMessage());
                    }
                });
            } else {
                System.out.println(languageService.translate("error.file.not.found") + ": " + file.getAbsolutePath());
            }
        } catch (IOException e) {
            securityLogger.logOperation(languageService.translate("error.image.read") + ": " + e.getMessage());
            throw new RuntimeException(languageService.translate("error.image.read") + ": " + e.getMessage());
        }
    }

    private void setAvatarImageSource() {
        try {
            shadcnAvatar.setAvatarSize(60);
            shadcnAvatar.setImage(AdminProfileController.class.getResource("/org/example/ibb_ecodation_javafx/assets/avatar.jpg"));
        } catch (NullPointerException e) {
            securityLogger.logOperation(languageService.translate("error.avatar.load") + ": " + e.getMessage());
        }
    }
}