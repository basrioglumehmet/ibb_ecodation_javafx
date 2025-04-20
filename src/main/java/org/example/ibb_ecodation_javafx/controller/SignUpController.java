package org.example.ibb_ecodation_javafx.controller;

import io.reactivex.rxjava3.disposables.Disposable;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import lombok.RequiredArgsConstructor;
import org.example.ibb_ecodation_javafx.core.service.LanguageService;
import org.example.ibb_ecodation_javafx.core.validation.FieldValidator;
import org.example.ibb_ecodation_javafx.core.validation.ValidationError;
import org.example.ibb_ecodation_javafx.core.validation.ValidationRule;
import org.example.ibb_ecodation_javafx.model.dto.RegisterDto;
import org.example.ibb_ecodation_javafx.model.enums.AuthenticationResult;
import org.example.ibb_ecodation_javafx.service.AuthenticationService;
import org.example.ibb_ecodation_javafx.service.MailService;
import org.example.ibb_ecodation_javafx.statemanagement.Store;
import org.example.ibb_ecodation_javafx.statemanagement.state.TranslatorState;
import org.example.ibb_ecodation_javafx.ui.ValidatableComponent;
import org.example.ibb_ecodation_javafx.ui.button.ShadcnButton;
import org.example.ibb_ecodation_javafx.ui.combobox.ShadcnLanguageComboBox;
import org.example.ibb_ecodation_javafx.ui.input.ShadcnInput;
import org.example.ibb_ecodation_javafx.utils.SceneUtil;
import org.springframework.stereotype.Controller;

import java.io.IOException;
import java.util.List;
import java.util.ResourceBundle;
import java.util.regex.Pattern;


@RequiredArgsConstructor
@Controller
public class SignUpController {

    @FXML private StackPane rootPane;
    @FXML private VBox mainVBox;
    @FXML private ShadcnLanguageComboBox languageComboBox;
    @FXML private Label signUpLabel;
    @FXML private ShadcnInput username;
    @FXML private ShadcnInput email;
    @FXML private ShadcnInput password;
    @FXML private ShadcnButton continueButton;
    @FXML private ShadcnButton backButton;
    @FXML private VBox termsVBox;
    @FXML private Label termsLabel;
    @FXML private Label policyLabel;

    private final LanguageService languageService;
    private final AuthenticationService authenticationService;
    private final MailService mailService;
    private final SceneUtil sceneUtil;
    private final Store store = Store.getInstance();
    private Disposable languageSubscription;

    // Validation patterns
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$"
    );
    private static final Pattern PASSWORD_PATTERN = Pattern.compile(
            "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}$"
    );

    @FXML
    public void initialize() {
        String languageCode = ShadcnLanguageComboBox.getCurrentLanguageCode();
        updateUIText(languageCode);
        subscribeToLanguageChanges();
    }

    private void subscribeToLanguageChanges() {
        languageSubscription = ShadcnLanguageComboBox.watchLanguageValue()
                .subscribe(pair -> Platform.runLater(() -> updateUIText(pair.getKey())));
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
            setDefaultUIText();
        }
    }

    private void setDefaultUIText() {
        signUpLabel.setText("Sign Up");
        username.setHeader("Username");
        email.setHeader("Email");
        password.setHeader("Password");
        continueButton.setText("Continue");
        backButton.setText("Back");
        termsLabel.setText("By clicking continue, you agree to our");
        policyLabel.setText("Terms of Service and Privacy Policy.");
    }

    @FXML
    private void handleSignUpProcess() {
        clearErrors();
        if (validateInputs()) {
            performSignUp();
        }
    }

    private void clearErrors() {
        username.clearError();
        email.clearError();
        password.clearError();
    }

    private boolean validateInputs() {
        FieldValidator validator = new FieldValidator();
        ResourceBundle bundle = languageService.loadAll(store.getCurrentState(TranslatorState.class).countryCode().getCode());

        validator.addRule(new ValidationRule<String>() {
            @Override
            public String getValue() {
                return username.getText().trim();
            }

            @Override
            public boolean validate(String value) {
                return value.length() >= 3;
            }

            @Override
            public String getErrorMessage() {
                return bundle.getString("auth.username.invalid");
            }

            @Override
            public ValidatableComponent getComponent() {
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
                return !value.isEmpty() && EMAIL_PATTERN.matcher(value).matches();
            }

            @Override
            public String getErrorMessage() {
                return bundle.getString("auth.email.invalid");
            }

            @Override
            public ValidatableComponent getComponent() {
                return email;
            }
        });

        validator.addRule(new ValidationRule<String>() {
            @Override
            public String getValue() {
                return password.getText().trim();
            }

            @Override
            public boolean validate(String value) {
                return !value.isEmpty() && PASSWORD_PATTERN.matcher(value).matches();
            }

            @Override
            public String getErrorMessage() {
                return bundle.getString("auth.password.invalid");
            }

            @Override
            public ValidatableComponent getComponent() {
                return password;
            }
        });

        validator.onError(error -> error.getComponent().setError(error.getErrorDetail()));
        List<ValidationError> errors = validator.runValidatorEngine();
        return errors.isEmpty();
    }

    private void performSignUp() {
        continueButton.setIsLoading(true);
        String emailText = email.getText().trim();

        Task<AuthenticationResult> signUpTask = new Task<>() {
            @Override
            protected AuthenticationResult call() {
                RegisterDto registerDto = new RegisterDto(
                        username.getText().trim(),
                        emailText,
                        password.getText().trim()
                );
                return authenticationService.signup(registerDto);
            }
        };

        signUpTask.setOnSucceeded(event -> {
            Platform.runLater(() -> {
                continueButton.setIsLoading(false);
                AuthenticationResult result = signUpTask.getValue();
                handleSignUpResult(result, emailText);
            });
        });

        signUpTask.setOnFailed(event -> {
            Platform.runLater(() -> {
                continueButton.setIsLoading(false);
                ResourceBundle bundle = languageService.loadAll(store.getCurrentState(TranslatorState.class).countryCode().getCode());
                // Log the actual exception
                Throwable exception = signUpTask.getException();
                exception.printStackTrace(); // Use proper logging in production
                System.err.println("SignUp failed: " + (exception != null ? exception.getMessage() : "Unknown error"));
                email.setError(bundle.getString("auth.error.server"));
            });
        });

        new Thread(signUpTask).start();
    }
    private void handleSignUpResult(AuthenticationResult result, String emailText) {
        ResourceBundle bundle = languageService.loadAll(store.getCurrentState(TranslatorState.class).countryCode().getCode());

        switch (result) {
            case CREATED:
                try {
                    // OTP is already sent by AuthenticationServiceImpl, navigate to verification
                    sceneUtil.loadSlidingContent(rootPane, "otp-verification");
                } catch (IOException e) {
                    email.setError(bundle.getString("auth.error.navigation"));
                }
                break;
            case EXISTS:
                email.setError(bundle.getString("auth.email.exists"));
                break;
            default:
                email.setError(bundle.getString("auth.error.generic"));
                break;
        }
    }

    @FXML
    private void handleBack(ActionEvent actionEvent) {
        try {
            sceneUtil.loadSlidingContent(rootPane, "signin");
        } catch (IOException e) {
            System.err.println("Failed to load sign-in page: " + e.getMessage());
        }
    }

    public void shutdown() {
        if (languageSubscription != null && !languageSubscription.isDisposed()) {
            languageSubscription.dispose();
        }
    }
}