package org.example.ibb_ecodation_javafx.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import org.example.ibb_ecodation_javafx.core.context.SpringContext;
import org.example.ibb_ecodation_javafx.core.logger.SecurityLogger;
import org.example.ibb_ecodation_javafx.core.service.LanguageService;
import org.example.ibb_ecodation_javafx.core.validation.FieldValidator;
import org.example.ibb_ecodation_javafx.core.validation.ValidationError;
import org.example.ibb_ecodation_javafx.core.validation.ValidationRule;
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
import java.util.regex.Pattern;

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

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");

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
        setDefaultAvatarImage();

        var state = store.getCurrentState(UserState.class);
        UserDetailDto userDetail = state.getUserDetail();
        if (userDetail == null) {
            logAndThrowException("error.user.notfound", "User detail not found in state");
            return;
        }

        userService.read(userDetail.getUserId(), user -> {
            if (user != null) {
                userPictureService.read(user.getId(), userPicture -> {
                    UserDetailDto updatedUserDetail = mapUserToUserDetailDto(user, userPicture);
                    store.dispatch(UserState.class,
                            new UserState(
                                    updatedUserDetail,
                                    state.isLoggedIn(),
                                    state.getSelectedUser(),
                                    state.getSelectedUserNote()
                            ));
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
        email.setText("Loading...");
        username.setText("Loading...");
        role.setText("Loading...");
    }

    private void updateUserProfileFields(User user, UserPicture userPicture) {
        UserDetailDto userDetail = mapUserToUserDetailDto(user, userPicture);
        if (userDetail.getRole().equals(Role.USER.toString())) {
            role.setVisible(false);
            role.setManaged(false);
        }
        username.setText(userDetail.getUsername());
        email.setText(userDetail.getEmail());
        role.setText(userDetail.getRole());
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
        update.setDisable(true);
        username.clearError();
        email.clearError();
        role.clearError();

        FieldValidator validator = new FieldValidator();

        validator.addRule(new ValidationRule<String>() {
            @Override
            public String getValue() {
                return username.getText().trim();
            }

            @Override
            public boolean validate(String value) {
                return !value.isEmpty();
            }

            @Override
            public String getErrorMessage() {
                return languageService.translate("input.username.empty");
            }

            @Override
            public ShadcnInput getInput() {
                return username;
            }
        });

        validator.addRule(new ValidationRule<String>() {
            @Override
            public String getValue() {
                return email.getText().trim();
            }

            @Override
            public boolean validate(String value) {
                return !value.isEmpty();
            }

            @Override
            public String getErrorMessage() {
                return languageService.translate("input.email.empty");
            }

            @Override
            public ShadcnInput getInput() {
                return email;
            }
        });

        validator.addRule(new ValidationRule<String>() {
            @Override
            public String getValue() {
                return email.getText().trim();
            }

            @Override
            public boolean validate(String value) {
                return value.isEmpty() || EMAIL_PATTERN.matcher(value).matches();
            }

            @Override
            public String getErrorMessage() {
                return languageService.translate("input.email.invalid");
            }

            @Override
            public ShadcnInput getInput() {
                return email;
            }
        });

        validator.addRule(new ValidationRule<String>() {
            @Override
            public String getValue() {
                return role.getText().trim();
            }

            @Override
            public boolean validate(String value) {
                return !value.isEmpty();
            }

            @Override
            public String getErrorMessage() {
                return languageService.translate("input.role.empty");
            }

            @Override
            public ShadcnInput getInput() {
                return role;
            }
        });

        validator.addRule(new ValidationRule<String>() {
            @Override
            public String getValue() {
                return role.getText().trim();
            }

            @Override
            public boolean validate(String value) {
                try {
                    Role.fromString(value);
                    return true;
                } catch (IllegalArgumentException e) {
                    return false;
                }
            }

            @Override
            public String getErrorMessage() {
                return languageService.translate("input.role.invalid");
            }

            @Override
            public ShadcnInput getInput() {
                return role;
            }
        });

        validator.onError(error -> error.getInput().setError(error.getErrorDetail()));

        if (validator.runValidatorEngine().isEmpty()) {
            try {
                var state = store.getCurrentState(UserState.class);
                UserDetailDto userDetail = state.getUserDetail();
                if (userDetail == null) {
                    logAndThrowException("error.user.notfound", "User detail not found");
                    return;
                }

                String usernameValue = username.getText().trim();
                String emailValue = email.getText().trim();
                String roleValue = role.getText().trim();
                String passwordValue = password.getText().trim();

                userService.read(userDetail.getUserId(), latestUser -> {
                    if (latestUser == null) {
                        logAndThrowException("error.user.notfound", "User not found in database");
                        return;
                    }

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

                    User updatedUser = new User(
                            latestUser.getId(),
                            usernameValue,
                            emailValue,
                            passwordValue.isEmpty() ? latestUser.getPassword() : BCrypt.hashpw(passwordValue, BCrypt.gensalt(12)),
                            Role.fromString(roleValue),
                            latestUser.isVerified(),
                            latestUser.isLocked(),
                            latestUser.getVersion()
                    );

                    try {
                        userService.update(updatedUser, updateCallback -> {
                            userService.read(userDetail.getUserId(), finalUser -> {
                                if (finalUser != null) {
                                    userPictureService.read(finalUser.getId(), finalUserPicture -> {
                                        UserDetailDto updatedUserDetail = mapUserToUserDetailDto(finalUser, finalUserPicture);
                                        store.dispatch(UserState.class,
                                                new UserState(
                                                        updatedUserDetail,
                                                        state.isLoggedIn(),
                                                        state.getSelectedUser(),
                                                        state.getSelectedUserNote()
                                                ));
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
            }
        }
        update.setDisable(false);
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