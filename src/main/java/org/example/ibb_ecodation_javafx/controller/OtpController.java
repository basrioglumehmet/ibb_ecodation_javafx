package org.example.ibb_ecodation_javafx.controller;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.example.ibb_ecodation_javafx.constants.ViewPathConstant;
import org.example.ibb_ecodation_javafx.core.context.SpringContext;
import org.example.ibb_ecodation_javafx.core.service.LanguageService;
import org.example.ibb_ecodation_javafx.model.dto.UserDetailDto;
import org.example.ibb_ecodation_javafx.service.MailService;
import org.example.ibb_ecodation_javafx.service.UserOtpCodeService;
import org.example.ibb_ecodation_javafx.service.UserService;
import org.example.ibb_ecodation_javafx.statemanagement.Store;
import org.example.ibb_ecodation_javafx.statemanagement.state.UserState;
import org.example.ibb_ecodation_javafx.ui.button.ShadcnButton;
import org.example.ibb_ecodation_javafx.ui.combobox.ShadcnLanguageComboBox;
import org.example.ibb_ecodation_javafx.ui.input.ShadcnOtpInput;
import org.example.ibb_ecodation_javafx.utils.OtpUtil;
import org.example.ibb_ecodation_javafx.utils.SceneUtil;
import io.reactivex.rxjava3.disposables.Disposable;

import java.io.IOException;
import java.util.ResourceBundle;

public class OtpController {

    @FXML
    private StackPane rootPane;
    @FXML
    private VBox mainVBox;
    @FXML
    private ImageView otpImage;
    @FXML
    private Label titleLabel;
    @FXML
    private Label descriptionLabel;
    @FXML
    private HBox otpHBox;
    @FXML
    private ShadcnOtpInput otpCodes;
    @FXML
    private VBox buttonVBox;
    @FXML
    private ShadcnButton continueButton;
    @FXML
    private ShadcnButton backButton;
    @FXML
    private ShadcnButton resendButton;
    @FXML
    private final UserService userService;

    private final LanguageService languageService = SpringContext.getContext().getBean(LanguageService.class);
    private final UserOtpCodeService userOtpCodeService;
    private final MailService mailService;
    private Disposable languageSubscription;
    private final Store store = Store.getInstance();

    public OtpController() {
        this.userOtpCodeService = SpringContext.getContext().getBean(UserOtpCodeService.class);
        this.mailService = SpringContext.getContext().getBean(MailService.class);
        this.userService = SpringContext.getContext().getBean(UserService.class);
    }

    @FXML
    public void initialize() {

        // Initialize with current language
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
            titleLabel.setText(bundle.getString("otp.header"));
            descriptionLabel.setText(bundle.getString("otp.description"));
            continueButton.setText(bundle.getString("otp.button.continue"));
            backButton.setText(bundle.getString("otp.button.back"));
            resendButton.setText(bundle.getString("otp.button.resend"));
        } catch (Exception e) {
            titleLabel.setText("One-Time Password Required");
            descriptionLabel.setText("Your account is not verified. Please enter the OTP code sent to you.");
            continueButton.setText("Continue");
            backButton.setText("Back");
            resendButton.setText("Resend OTP");
        }
    }

    @FXML
    public void handleVerification(ActionEvent actionEvent) {
        continueButton.setIsLoading(true);
        String languageCode = ShadcnLanguageComboBox.getCurrentLanguageCode();
        ResourceBundle bundle = languageService.loadAll(languageCode);

        new Thread(() -> {
            try {
                Thread.sleep(1000);

                //İçim rahat etmedi callback düzensiz kod yapısı oluşturdu clean code aykırı durum.
                //Technical debt maalesef oluşabilir çünkü zaman kısıtlı.
                Platform.runLater(() -> {
                    userOtpCodeService.verifyOtp(otpCodes.getCode(), cb -> {
                        if (cb != null) {
                                userService.read(cb.getUserId(),user -> {
                                    user.setVerified(true);
                                    userService.update(user,user1 -> {
                                        try {
                                            continueButton.setIsLoading(false);
                                            otpCodes.setError(false);
                                            var userDetail = new UserDetailDto();
                                            user.setId(user1.getId());
                                            user.setPassword(user1.getPassword());
                                            user.setRole(user1.getRole());
                                            user.setVersion(user.getVersion());
                                            user.setLocked(user.isLocked());
                                            user.setUsername(user.getUsername());
                                            user.setEmail(user.getEmail());
                                            user.setVerified(user.isVerified());
                                            var state = new UserState(
                                                    userDetail,
                                                    true,
                                                    null,
                                                    null
                                            );
                                            store.dispatch(UserState.class,state);
                                            SceneUtil.loadScene(
                                OtpController.class,
                                (Stage) rootPane.getScene().getWindow(),
                                String.format(ViewPathConstant.FORMAT, "admin-dashboard"),
                                "Admin Dashboard"
                        );
                                        } catch (IOException e) {
                                            System.out.println("Otp sayfasından dashboard'a geçerken sorun oluştu: " + e.getMessage());
                                            continueButton.setIsLoading(false);
                                        }
                                    });
                                });

                        } else {
                            continueButton.setIsLoading(false);
                            otpCodes.setError(true);
                        }
                    });
                });

                // Temporary hardcoded navigation
//                Platform.runLater(() -> {
//                    try {
//                        SceneUtil.loadScene(
//                                OtpController.class,
//                                (Stage) rootPane.getScene().getWindow(),
//                                String.format(ViewPathConstant.FORMAT, "admin-dashboard"),
//                                "Admin Dashboard"
//                        );
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                        System.out.println("Otp sayfasından dashboard'a geçerken sorun oluştu: " + e.getMessage());
//                    } finally {
//                        continueButton.setIsLoading(false);
//                    }
//                });

            } catch (InterruptedException e) {
                Platform.runLater(() -> {
                    continueButton.setIsLoading(false);
                    System.out.println("Verification interrupted: " + e.getMessage());
                });
            }
        }).start();
    }

    @FXML
    public void handleBack(ActionEvent actionEvent) {
        try {
            SceneUtil.loadSlidingContent(rootPane, "signin"); // Return to sign-in
        } catch (IOException e) {
            System.out.println("Geri giderken sorun oluştu: " + e.getMessage());
        }
    }

    @FXML
    public void handleResend(ActionEvent actionEvent) {
        resendButton.setIsLoading(true);
        String languageCode = ShadcnLanguageComboBox.getCurrentLanguageCode();
        ResourceBundle bundle = languageService.loadAll(languageCode);

        new Thread(() -> {
            try {
                Thread.sleep(1000); // Simulate resend delay

                // Assuming email is stored somewhere or passed; using placeholder for now
                String email = "user@example.com"; // Replace with actual email logic
                mailService.sendMail(email, OtpUtil.random(6));

                Platform.runLater(() -> {
                    resendButton.setIsLoading(false);
                    System.out.println(bundle.getString("otp.resend.success"));
                });

            } catch (InterruptedException e) {
                Platform.runLater(() -> {
                    resendButton.setIsLoading(false);
                    System.out.println(bundle.getString("otp.resend.failed") + ": " + e.getMessage());
                });
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
