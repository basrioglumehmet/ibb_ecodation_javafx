package org.example.ibb_ecodation_javafx.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import org.example.ibb_ecodation_javafx.core.context.SpringContext;
import org.example.ibb_ecodation_javafx.core.service.LanguageService;
import org.example.ibb_ecodation_javafx.model.Authentication;
import org.example.ibb_ecodation_javafx.model.dto.UserDetailDto;
import org.example.ibb_ecodation_javafx.model.enums.AuthenticationResult;
import org.example.ibb_ecodation_javafx.service.AuthenticationService;
import org.example.ibb_ecodation_javafx.statemanagement.Store;
import org.example.ibb_ecodation_javafx.statemanagement.state.UserState;
import org.example.ibb_ecodation_javafx.ui.button.ShadcnButton;
import org.example.ibb_ecodation_javafx.ui.combobox.ShadcnLanguageComboBox;
import org.example.ibb_ecodation_javafx.ui.input.ShadcnInput;
import org.example.ibb_ecodation_javafx.utils.SceneUtil;
import org.example.ibb_ecodation_javafx.utils.ValidationUtil;
import io.reactivex.rxjava3.disposables.Disposable;

import java.io.IOException;
import java.util.Map;
import java.util.ResourceBundle;

public class SignInController {

    @FXML
    private StackPane rootPane;
    @FXML
    private ShadcnInput email;
    @FXML
    private ShadcnInput password;
    @FXML
    private ShadcnButton login;
    @FXML
    private Label signInLabel;
    @FXML
    private ShadcnButton signUpButton;
    @FXML
    private Label orLabel;
    @FXML
    private Label termsLabel;
    @FXML
    private Label policyLabel;
    @FXML
    private ShadcnLanguageComboBox languageComboBox;

    @FXML
    private final Store store = Store.getInstance();

    private final LanguageService languageService = SpringContext.getContext().getBean(LanguageService.class);
    private final AuthenticationService authenticationService;
    private Disposable languageSubscription;

    public SignInController() {
        this.authenticationService = SpringContext.getContext().getBean(AuthenticationService.class);
    }

    @FXML
    public void initialize() {
        String languageCode = ShadcnLanguageComboBox.getCurrentLanguageCode();
        updateUIText(languageCode);

        languageSubscription = ShadcnLanguageComboBox.watchLanguageValue().subscribe(pair -> {
            String newLanguageCode = pair.getKey();
            Platform.runLater(() -> updateUIText(newLanguageCode));
        });
    }

    private void updateUIText(String languageCode) {
        ResourceBundle bundle = languageService.loadAll(languageCode);
        try {
            signInLabel.setText(bundle.getString("login.header"));
            email.setHeader(bundle.getString("auth.email"));
            password.setHeader(bundle.getString("auth.password"));
            login.setText(bundle.getString("login.button"));
            orLabel.setText(bundle.getString("auth.continue"));
            signUpButton.setText(bundle.getString("register.header"));
            termsLabel.setText(bundle.getString("gdpr.termsLabel"));
            policyLabel.setText(bundle.getString("gdpr.policyLabel"));
        } catch (Exception e) {
            signInLabel.setText("Sign In");
            email.setHeader("Email");
            password.setHeader("Password");
            login.setText("Continue");
            orLabel.setText("OR CONTINUE WITH");
            signUpButton.setText("Sign Up");
            termsLabel.setText("By clicking continue, you agree to our");
            policyLabel.setText("Terms of Service and Privacy Policy.");
        }
    }

    @FXML
    private void handleSignInProcess() throws IOException {
        ValidationUtil.clearErrors(email, password);
        String languageCode = ShadcnLanguageComboBox.getCurrentLanguageCode();
        ResourceBundle bundle = languageService.loadAll(languageCode);
        var validator = ValidationUtil.createValidator(Map.of(
                email, bundle.getString("auth.email.empty"),
                password, bundle.getString("auth.password.empty")
        ));
        var errors = validator.runValidatorEngine();
        if (errors.isEmpty()) {
            handleSignIn();
        }
    }

    private void handleSignIn() {
        var authenticationModel = new Authentication(email.getText(), password.getText());
        authenticationService.signin(authenticationModel, callback -> {
            if (callback.getAuthenticationResult().equals(AuthenticationResult.OTP_REQUIRED)) {
                try {
                    SceneUtil.loadSlidingContent(rootPane,
                            "otp-verification");
                } catch (IOException e) {
                    System.out.println("Error loading OTP screen: " + e.getMessage());
                }
            }
            else if(callback.getAuthenticationResult().equals(AuthenticationResult.PASSWORD_MIS_MATCH)){
                password.setError(languageService.translate("pwd.mismatch"));
            }
            else if(callback.getAuthenticationResult().equals(AuthenticationResult.ERROR)){
                email.setError(languageService.translate("notfound"));
            }
            else if(callback.getAuthenticationResult().equals(AuthenticationResult.OK)){
                try {
                    var userDetail = new UserDetailDto(callback.getUser().getId(),
                            callback.getUserPicture().getImageData(),
                            callback.getUser().getUsername(),
                            callback.getUser().getEmail(),
                            callback.getUser().getPassword(),
                            callback.getUser().getRole().toString(),
                            callback.getUser().isVerified(),
                            callback.getUser().isLocked(),callback.getUser().getVersion());
                    store.dispatch(UserState.class,new UserState(userDetail,true,null,null));
                    SceneUtil.loadScene(SignInController.class,(Stage) rootPane.getScene().getWindow(),
                            "/org/example/ibb_ecodation_javafx/views/admin-dashboard-view.fxml",
                            "test");
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    @FXML
    public void handleSignUp() {
        try {
            SceneUtil.loadSlidingContent(rootPane, "signup");
        } catch (Exception ex) {
            System.out.println("Error loading signup page: " + ex.getMessage());
        }
    }

    private void showLoadingDialogAndSignIn() {
        login.setIsLoading(true);
        new Thread(() -> {
            try {
                Thread.sleep(3000);
                Platform.runLater(() -> {
                    login.setIsLoading(false);
                    handleSignIn();
                });
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    public void shutdown() {
        if (languageSubscription != null && !languageSubscription.isDisposed()) {
            languageSubscription.dispose();
        }
    }
}