package org.example.ibb_ecodation_javafx;

import javafx.application.Application;
import javafx.stage.Stage;
import org.example.ibb_ecodation_javafx.constants.ViewPathConstant;
import org.example.ibb_ecodation_javafx.controller.LoginController;
import org.example.ibb_ecodation_javafx.statemanagement.Store;
import org.example.ibb_ecodation_javafx.utils.AlertSchedulerUtil;
import org.example.ibb_ecodation_javafx.utils.SceneUtil;

import static org.example.ibb_ecodation_javafx.utils.TrayUtil.showTrayNotification;

public class HelloApplication extends Application {

    private final Store store = Store.getInstance();

    @Override
    public void start(Stage stage) {
        try {
            SceneUtil.loadScene(
                    LoginController.class,
                    stage,
                    String.format(ViewPathConstant.FORMAT, "login"),
                    "Login"
            );

            AlertSchedulerUtil.start();


            showTrayNotification("Hoşgeldiniz", "IBB ve Ecodation Bootcamp Projesi");

            // Dark mode için state dinleme örneği (açmak istersen)
            /*
            store.getState().subscribe(stateRegistry -> {
                boolean isDarkMode = stateRegistry.getState(DarkModeState.class).isEnabled();
                String textColor = !isDarkMode ? "black" : "white";
                updateLabelStyles(stage.getScene().getRoot(), textColor);
            });
            */

        } catch (Exception e) {
            e.printStackTrace(); // Geliştirme aşamasında hata kontrolü için
        }
    }

    public static void main(String[] args) {
        launch();
    }
}
