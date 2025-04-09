package org.example.ibb_ecodation_javafx.controller;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import org.example.ibb_ecodation_javafx.core.context.SpringContext;
import org.example.ibb_ecodation_javafx.model.Authentication;
import org.example.ibb_ecodation_javafx.model.UserOtpCode;
import org.example.ibb_ecodation_javafx.model.enums.AuthenticationResult;
import org.example.ibb_ecodation_javafx.service.AuthenticationService;
import org.example.ibb_ecodation_javafx.ui.button.ShadcnButton;
import org.example.ibb_ecodation_javafx.ui.input.ShadcnInput;
import org.example.ibb_ecodation_javafx.utils.GuiAnimationUtil;
import org.example.ibb_ecodation_javafx.utils.SceneUtil;
import org.example.ibb_ecodation_javafx.utils.ValidationUtil;

import java.io.IOException;
import java.util.Map;


public class SignInController {

    @FXML
    private StackPane rootPane;
    @FXML
    private ShadcnInput email;

    @FXML
    private ShadcnInput password;

    @FXML
    private ShadcnButton login;




    private final AuthenticationService authenticationService;

    public SignInController() {
        this.authenticationService = SpringContext.getContext().getBean(AuthenticationService.class);
    }


    @FXML
    private void handleSignInProcess() throws IOException {
        // Önce önceki hataları temizle
        ValidationUtil.clearErrors(email, password);

        // Validator oluşturuluyor
        var validator = ValidationUtil.createValidator(Map.of(
                email, "Email boş bırakılamaz",
                password, "Şifre boş olamaz"
        ));

        // Validasyon çalıştırılır
        var errors = validator.runValidatorEngine();

        if (errors.isEmpty()) {
            handleSignIn();
        }
    }

    private void handleSignIn() {
        var authenticationModel = new Authentication(email.getText(), password.getText());
        authenticationService.signin(authenticationModel, callback -> {
            if(callback.equals(AuthenticationResult.OTP_REQUIRED)){
                try {
                    SceneUtil.loadSlidingContent(rootPane,"otp-verification");
                } catch (IOException e) {
                    System.out.println("Giriş sayfasında otp ekranı yüklenirken sorun oluştu:" +e.getMessage());
                }
            }
        });
    }

    @FXML
    public void handleSignUp() {
        try{
            SceneUtil.loadSlidingContent(rootPane,"signup");
        }
        catch (Exception ex){
            System.out.println("Kayıt sayfası yüklenirken sorun oluştu.");
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
}