package org.example.ibb_ecodation_javafx.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import org.example.ibb_ecodation_javafx.core.context.SpringContext;
import org.example.ibb_ecodation_javafx.core.logger.SecurityLogger;
import org.example.ibb_ecodation_javafx.core.service.LanguageService;
import org.example.ibb_ecodation_javafx.model.User;
import org.example.ibb_ecodation_javafx.model.UserPicture;
import org.example.ibb_ecodation_javafx.model.dto.UserDetailDto;
import org.example.ibb_ecodation_javafx.service.UserPictureService;
import org.example.ibb_ecodation_javafx.service.UserService;
import org.example.ibb_ecodation_javafx.statemanagement.Store;
import org.example.ibb_ecodation_javafx.statemanagement.state.DarkModeState;
import org.example.ibb_ecodation_javafx.statemanagement.state.UserState;
import org.example.ibb_ecodation_javafx.ui.avatar.ShadcnAvatar;
import org.example.ibb_ecodation_javafx.ui.button.ShadcnButton;
import org.example.ibb_ecodation_javafx.ui.combobox.ShadcnLanguageComboBox;
import org.example.ibb_ecodation_javafx.ui.dragndrop.Upload;
import org.example.ibb_ecodation_javafx.ui.input.ShadcnInput;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
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
    @FXML private Label pageTitle;
    @FXML private Upload upload;

    private final UserService userService;
    private final UserPictureService userPictureService;
    private final SecurityLogger securityLogger;
    private final LanguageService languageService;
    private final Store store = Store.getInstance();

    public AdminProfileController() {
        this.userService = SpringContext.getContext().getBean(UserService.class);
        this.userPictureService = SpringContext.getContext().getBean(UserPictureService.class);
        this.securityLogger = SpringContext.getContext().getBean(SecurityLogger.class);
        this.languageService = SpringContext.getContext().getBean(LanguageService.class);
    }

    public void initialize() {
        setPageTitle();
        initializeUserProfile();
    }

    private void setPageTitle() {
        pageTitle.setText(languageService.translate("label.profile"));
        pageTitle.setStyle("-fx-font-size:24;" +
                String.format("-fx-text-fill:%s;", store.getCurrentState(DarkModeState.class).isEnabled() ? "white" : "black"));
    }

    private void initializeUserProfile() {
        String languageCode = ShadcnLanguageComboBox.getCurrentLanguageCode();

        securityLogger.logOperation(languageService.translate("log.profile.opened"));

        setInputLabels();
        setAvatarImageSource();

        userService.read(1, user -> {
            if (user != null) {
                userPictureService.read(user.getId(), userPicture -> {
                    if (userPicture != null) {
                        updateUserProfileFields(user, userPicture);
                    }
                });
            }
        });
    }

    private void setInputLabels() {
        email.setHeader(languageService.translate("input.email.placeholder"));
        username.setHeader(languageService.translate("input.username.placeholder"));
        password.setHeader(languageService.translate("input.password.placeholder"));
        role.setHeader(languageService.translate("input.role.placeholder"));
        update.setText(languageService.translate("update"));
        upload.setLabelText(languageService.translate("dragndrop"));
    }

    private void updateUserProfileFields(User user, UserPicture userPicture) {
        UserDetailDto userDetail = mapUserToUserDetailDto(user, userPicture);

        username.setText(userDetail.getUsername());
        email.setText(userDetail.getEmail());
        role.setText(userDetail.getRole());
    }

    private UserDetailDto mapUserToUserDetailDto(User user, UserPicture userPicture) {

        byte[] profilePictureBytes = (userPicture != null) ? userPicture.getImageData() : null;

        return new UserDetailDto(
                user.getId(),
                profilePictureBytes,
                user.getUsername(),
                user.getEmail(),
                user.getRole().toString(),
                user.isVerified(),
                user.isLocked()
        );
    }

    public void updateProfile(ActionEvent actionEvent) {
        try {
            File uploadedFile = new File(upload.getDroppedImagePath());
            if (uploadedFile.exists()) {
                updateAvatarImage(uploadedFile);
            } else {
                logAndThrowException("error.file.not.found", uploadedFile.getAbsolutePath());
            }
        } catch (IOException e) {
            logAndThrowException("error.image.read", e.getMessage());
        }
    }
    private void updateAvatarImage(File uploadedFile) throws IOException {
        BufferedImage bufferedImage = ImageIO.read(uploadedFile);
        Image image = convertBufferedImageToImage(bufferedImage);
        shadcnAvatar.setImage(image);

        userPictureService.read(28, picture -> {
            try {
                if (picture == null) {
                    var entity = new UserPicture(
                            28,
                            convertImageToByteArray(bufferedImage),
                            0
                    );

                    userPictureService.create(entity);
                } else {
                    updateUserProfilePicture(uploadedFile, picture);
                }
            } catch (IOException e) {
                logAndThrowException("error.picture.read", e.getMessage());
            }
        });
    }



    private Image convertBufferedImageToImage(BufferedImage bufferedImage) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try {
            ImageIO.write(bufferedImage, "png", byteArrayOutputStream);
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
            return new Image(byteArrayInputStream);
        } catch (IOException e) {
            throw new RuntimeException("Failed to convert BufferedImage to Image", e);
        }
    }

    private void updateUserProfilePicture(File uploadedFile, UserPicture picture) throws IOException {
        byte[] imageBytes = convertImageToByteArray(ImageIO.read(uploadedFile));
        if (picture == null) {
            picture = createDefaultUserPicture(imageBytes);
        }
        picture.setImageData(imageBytes);

        userPictureService.update(picture, updateCallback -> {
            updateStateWithNewProfilePicture(imageBytes);
            securityLogger.logOperation(languageService.translate("log.profile.updated"));
        });
    }

    private UserPicture createDefaultUserPicture(byte[] imageBytes) {
        return new UserPicture(0, imageBytes, 0);
    }

    private void updateStateWithNewProfilePicture(byte[] imageBytes) {
        var userState = store.getCurrentState(UserState.class).getUserDetail();
        userState.setProfilePicture(imageBytes);
        store.dispatch(UserState.class, new UserState(
                userState,
                store.getCurrentState(UserState.class).isLoggedIn(),
                null,
                store.getCurrentState(UserState.class).getSelectedUserNote()
        ));
    }

    private void logAndThrowException(String errorCode, String message) {
        securityLogger.logOperation(languageService.translate(errorCode) + ": " + message);
        throw new RuntimeException(languageService.translate(errorCode) + ": " + message);
    }

    private void setAvatarImageSource() {
        try {
            UserDetailDto userDetail = store.getCurrentState(UserState.class).getUserDetail();
            if (userDetail != null && userDetail.getProfilePicture() != null) {
                byte[] imageBytes = userDetail.getProfilePicture();
                if (imageBytes.length > 0) {
                    Image image = new Image(new ByteArrayInputStream(imageBytes));
                    shadcnAvatar.setImage(image);
                    shadcnAvatar.setAvatarSize(40);
                    return;
                }
            }
            setDefaultAvatarImage();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setDefaultAvatarImage() {
        shadcnAvatar.setImage(AdminDashboardController.class.getResource("/org/example/ibb_ecodation_javafx/assets/avatar.jpg"));
    }
}
