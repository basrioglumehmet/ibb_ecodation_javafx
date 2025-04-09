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
import org.example.ibb_ecodation_javafx.model.User;
import org.example.ibb_ecodation_javafx.model.UserOtpCode;
import org.example.ibb_ecodation_javafx.model.dto.RegisterDto;
import org.example.ibb_ecodation_javafx.model.dto.UserDto;
import org.example.ibb_ecodation_javafx.model.enums.AuthenticationResult;
import org.example.ibb_ecodation_javafx.service.AuthenticationService;
import org.example.ibb_ecodation_javafx.service.MailService;
import org.example.ibb_ecodation_javafx.service.UserOtpCodeService;
import org.example.ibb_ecodation_javafx.service.UserService;
import org.example.ibb_ecodation_javafx.ui.button.ShadcnButton;
import org.example.ibb_ecodation_javafx.ui.input.ShadcnInput;
import org.example.ibb_ecodation_javafx.utils.OtpUtil;
import org.example.ibb_ecodation_javafx.utils.SceneUtil;
import org.example.ibb_ecodation_javafx.utils.ValidationUtil;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

public class SignUpController {

    @FXML
    private StackPane rootPane;
    @FXML
    private ShadcnInput email;
    @FXML
    private ShadcnInput username;

    @FXML
    private ShadcnInput password;

    @FXML
    private ShadcnButton continueButton;

    private final MailService mailService;
    private final UserService userService;


    private final AuthenticationService authenticationService;

    public SignUpController() {
        this.authenticationService = SpringContext.getContext().getBean(AuthenticationService.class);
        this.userService = SpringContext.getContext().getBean(UserService.class);
        this.mailService = SpringContext.getContext().getBean(MailService.class);
    }


    @FXML
    private void handleSignUpProcess() throws IOException {
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
            signUp();
            var otp = new UserOtpCode();
           // mailService.sendMail("basrioglumehmet@gmail.com", OtpUtil.random(6));
        }
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
                try {
                    SceneUtil.loadSlidingContent(rootPane,"otp-verification");
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            } else {
                email.setError("Email adresinizi kontrol edin.");
                password.setError("Şifrenizi kontrol edin.");
            }
        });
    }


    private void signUp() {
        continueButton.setIsLoading(true);
        new Thread(() -> {
            try {
                Thread.sleep(3000);
                Platform.runLater(() -> {
                    continueButton.setIsLoading(false);
                    authenticationService.signup(new RegisterDto(username.getText(),email.getText(),password.getText()), cb ->{
                        if(cb == AuthenticationResult.CREATED){
                            email.clearError();
                            //StateManager'da veriyi setle.
                            try {
                                SceneUtil.loadSlidingContent(rootPane,"otp-verification");
                            } catch (IOException e) {
                                System.out.println("Otp ekranı yüklenirken sorun oluştu:"+e.getMessage());
                            }
                        }
                        if(cb == AuthenticationResult.EXISTS){
                            email.setError("E-posta kullanılmaktadır.");
                        }
                    });
                });
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }
}