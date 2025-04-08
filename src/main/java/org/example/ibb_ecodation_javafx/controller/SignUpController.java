package org.example.ibb_ecodation_javafx.controller;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.layout.StackPane;
import org.example.ibb_ecodation_javafx.constants.FieldRuleConstant;
import org.example.ibb_ecodation_javafx.core.context.SpringContext;
import org.example.ibb_ecodation_javafx.core.validation.FieldValidator;
import org.example.ibb_ecodation_javafx.core.validation.ValidationRule;
import org.example.ibb_ecodation_javafx.model.Authentication;
import org.example.ibb_ecodation_javafx.service.AuthenticationService;
import org.example.ibb_ecodation_javafx.service.MailService;
import org.example.ibb_ecodation_javafx.ui.button.ShadcnButton;
import org.example.ibb_ecodation_javafx.ui.input.ShadcnInput;
import org.example.ibb_ecodation_javafx.utils.SceneUtil;
import org.example.ibb_ecodation_javafx.utils.ValidationUtil;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class SignUpController {

    @FXML
    private StackPane rootPane; // Add this to reference the parent StackPane from FXML
    @FXML
    private ShadcnInput email;
    @FXML
    private ShadcnInput username;

    @FXML
    private ShadcnInput password;

    @FXML
    private ShadcnButton login;

    private final MailService mailService;



    private final AuthenticationService authenticationService;

    public SignUpController() {
        this.authenticationService = SpringContext.getContext().getBean(AuthenticationService.class);
        this.mailService = SpringContext.getContext().getBean(MailService.class);
    }


    @FXML
    private void handleSignUpProcess() throws IOException {
        boolean hasError = false;
        // Önce önceki hataları temizle
        ValidationUtil.clearErrors(email, password, username);

        // Validator oluşturuluyor
        var validator = ValidationUtil.createValidator(Map.of(
                email, "Email boş bırakılamaz",
                password, "Şifre boş olamaz",
                username, "Kullanıcı adı gerekli"
        ));

        // Validasyon çalıştırılır
        var errors = validator.runValidatorEngine();

        if (errors.isEmpty()) {
            showLoadingDialogAndSignIn();
            mailService.sendMail("basrioglumehmet@gmail.com","Deneme","Deneme");
        }
        // Hatalar yoksa giriş yap
//        if (!hasError) {
//            showLoadingDialogAndSignIn();
//        }
    }

    @FXML
    public void handleBack(ActionEvent actionEvent){
        try{
            SceneUtil.loadSlidingContent(rootPane,"signin");
        } catch (IOException e) {
            System.out.println("Geri giderken sorun oluştu.");
        }
    }

    private void handleSignIn() {
        var authenticationModel = new Authentication(email.getText(), password.getText());
        authenticationService.signin(authenticationModel, callback -> {
            if (callback != null) {
                System.out.println("Ok");
            } else {
                email.setError("Email adresinizi kontrol edin.");
                password.setError("Şifrenizi kontrol edin.");
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