package org.example.ibb_ecodation_javafx.controller;

import javafx.fxml.FXML;
import javafx.stage.Stage;
import org.example.ibb_ecodation_javafx.constants.ViewPathConstant;
import org.example.ibb_ecodation_javafx.ui.input.ShadcnInput;
import org.example.ibb_ecodation_javafx.utils.SceneUtil;

import java.io.IOException;

import static org.example.ibb_ecodation_javafx.utils.TrayUtil.showTrayNotification;

public class LoginController {

    @FXML
    private ShadcnInput email;

    @FXML
    private void handleHomeButton() throws IOException {

        SceneUtil.loadScene(LoginController.class,(Stage) email.getScene().getWindow(),
                String.format(ViewPathConstant.FORMAT,"admin-dashboard"),"Dashboard");
    }
}
