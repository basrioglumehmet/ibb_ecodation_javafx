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
import org.example.ibb_ecodation_javafx.service.AuthenticationService;
import org.example.ibb_ecodation_javafx.ui.button.ShadcnButton;
import org.example.ibb_ecodation_javafx.ui.input.ShadcnInput;
import org.example.ibb_ecodation_javafx.utils.GuiAnimationUtil;
import org.example.ibb_ecodation_javafx.utils.SceneUtil;

import java.io.IOException;

import static org.example.ibb_ecodation_javafx.utils.GuiAnimationUtil.runSceneSlideAnimation;

public class SignInController {

    @FXML
    private StackPane rootPane; // Add this to reference the parent StackPane from FXML
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
        boolean hasError = false;

        String emailText = email.getText().trim();
        String passwordText = password.getText().trim();

        // E-posta kontrolü
        if (emailText.length() < 5) {
            email.setError("Email adresi 5 karakterden kısa olamaz.");
            hasError = true;
        } else {
            email.clearError();
        }

        // Şifre kontrolü
        if (passwordText.length() < 5) {
            password.setError("Şifre 5 karakterden kısa olamaz.");
            hasError = true;
        } else {
            password.clearError();
        }

        // Hatalar yoksa giriş yap
        if (!hasError) {
            showLoadingDialogAndSignIn();
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