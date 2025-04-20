package org.example.ibb_ecodation_javafx.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.ibb_ecodation_javafx.core.service.LanguageService;
import org.example.ibb_ecodation_javafx.core.validation.FieldValidator;
import org.example.ibb_ecodation_javafx.core.validation.ValidationRule;
import org.example.ibb_ecodation_javafx.model.User;
import org.example.ibb_ecodation_javafx.model.UserPicture;
import org.example.ibb_ecodation_javafx.model.dto.UserDetailDto;
import org.example.ibb_ecodation_javafx.model.enums.Role;
import org.example.ibb_ecodation_javafx.service.UserProfileService;
import org.example.ibb_ecodation_javafx.service.UserPictureService;
import org.example.ibb_ecodation_javafx.service.UserService;
import org.example.ibb_ecodation_javafx.statemanagement.Store;
import org.example.ibb_ecodation_javafx.statemanagement.state.DarkModeState;

import org.example.ibb_ecodation_javafx.statemanagement.state.UserState;
import org.example.ibb_ecodation_javafx.ui.ValidatableComponent;
import org.example.ibb_ecodation_javafx.ui.avatar.ShadcnAvatar;
import org.example.ibb_ecodation_javafx.ui.button.ShadcnButton;
import org.example.ibb_ecodation_javafx.ui.dragndrop.Upload;
import org.example.ibb_ecodation_javafx.ui.input.ShadcnInput;
import org.example.ibb_ecodation_javafx.utils.ImageUtil;
import org.springframework.stereotype.Controller;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.regex.Pattern;

@Controller
@RequiredArgsConstructor
public class ProfileController {

    private static final Logger logger = LogManager.getLogger(ProfileController.class);
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");
    private static final int AVATAR_SIZE = 40;

    @FXML private ShadcnAvatar avatar;
    @FXML private ShadcnInput emailInput;
    @FXML private ShadcnInput usernameInput;
    @FXML private ShadcnInput passwordInput;
    @FXML private ShadcnInput roleInput;
    @FXML private ShadcnButton updateButton;
    @FXML private Label pageTitle;
    @FXML private Upload imageUpload;

    private final UserService userService;
    private final UserPictureService userPictureService;
    private final UserProfileService userDetailService;
    private final LanguageService languageService;
    private final Store store = Store.getInstance();
    private final FieldValidator fieldValidator;

    public void initialize() {
        configurePageTitle();
        initializeUserProfile();
    }

    private void configurePageTitle() {
        pageTitle.setText(languageService.translate("label.profile"));
        pageTitle.setStyle(getPageTitleStyle());
    }

    private String getPageTitleStyle() {
        boolean isDarkMode = store.getCurrentState(DarkModeState.class).isEnabled();
        return String.format("-fx-font-size:24;-fx-text-fill:%s;", isDarkMode ? "white" : "black");
    }

    private void initializeUserProfile() {
        configureInputFields();
        setDefaultAvatar();
        avatar.setAvatarSize(AVATAR_SIZE);

        UserState userState = store.getCurrentState(UserState.class);
        UserDetailDto userDetail = userState.getUserDetail();
        if (userDetail == null) {
            logError("error.user.notfound", "User detail not found in state");
            return;
        }

        loadUserProfile(userDetail.getUserId());
    }

    private void configureInputFields() {
        emailInput.setHeader(languageService.translate("input.email.placeholder"));
        usernameInput.setHeader(languageService.translate("input.username.placeholder"));
        passwordInput.setHeader(languageService.translate("input.password.placeholder"));
        roleInput.setHeader(languageService.translate("input.role.placeholder"));
        updateButton.setText(languageService.translate("update"));
        imageUpload.setLabelText(languageService.translate("dragndrop"));
        setLoadingState();
    }

    private void setLoadingState() {
        emailInput.setText("Loading...");
        usernameInput.setText("Loading...");
        roleInput.setText("Loading...");
    }

    private void loadUserProfile(int userId) {
        userService.findById(userId).ifPresent(this::populateUserFields);
        userPictureService.findById(userId).ifPresent(this::displayUserPicture);
    }

    private void populateUserFields(User user) {
        usernameInput.setText(user.getUsername());
        emailInput.setText(user.getEmail());
        roleInput.setText(user.getRole().toString());
    }

    private void displayUserPicture(UserPicture picture) {
        try {
            Image image = ImageUtil.convertByteArrayToImage(picture.getImageData());
            avatar.setImage(image);
        } catch (IOException e) {
            logError("error.image.read", "Failed to load user picture", e);
        }
    }

    public void updateProfile(ActionEvent event) {
        updateButton.setDisable(true);
        clearInputErrors();

        if (!validateInputs()) {
            updateButton.setDisable(false);
            return;
        }

        UserState userState = store.getCurrentState(UserState.class);
        UserDetailDto userDetail = userState.getUserDetail();
        try {
            if (userDetail == null) {
                logError("error.user.notfound", "User detail not found");
                return;
            }

            updateUserProfile(userDetail);
        } catch (Exception e) {
            logError("error.profile.update", "Failed to update profile for userId: " + userDetail.getUserId(), e);
        } finally {
            updateButton.setDisable(false);
        }
    }

    private void updateUserProfile(UserDetailDto userDetail) throws IOException {
        UserPicture userPicture = updateProfilePicture(userDetail.getUserId());
        updateUserDetails(userDetail);
        userDetailService.updateProfile(userDetail, userPicture);
    }

    private UserPicture updateProfilePicture(int userId) throws IOException {
        String droppedImagePath = imageUpload.getDroppedImagePath();
        if (droppedImagePath == null) {
            return null; // No new picture uploaded
        }

        File uploadedFile = new File(droppedImagePath);
        if (!uploadedFile.exists()) {
            return null; // File does not exist
        }

        Optional<UserPicture> existingPicture = userPictureService.findById(userId);
        UserPicture picture = createProfilePicture(uploadedFile, userId, existingPicture);
        updateUserStateWithPicture(picture);
        avatar.setImage(ImageUtil.convertFileToImage(uploadedFile));
        return picture;
    }

    private UserPicture createProfilePicture(File uploadedFile, int userId, Optional<UserPicture> existingPicture) throws IOException {
        byte[] imageData = ImageUtil.convertImageToByteArray(uploadedFile);
        int version = existingPicture.map(UserPicture::getVersion).orElse(1);
        return new UserPicture(userId, imageData, version);
    }



    private void updateUserStateWithPicture(UserPicture picture) {
        UserState currentState = store.getCurrentState(UserState.class);
        UserDetailDto userDetail = currentState.getUserDetail();
        userDetail.setProfilePicture(picture.getImageData());

        store.dispatch(UserState.class, new UserState(
                userDetail,
                currentState.isLoggedIn(),
                currentState.getSelectedUser(),
                currentState.getSelectedUserNote()
        ));
    }

    private void updateUserDetails(UserDetailDto userDetail) {
        updateFieldIfChanged(userDetail, UserDetailDto::setUsername, userDetail.getUsername(), usernameInput.getText(), usernameInput);
        updateFieldIfChanged(userDetail, UserDetailDto::setEmail, userDetail.getEmail(), emailInput.getText(), emailInput);
        updateFieldIfChanged(userDetail, UserDetailDto::setPassword, userDetail.getPassword(), passwordInput.getText(), passwordInput);
        updateRoleIfChanged(userDetail, roleInput.getText());
    }

    private void updateFieldIfChanged(UserDetailDto userDetail, BiConsumer<UserDetailDto, String> setter, String currentValue, String newValue, ShadcnInput input) {
        if (!newValue.equals(currentValue)) {
            setter.accept(userDetail, newValue);
        }
    }

    private void updateRoleIfChanged(UserDetailDto userDetail, String newRole) {
        if (!newRole.equals(userDetail.getRole()) && isValidRole(newRole)) {
            userDetail.setRole(newRole);
        } else if (!isValidRole(newRole)) {
            roleInput.setError(languageService.translate("input.role.invalid"));
        }
    }

    private boolean validateInputs() {
        List<ValidationRule<String>> rules = new ArrayList<>();
        rules.add(createValidationRule(usernameInput, "input.username.empty", value -> !value.isEmpty()));
        rules.add(createValidationRule(emailInput, "input.email.empty", value -> !value.isEmpty()));
        rules.add(createValidationRule(emailInput, "input.email.invalid", value -> value.isEmpty() || EMAIL_PATTERN.matcher(value).matches()));
        rules.add(createValidationRule(roleInput, "input.role.empty", value -> !value.isEmpty()));
        rules.add(createValidationRule(roleInput, "input.role.invalid", this::isValidRole));
        rules.add(createValidationRule(passwordInput, "input.password.length", value -> value.isEmpty() || value.length() >= 8));

        FieldValidator validator = new FieldValidator();
        rules.forEach(validator::addRule);
        validator.onError(error -> error.getComponent().setError(error.getErrorDetail()));
        return validator.runValidatorEngine().isEmpty();
    }

    private ValidationRule<String> createValidationRule(ShadcnInput input, String errorKey, java.util.function.Predicate<String> validation) {
        return new ValidationRule<>() {
            @Override
            public String getValue() {
                return input.getText().trim();
            }

            @Override
            public boolean validate(String value) {
                return validation.test(value);
            }

            @Override
            public String getErrorMessage() {
                return languageService.translate(errorKey);
            }

            @Override
            public ValidatableComponent getComponent() {
                return input;
            }
        };
    }

    private boolean isValidRole(String role) {
        try {
            Role.fromString(role);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    private void clearInputErrors() {
        List.of(usernameInput, emailInput, roleInput, passwordInput).forEach(ShadcnInput::clearError);
    }

    private void setDefaultAvatar() {
        avatar.setImage(new Image(getClass().getResourceAsStream("/org/example/ibb_ecodation_javafx/assets/avatar.png")));
    }

    private void logError(String errorKey, String message) {
        logger.error("{}: {}", languageService.translate(errorKey), message);
    }

    private void logError(String errorKey, String message, Exception e) {
        logger.error("{}: {}", languageService.translate(errorKey), message, e);
    }
}