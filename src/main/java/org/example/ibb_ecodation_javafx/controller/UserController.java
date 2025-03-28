package org.example.ibb_ecodation_javafx.controller;

import javafx.animation.ScaleTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.ibb_ecodation_javafx.common.interfaces.IDatabaseConnection;
import org.example.ibb_ecodation_javafx.common.util.AlertUtil;
import org.example.ibb_ecodation_javafx.common.util.GuiAnimationUtil;
import org.example.ibb_ecodation_javafx.common.util.SceneUtil;
import org.example.ibb_ecodation_javafx.constant.AlertConstant;
import org.example.ibb_ecodation_javafx.constant.ViewPathConstant;
import org.example.ibb_ecodation_javafx.dao.UserDao;
import org.example.ibb_ecodation_javafx.database.SingletonDBConnection;
import org.example.ibb_ecodation_javafx.dto.UserDto;
import org.example.ibb_ecodation_javafx.mapper.UserMapper;
import org.example.ibb_ecodation_javafx.model.User;

import java.sql.SQLException;

public class UserController {

    /// /////////////////////////////////////////////////////////////////////////////
    ///  FXML Field
    @FXML
    private TextField usernameField;

    private final UserDao userDao;

    @FXML
    private TextField passwordField;

    //DI
   private final UserMapper userMapper = UserMapper.INSTANCE;

    public UserController() {
        var db = SingletonDBConnection.getInstance();
        this.userDao = UserDao.getInstance(db);
    }
    //FX Field

    /// /////////////////////////////////////////////////////////////////////////////////////
    // Login ( Kullanıcı giriş işlemini gerçekleştiren metot)
    @FXML
    public void login() throws SQLException {
        // Kullanıcı Giriş Yaparken Username, Password Almak
        String username, password;
        username = usernameField.getText();
        password = passwordField.getText();
        try{
            userDao.read(
                    User.builder()
                            .username(username)
                            .password(password)
                            .build()
            );
            switchToDashboard();
        } catch (Exception e) {
            AlertUtil.showAlert(Alert.AlertType.ERROR, AlertConstant.INFORMATION_HEADER,e.getMessage());
        }
    }

    private void switchToDashboard() {
        try {
            SceneUtil.loadScene(UserController.class,(Stage) usernameField.getScene().getWindow());
        } catch (Exception e) {
            System.out.println("Failed to load the Dashboard scene.");
            e.printStackTrace();
        }
    }

    /// //////////////////////////////////////////////////////////////////////////////////////
    // Sayfalar Arasında Geçiş (LOGIN -> REGISTER)
    // Register (Switch)
    @FXML
    private void switchToRegister(ActionEvent actionEvent) {
        try {
            // FXML Dosyalarını Yükle (Kayıt ekranının FXML dosyasını yüklüyoruz)
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(ViewPathConstant.REGISTER));
            Parent parent = fxmlLoader.load();

            // Var olan sahneyi alıp ve değiştirmek
            Stage stage = (Stage) ((javafx.scene.Node) actionEvent.getSource()).getScene().getWindow();

            var scene = new Scene(parent);
            scene.setFill(Color.TRANSPARENT);
            stage.setScene(scene);

            // Pencere başlığını 'Kayıt Ol' olarak ayarlıyalım
            stage.setTitle("Kayıt Ol");


            GuiAnimationUtil.runAnimation(parent);

            // Sahneyi göster
            stage.show();
        } catch (Exception e) {
            System.out.println("Register Sayfasında yönlendirilmedi");
            e.printStackTrace();
        }

    } //end switchToLogin

}
