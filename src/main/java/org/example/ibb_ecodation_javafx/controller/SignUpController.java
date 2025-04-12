package org.example.ibb_ecodation_javafx.controller;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Pair;
import org.example.ibb_ecodation_javafx.core.context.SpringContext;
import org.example.ibb_ecodation_javafx.core.service.LanguageService;
import org.example.ibb_ecodation_javafx.core.validation.FieldValidator;
import org.example.ibb_ecodation_javafx.core.validation.ValidationError;
import org.example.ibb_ecodation_javafx.core.validation.ValidationRule;
import org.example.ibb_ecodation_javafx.model.dto.RegisterDto;
import org.example.ibb_ecodation_javafx.model.enums.AuthenticationResult;
import org.example.ibb_ecodation_javafx.service.AuthenticationService;
import org.example.ibb_ecodation_javafx.service.MailService;
import org.example.ibb_ecodation_javafx.ui.button.ShadcnButton;
import org.example.ibb_ecodation_javafx.ui.combobox.ShadcnLanguageComboBox;
import org.example.ibb_ecodation_javafx.ui.input.ShadcnInput;
import org.example.ibb_ecodation_javafx.utils.OtpUtil;
import org.example.ibb_ecodation_javafx.utils.SceneUtil;
import io.reactivex.rxjava3.disposables.Disposable;

import java.io.IOException;
import java.util.List;
import java.util.ResourceBundle;
import java.util.regex.Pattern;

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

    // Email validation regex pattern
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$"
    );

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
        // Clear previous errors
        username.clearError();
        email.clearError();
        password.clearError();

        String languageCode = ShadcnLanguageComboBox.getCurrentLanguageCode();
        ResourceBundle bundle = languageService.loadAll(languageCode);

        // Create validator and add rules
        FieldValidator validator = new FieldValidator();

        // Username not empty rule
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
                return bundle.getString("auth.username.empty");
            }

            @Override
            public ShadcnInput getInput() {
                return username;
            }
        });

        // Email not empty rule
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
                return bundle.getString("auth.email.empty");
            }

            @Override
            public ShadcnInput getInput() {
                return email;
            }
        });

        // Email format rule
        validator.addRule(new ValidationRule<String>() {
            @Override
            public String getValue() {
                return email.getText().trim();
            }

            @Override
            public boolean validate(String value) {
                return EMAIL_PATTERN.matcher(value).matches();
            }

            @Override
            public String getErrorMessage() {
                return bundle.getString("auth.email.invalid");
            }

            @Override
            public ShadcnInput getInput() {
                return email;
            }
        });

        // Password not empty rule
        validator.addRule(new ValidationRule<String>() {
            @Override
            public String getValue() {
                return password.getText().trim();
            }

            @Override
            public boolean validate(String value) {
                return !value.isEmpty();
            }

            @Override
            public String getErrorMessage() {
                return bundle.getString("auth.password.empty");
            }

            @Override
            public ShadcnInput getInput() {
                return password;
            }
        });

        // Set error callback to display errors on UI
        validator.onError(error -> error.getInput().setError(error.getErrorDetail()));

        // Run validation
        List<ValidationError> errors = validator.runValidatorEngine();

        if (errors.isEmpty()) {
            signUp();
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