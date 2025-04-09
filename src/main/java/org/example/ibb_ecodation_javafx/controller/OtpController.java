package org.example.ibb_ecodation_javafx.controller;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import org.example.ibb_ecodation_javafx.constants.ViewPathConstant;
import org.example.ibb_ecodation_javafx.core.context.SpringContext;
import org.example.ibb_ecodation_javafx.service.UserOtpCodeService;
import org.example.ibb_ecodation_javafx.service.UserService;
import org.example.ibb_ecodation_javafx.ui.button.ShadcnButton;
import org.example.ibb_ecodation_javafx.ui.input.ShadcnOtpInput;
import org.example.ibb_ecodation_javafx.utils.SceneUtil;

import java.io.IOException;

public class OtpController {

    private final UserOtpCodeService userOtpCodeService;

    @FXML
    private ShadcnButton continueButton;

    @FXML
    private ShadcnOtpInput otpCodes;

    @FXML
    private StackPane rootPane;

    public OtpController() {
        this.userOtpCodeService = SpringContext.getContext().getBean(UserOtpCodeService.class);
    }

    @FXML
    public void handleVerification(ActionEvent actionEvent){
        continueButton.setIsLoading(true);
        new Thread(() -> {
            try {
                Thread.sleep(1000);
                Platform.runLater(() -> {
//                    userOtpCodeService.verifyOtp(otpCodes.getCode(), cb -> {
//                        if(cb){
//                            try {
//                                //mhmtbasrioglu.1@gmail.com
//                                SceneUtil.loadScene(OtpController.class, (Stage) rootPane.getScene().getWindow(),
//                                        String.format(ViewPathConstant.FORMAT, "login"),
//                                        "Login");
//
//                                continueButton.setIsLoading(false);
//                                otpCodes.setError(false);
//                            } catch (IOException e) {
//                                System.out.println("Otp sayfasından dashboard'a geçerken sorun oluştu:" + e.getMessage());
//                            }
//                        }
//                        else{
//                            continueButton.setIsLoading(false);
//                            otpCodes.setError(true);
//                        }
//                    });
                    //mhmtbasrioglu.1@gmail.com
                    try {
                        SceneUtil.loadScene(OtpController.class, (Stage) rootPane.getParent().getScene().getWindow(),
                                String.format(ViewPathConstant.FORMAT, "admin-dashboard"),
                                "Login");
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

        }).start();
    }
}
