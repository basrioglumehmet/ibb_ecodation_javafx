package org.example.ibb_ecodation_javafx.controller;


import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.example.ibb_ecodation_javafx.common.util.AlertUtil;
import org.example.ibb_ecodation_javafx.common.util.SceneUtil;
import org.example.ibb_ecodation_javafx.constant.AlertConstant;
import org.example.ibb_ecodation_javafx.constant.ViewPathConstant;
import org.example.ibb_ecodation_javafx.dao.UserDao;
import org.example.ibb_ecodation_javafx.database.SingletonDBConnection;
import org.example.ibb_ecodation_javafx.mapper.UserMapper;
import org.example.ibb_ecodation_javafx.model.User;

import java.sql.SQLException;

public class RegisterController {
    /// /////////////////////////////////////////////////////////////////////////////
    ///  FXML Field
    @FXML
    private TextField usernameField;

    private final UserDao userDao;

    @FXML
    private TextField passwordField;

    @FXML
    private TextField emailField;

    //DI
    private final UserMapper userMapper = UserMapper.INSTANCE;

    public RegisterController() {
        var db = SingletonDBConnection.getInstance();
        this.userDao = UserDao.getInstance(db);
    }

    /// /////////////////////////////////////////////////////////////////////////////////////
    // Register ( Kullanıcı kayıt işlemini gerçekleştiren metot)
    @FXML
    public void register() throws SQLException {
        String username, password, email;
        username = usernameField.getText();
        password = passwordField.getText();
        email = emailField.getText();
        try{
            userDao.create(
                    User.builder()
                            .username(username)
                            .password(password)
                            .email(email)
                            .build()
            );
            switchToLogin();
        } catch (Exception e) {
            AlertUtil.showAlert(Alert.AlertType.ERROR, AlertConstant.INFORMATION_HEADER,e.getMessage());
        }
    }

    @FXML
    private void switchToLogin() {
        try {
            SceneUtil.loadScene(UserController.class,(Stage) usernameField.getScene().getWindow(), ViewPathConstant.LOGIN,"Login");
        } catch (Exception e) {
            System.out.println("Failed to load the Dashboard scene.");
            e.printStackTrace();
        }
    }
}
