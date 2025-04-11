package org.example.ibb_ecodation_javafx.controller;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Pair;
import org.example.ibb_ecodation_javafx.core.context.SpringContext;
import org.example.ibb_ecodation_javafx.core.service.LanguageService;
import org.example.ibb_ecodation_javafx.model.dto.RegisterDto;
import org.example.ibb_ecodation_javafx.model.enums.AuthenticationResult;
import org.example.ibb_ecodation_javafx.service.AuthenticationService;
import org.example.ibb_ecodation_javafx.service.MailService;
import org.example.ibb_ecodation_javafx.ui.button.ShadcnButton;
import org.example.ibb_ecodation_javafx.ui.combobox.ShadcnLanguageComboBox;
import org.example.ibb_ecodation_javafx.ui.input.ShadcnInput;
import org.example.ibb_ecodation_javafx.utils.OtpUtil;
import org.example.ibb_ecodation_javafx.utils.SceneUtil;
import org.example.ibb_ecodation_javafx.utils.ValidationUtil;
import io.reactivex.rxjava3.disposables.Disposable;

import java.io.IOException;
import java.util.Map;
import java.util.ResourceBundle;

public class SignUpController {

    @FXML
    private StackPane rootPane;
    @FXML
    private VBox mainVBox;
    @FXML
    private ShadcnLanguageComboBox languageComboBox;
    @FXML
    private Label signUpLabel;
    @FXML
    private ShadcnInput username;
    @FXML
    private ShadcnInput email;
    @FXML
    private ShadcnInput password;
    @FXML
    private ShadcnButton continueButton;
    @FXML
    private ShadcnButton backButton;
    @FXML
    private VBox termsVBox;
    @FXML
    private Label termsLabel;
    @FXML
    private Label policyLabel;

    private final LanguageService languageService = SpringContext.getContext().getBean(LanguageService.class);
    private final AuthenticationService authenticationService;
    private final MailService mailService;
    private Disposable languageSubscription;

    public SignUpController() {
        this.authenticationService = SpringContext.getContext().getBean(AuthenticationService.class);
        this.mailService = SpringContext.getContext().getBean(MailService.class);
    }

    @FXML
    public void initialize() {
        // Use the shared language code from ShadcnLanguageComboBox
        String languageCode = ShadcnLanguageComboBox.getCurrentLanguageCode();
        updateUIText(languageCode);

        // Subscribe to language changes
        languageSubscription = ShadcnLanguageComboBox.watchLanguageValue().subscribe(pair -> {
            String newLanguageCode = pair.getKey();
            Platform.runLater(() -> updateUIText(newLanguageCode));
        });
    }

    private void updateUIText(String languageCode) {
        ResourceBundle bundle = languageService.loadAll(languageCode);
        try {
            signUpLabel.setText(bundle.getString("register.header"));
            username.setHeader(bundle.getString("auth.username"));
            email.setHeader(bundle.getString("auth.email"));
            password.setHeader(bundle.getString("auth.password"));
            continueButton.setText(bundle.getString("register.button"));
            backButton.setText(bundle.getString("register.back"));
            termsLabel.setText(bundle.getString("gdpr.termsLabel"));
            policyLabel.setText(bundle.getString("gdpr.policyLabel"));
        } catch (Exception e) {
            // Fallback to English defaults if keys are missing
            signUpLabel.setText("Sign Up");
            username.setHeader("Username");
            email.setHeader("Email");
            password.setHeader("Password");
            continueButton.setText("Continue");
            backButton.setText("Back");
            termsLabel.setText("By clicking continue, you agree to our");
            policyLabel.setText("Terms of Service and Privacy Policy.");
        }
    }

    @FXML
    private void handleSignUpProcess() throws IOException {
        ValidationUtil.clearErrors(email, password, username);
        String languageCode = ShadcnLanguageComboBox.getCurrentLanguageCode();
        ResourceBundle bundle = languageService.loadAll(languageCode);
        var validator = ValidationUtil.createValidator(Map.of(
                email, bundle.getString("auth.email.empty"),
                password, bundle.getString("auth.password.empty"),
                username, bundle.getString("auth.username.empty")
        ));
        var errors = validator.runValidatorEngine();

        if (errors.isEmpty()) {
            signUp();
            mailService.sendMail(email.getText(), OtpUtil.random(6));
        }
    }

    @FXML
    public void handleBack(ActionEvent actionEvent) {
        try {
            SceneUtil.loadSlidingContent(rootPane, "signin");
        } catch (IOException e) {
            System.out.println("Geri giderken sorun oluştu: " + e.getMessage());
        }
    }

    private void signUp() {
        continueButton.setIsLoading(true);
        new Thread(() -> {
            try {
                Thread.sleep(500);
                Platform.runLater(() -> {
                    continueButton.setIsLoading(false);
                    String languageCode = ShadcnLanguageComboBox.getCurrentLanguageCode();
                    ResourceBundle bundle = languageService.loadAll(languageCode);
                    authenticationService.signup(new RegisterDto(username.getText(), email.getText(), password.getText()), cb -> {
                        if (cb == AuthenticationResult.CREATED) {
                            email.clearError();
                            try {
                                SceneUtil.loadSlidingContent(rootPane, "otp-verification");
                            } catch (IOException e) {
                                System.out.println("Otp ekranı yüklenirken sorun oluştu: " + e.getMessage());
                            }
                        } else if (cb == AuthenticationResult.EXISTS) {
                            email.setError(bundle.getString("auth.email.exists"));
                        }
                    });
                });
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    // Clean up subscription on controller destruction
    public void shutdown() {
        if (languageSubscription != null && !languageSubscription.isDisposed()) {
            languageSubscription.dispose();
        }
    }
}