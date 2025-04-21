package org.example.ibb_ecodation_javafx.controller;

import io.reactivex.rxjava3.disposables.Disposable;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import lombok.RequiredArgsConstructor;
import org.example.ibb_ecodation_javafx.core.service.LanguageService;
import org.example.ibb_ecodation_javafx.model.Authentication;
import org.example.ibb_ecodation_javafx.model.User;
import org.example.ibb_ecodation_javafx.model.UserPicture;
import org.example.ibb_ecodation_javafx.model.dto.SignInDto;
import org.example.ibb_ecodation_javafx.model.dto.UserDetailDto;
import org.example.ibb_ecodation_javafx.service.AuthenticationService;
import org.example.ibb_ecodation_javafx.statemanagement.Store;
import org.example.ibb_ecodation_javafx.statemanagement.enums.CountryCode;
import org.example.ibb_ecodation_javafx.statemanagement.state.TranslatorState;
import org.example.ibb_ecodation_javafx.statemanagement.state.UserState;
import org.example.ibb_ecodation_javafx.ui.button.ShadcnButton;
import org.example.ibb_ecodation_javafx.ui.combobox.ShadcnLanguageComboBox;
import org.example.ibb_ecodation_javafx.ui.input.ShadcnInput;
import org.example.ibb_ecodation_javafx.utils.SceneUtil;
import org.example.ibb_ecodation_javafx.utils.ValidationUtil;
import org.springframework.stereotype.Controller;

import java.io.IOException;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class SignInController {

    @FXML private StackPane rootPane;
    @FXML private ShadcnInput email;
    @FXML private ShadcnInput password;
    @FXML private ShadcnButton login;
    @FXML private Label signInLabel;
    @FXML private ShadcnButton signUpButton;
    @FXML private Label orLabel;
    @FXML private Label termsLabel;
    @FXML private Label policyLabel;
    @FXML private ShadcnLanguageComboBox languageComboBox;

    private final LanguageService languageService;
    private final AuthenticationService authenticationService;
    private final Store store = Store.getInstance();
    private Disposable languageSubscription;
    private final SceneUtil sceneUtil;

    public void initialize() {
        String languageCode = ShadcnLanguageComboBox.getCurrentLanguageCode();
        updateUI(languageCode);
        subscribeToLanguageChanges();
    }

    private void subscribeToLanguageChanges() {
        languageSubscription = ShadcnLanguageComboBox.watchLanguageValue()
                .subscribe(pair -> {
                    String newLanguageCode = pair.getKey();
                    Platform.runLater(() -> updateUI(newLanguageCode));
                });
    }

    private void updateUI(String languageCode) {
        try {
            languageService.loadAll(languageCode);
            store.dispatch(TranslatorState.class,new TranslatorState(CountryCode.fromCode(languageCode)));
            updateUIText();
        } catch (Exception e) {
            setDefaultUIText();
        }
    }

    private void updateUIText() {
        signInLabel.setText(languageService.translate("login.header"));
        email.setHeader(languageService.translate("auth.email"));
        password.setHeader(languageService.translate("auth.password"));
        login.setText(languageService.translate("login.button"));
        orLabel.setText(languageService.translate("auth.continue"));
        signUpButton.setText(languageService.translate("register.header"));
        termsLabel.setText(languageService.translate("gdpr.termsLabel"));
        policyLabel.setText(languageService.translate("gdpr.policyLabel"));
    }

    private void setDefaultUIText() {
        signInLabel.setText("Sign In");
        email.setHeader("Email");
        password.setHeader("Password");
        login.setText("Continue");
        orLabel.setText("OR CONTINUE WITH");
        signUpButton.setText("Sign Up");
        termsLabel.setText("By clicking continue, you agree to our");
        policyLabel.setText("Terms of Service and Privacy Policy.");
    }

    @FXML
    private void handleSignInProcess() {
        ValidationUtil.clearErrors(email, password);
        if (validateInputs()) {
            performSignIn();
        }
    }

    private boolean validateInputs() {
        var validator = ValidationUtil.createValidator(Map.of(
                email, languageService.translate("auth.email.empty"),
                password, languageService.translate("auth.password.empty")
        ));
        return validator.runValidatorEngine().isEmpty();
    }

    private void performSignIn() {
        Authentication authentication = Authentication.builder()
                .email(email.getText())
                .password(password.getText())
                .build();

        SignInDto signInDto = authenticationService.signin(authentication);
        handleSignInResult(signInDto);
    }

    private void handleSignInResult(SignInDto signInDto) {
        switch (signInDto.getAuthenticationResult()) {
            case ERROR -> email.setError(languageService.translate("notfound"));
            case PASSWORD_MIS_MATCH -> password.setError(languageService.translate("pwd.mismatch"));
            case OK -> handleSuccessfulSignIn(signInDto.getUser(), signInDto.getUserPicture());
            case OTP_REQUIRED -> loadOtpVerification();
        }
    }

    private void handleSuccessfulSignIn(User user, UserPicture userPicture) {
        try {
            UserDetailDto userDetail = createUserDetailDto(user, userPicture);
            store.dispatch(UserState.class, new UserState(userDetail, true, null, null));
            sceneUtil.loadScene(
                    AdminDashboardController.class,
                    (Stage) email.getScene().getWindow(),
                    "/org/example/ibb_ecodation_javafx/views/admin-dashboard-view.fxml",
                    "Dashboard - App"
            );
        } catch (Exception e) {
            throw new RuntimeException("Failed to handle successful sign-in", e);
        }
    }

    private UserDetailDto createUserDetailDto(User user, UserPicture userPicture) {
        return new UserDetailDto(
                user.getId(),
                userPicture.getImageData(),
                user.getUsername(),
                user.getEmail(),
                user.getPassword(),
                user.getRole().toString(),
                user.isVerified(),
                user.isLocked(),
                user.getVersion()
        );
    }

    private void loadOtpVerification() {
        try {
            sceneUtil.loadSlidingContent(rootPane, "otp-verification");
        } catch (IOException e) {
            throw new RuntimeException("Failed to load OTP verification page", e);
        }
    }

    @FXML
    private void handleSignUp() {
        try {
            sceneUtil.loadSlidingContent(rootPane, "signup");
        } catch (IOException e) {
            throw new RuntimeException("Failed to load signup page", e);
        }
    }

    // Ensure to clean up resources if needed
    public void cleanup() {
        if (languageSubscription != null && !languageSubscription.isDisposed()) {
            languageSubscription.dispose();
        }
    }
}