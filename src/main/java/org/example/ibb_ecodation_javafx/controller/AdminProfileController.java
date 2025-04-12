package org.example.ibb_ecodation_javafx.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import org.example.ibb_ecodation_javafx.core.context.SpringContext;
import org.example.ibb_ecodation_javafx.core.logger.SecurityLogger;
import org.example.ibb_ecodation_javafx.core.service.LanguageService;
import org.example.ibb_ecodation_javafx.exception.OptimisticLockException;
import org.example.ibb_ecodation_javafx.model.User;
import org.example.ibb_ecodation_javafx.model.UserPicture;
import org.example.ibb_ecodation_javafx.model.dto.UserDetailDto;
import org.example.ibb_ecodation_javafx.model.enums.Role;
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
import org.mindrot.jbcrypt.BCrypt;

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
        setDefaultAvatarImage(); // Set default avatar until data is loaded

        // Fetch the latest user data
        var state = store.getCurrentState(UserState.class);
        UserDetailDto userDetail = state.getUserDetail();
        if (userDetail == null) {
            logAndThrowException("error.user.notfound", "User detail not found in state");
            return;
        }

        userService.read(userDetail.getUserId(), user -> {
            if (user != null) {
                userPictureService.read(user.getId(), userPicture -> {
                    // Update state with fresh data
                    UserDetailDto updatedUserDetail = mapUserToUserDetailDto(user, userPicture);
                    store.dispatch(UserState.class,
                            new UserState(
                                    updatedUserDetail,
                                    state.isLoggedIn(),
                                    state.getSelectedUser(),
                                    state.getSelectedUserNote()
                            ));

                    // Update UI with fresh data
                    updateUserProfileFields(user, userPicture);
                    setAvatarImageSource();
                });
            } else {
                logAndThrowException("error.user.notfound", "User not found in database");
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
        // Add loading placeholder
        email.setText("Loading...");
        username.setText("Loading...");
        role.setText("Loading...");
    }

    private void updateUserProfileFields(User user, UserPicture userPicture) {
        UserDetailDto userDetail = mapUserToUserDetailDto(user, userPicture);
        if(userDetail.getRole().equals(Role.USER.toString())){
            role.setVisible(false);
        }
        username.setText(userDetail.getUsername());
        email.setText(userDetail.getEmail());
        role.setText(userDetail.getRole());
        // Clear loading placeholders
        email.setText(userDetail.getEmail());
        username.setText(userDetail.getUsername());
        role.setText(userDetail.getRole());
    }

    private UserDetailDto mapUserToUserDetailDto(User user, UserPicture userPicture) {
        byte[] profilePictureBytes = (userPicture != null) ? userPicture.getImageData() : null;

        return new UserDetailDto(
                user.getId(),
                profilePictureBytes,
                user.getUsername(),
                user.getEmail(),
                user.getPassword(),
                user.getRole().toString(),
                user.isVerified(),
                user.isLocked(),
                user.getVersion()
        );
    }

    public void updateProfile(ActionEvent actionEvent) {
        update.setDisable(true); // Disable button to prevent multiple clicks
        try {
            // Fetch the latest user data to ensure correct version
            var state = store.getCurrentState(UserState.class);
            UserDetailDto userDetail = state.getUserDetail();
            if (userDetail == null) {
                logAndThrowException("error.user.notfound", "User detail not found");
                return;
            }

            // Validate input
            String usernameValue = username.getText();
            String emailValue = email.getText();
            String roleValue = role.getText();
            String passwordValue = password.getText();
            if (!validateInput(usernameValue, emailValue, roleValue)) {
                return;
            }

            // Read the latest user from the database
            userService.read(userDetail.getUserId(), latestUser -> {
                if (latestUser == null) {
                    logAndThrowException("error.user.notfound", "User not found in database");
                    return;
                }

                // Check if an image has been uploaded
                if (upload.getDroppedImagePath() != null) {
                    File uploadedFile = new File(upload.getDroppedImagePath());
                    if (uploadedFile.exists()) {
                        try {
                            updateAvatarImage(uploadedFile, userDetail.getUserId());
                        } catch (IOException e) {
                            logAndThrowException("error.image.read", e.getMessage());
                        }
                    }
                }

                // Update user information
                User updatedUser = new User(
                        latestUser.getId(),
                        usernameValue,
                        emailValue,
                        passwordValue.isEmpty() ? latestUser.getPassword() : BCrypt.hashpw(passwordValue, BCrypt.gensalt(12)),
                        Role.fromString(roleValue),
                        latestUser.isVerified(),
                        latestUser.isLocked(),
                        latestUser.getVersion() // Use the latest version
                );

                // Attempt to update the user
                try {
                    userService.update(updatedUser, updateCallback -> {
                        // Fetch the latest user data again to ensure we have the most recent state
                        userService.read(userDetail.getUserId(), finalUser -> {
                            if (finalUser != null) {
                                userPictureService.read(finalUser.getId(), finalUserPicture -> {
                                    // Create updated UserDetailDto with the latest data
                                    UserDetailDto updatedUserDetail = mapUserToUserDetailDto(finalUser, finalUserPicture);
                                    store.dispatch(UserState.class,
                                            new UserState(
                                                    updatedUserDetail,
                                                    state.isLoggedIn(),
                                                    state.getSelectedUser(),
                                                    state.getSelectedUserNote()
                                            ));
                                    // Update UI with latest data
                                    updateUserProfileFields(finalUser, finalUserPicture);
                                    setAvatarImageSource();


                                    securityLogger.logOperation(languageService.translate("log.profile.updated"));
                                });
                            } else {
                                logAndThrowException("error.user.notfound", "User not found after update");
                            }
                        });
                    });
                } catch (Exception e) {
                    if (e.getCause() instanceof OptimisticLockException) {
                        logAndThrowException("error.optimistic.lock", "Version conflict, please try again");
                    } else {
                        throw e;
                    }
                }
            });

        } catch (Exception e) {
            logAndThrowException("error.profile.update", e.getMessage());
        } finally {
            update.setDisable(false); // Re-enable button
        }
    }

    private boolean validateInput(String username, String email, String role) {
        if (username.isEmpty() || email.isEmpty() || role.isEmpty()) {
            logAndThrowException("error.validation", "All fields are required");
            return false;
        }
        if (!email.matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
            logAndThrowException("error.validation", "Invalid email format");
            return false;
        }
        try {
            Role.fromString(role);
        } catch (IllegalArgumentException e) {
            logAndThrowException("error.validation", "Invalid role");
            return false;
        }
        return true;
    }

    private void updateAvatarImage(File uploadedFile, int userId) throws IOException {
        BufferedImage bufferedImage = ImageIO.read(uploadedFile);
        Image image = convertBufferedImageToImage(bufferedImage);
        shadcnAvatar.setImage(image);

        userPictureService.read(userId, picture -> {
            try {
                if (picture == null) {
                    var entity = new UserPicture(
                            userId,
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
        picture.setImageData(imageBytes);

        userPictureService.update(picture, updateCallback -> {
            updateStateWithNewProfilePicture(imageBytes);
            securityLogger.logOperation(languageService.translate("log.profile.updated"));
        });
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

    private void setAvatarImageSource() {
        try {
            UserDetailDto userDetail = store.getCurrentState(UserState.class).getUserDetail();
            if (userDetail != null && userDetail.getProfilePicture() != null && userDetail.getProfilePicture().length > 0) {
                Image image = new Image(new ByteArrayInputStream(userDetail.getProfilePicture()));
                shadcnAvatar.setImage(image);

            } else {
                setDefaultAvatarImage();
            }
            shadcnAvatar.setAvatarSize(40);
        } catch (Exception e) {
            securityLogger.logOperation(languageService.translate("error.avatar.load") + ": " + e.getMessage());
            setDefaultAvatarImage();
        }
    }

    private void setDefaultAvatarImage() {
        shadcnAvatar.setImage(AdminDashboardController.class.getResource("/org/example/ibb_ecodation_javafx/assets/avatar.jpg"));
    }

    private void logAndThrowException(String errorCode, String message) {
        String logMessage = String.format(
                "%s: %s (User ID: %d)",
                languageService.translate(errorCode),
                message,
                store.getCurrentState(UserState.class).getUserDetail() != null ?
                        store.getCurrentState(UserState.class).getUserDetail().getUserId() : -1
        );
        securityLogger.logOperation(logMessage);
        throw new RuntimeException(logMessage);
    }
}