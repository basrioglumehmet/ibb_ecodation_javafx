package org.example.ibb_ecodation_javafx.controller;

import io.reactivex.rxjava3.disposables.Disposable;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import lombok.RequiredArgsConstructor;
import org.example.ibb_ecodation_javafx.constants.ViewPathConstant;
import org.example.ibb_ecodation_javafx.core.service.LanguageService;
import org.example.ibb_ecodation_javafx.repository.UserOtpCodeRepository;
import org.example.ibb_ecodation_javafx.service.UserOtpCodeService;
import org.example.ibb_ecodation_javafx.service.UserOtpCodeServiceImpl;
import org.example.ibb_ecodation_javafx.statemanagement.Store;
import org.example.ibb_ecodation_javafx.statemanagement.state.TranslatorState;
import org.example.ibb_ecodation_javafx.ui.button.ShadcnButton;
import org.example.ibb_ecodation_javafx.ui.combobox.ShadcnLanguageComboBox;
import org.example.ibb_ecodation_javafx.ui.input.ShadcnOtpInput;
import org.example.ibb_ecodation_javafx.utils.SceneUtil;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
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

    private Disposable languageSubscription;


    private final LanguageService languageService;
    private final UserOtpCodeService userOtpCodeService;
    private final SceneUtil sceneUtil;
    private final Store store = Store.getInstance();



    @FXML
    public void initialize() {

        languageService.loadAll(store.getCurrentState(TranslatorState.class).countryCode().getCode());
        updateUIText();
        languageSubscription = ShadcnLanguageComboBox.watchLanguageValue().subscribe(pair -> {
            String newLanguageCode = pair.getKey();
            Platform.runLater(this::updateUIText);
        });
    }

    private void updateUIText() {
        try {
            titleLabel.setText(languageService.translate("otp.header"));
            descriptionLabel.setText(languageService.translate("otp.description"));
            continueButton.setText(languageService.translate("otp.button.continue"));
            backButton.setText(languageService.translate("otp.button.back"));
            resendButton.setText(languageService.translate("otp.button.resend"));
        } catch (Exception e) {
            titleLabel.setText("One-Time Password Required");
            descriptionLabel.setText("Your account is not verified. Please enter the OTP code sent to you.");
            continueButton.setText("Continue");
            backButton.setText("Back");
            resendButton.setText("Resend OTP");
        }
    }


    @FXML
    private void handleVerification(){
        var result = userOtpCodeService.verify(otpCodes.getCode());
        if(result.isSuccess()){
            System.out.println(result.getOwnerId());
            //If success the result, navigate to the dashboard
            try{
                sceneUtil.loadScene(
                        AdminDashboardController.class,
                        (Stage) rootPane.getScene().getWindow(),
                        String.format(ViewPathConstant.FORMAT, "admin-dashboard"),
                        "Admin Dashboard"
                );
            } catch (Exception e) {
                throw new RuntimeException(e.getMessage());
            }
        }
        else{
            otpCodes.setError(true);
        }
    }
}
