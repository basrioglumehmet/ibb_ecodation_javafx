package org.example.ibb_ecodation_javafx;

import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.example.ibb_ecodation_javafx.statemanagement.Store;
import org.example.ibb_ecodation_javafx.statemanagement.state.DarkModeState;
import java.io.IOException;

import static org.example.ibb_ecodation_javafx.utils.LabelUtil.updateLabelStyles;
import static org.example.ibb_ecodation_javafx.utils.TrayUtil.showTrayNotification;

public class HelloApplication extends Application {
    private double xOffset = 0;
    private double yOffset = 0;
    private final Store store = Store.getInstance();



    @Override
    public void start(Stage stage) throws IOException {
        // Initialize Dagger and create the component
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.
                getResource("/org/example/ibb_ecodation_javafx/views/admin-dashboard-view.fxml"));
        Parent parent = fxmlLoader.load();


        parent.setOnMousePressed(event -> {
            xOffset = event.getSceneX();
            yOffset = event.getSceneY();
        });

        parent.setOnMouseDragged(event -> {
            stage.setX(event.getScreenX() - xOffset);
            stage.setY(event.getScreenY() - yOffset);
        });

        // Kullanıcı sürüklemeyi bıraktığında ekran boyutlarını al ve güncelle
        parent.setOnMouseReleased(event -> {
            updateSize(stage, (Region) parent);
        });



        Scene scene = new Scene(parent);
        scene.setFill(Color.TRANSPARENT);
        stage.initStyle(StageStyle.TRANSPARENT);
        stage.setTitle("Login");
        stage.setScene(scene);


        ChangeListener<Number> stageSizeListener = (obs, oldValue, newValue) -> updateSize(stage, (Region) parent);
        stage.widthProperty().addListener(stageSizeListener);
        stage.heightProperty().addListener(stageSizeListener);
        stage.xProperty().addListener(stageSizeListener);
        stage.yProperty().addListener(stageSizeListener);

        stage.show();
       centerOnScren(stage,(Region) parent);

        showTrayNotification("Hoşgeldiniz", "IBB ve Ecodation Bootcamp Projesi");

        store.getState().subscribe(stateRegistry -> {
            // Get the dark mode state
            boolean isDarkMode = stateRegistry.getState(DarkModeState.class).isEnabled();
            String textColor = isDarkMode ? "black" : "white";
            updateLabelStyles(parent, textColor);
        });
    }


    private void centerOnScren(Stage stage, Region root) {
        if (Screen.getScreensForRectangle(stage.getX(), stage.getY(), stage.getWidth(), stage.getHeight()).isEmpty()) {
            return;
        }

        Rectangle2D screenBounds = Screen.getScreensForRectangle(
                stage.getX(), stage.getY(), stage.getWidth(), stage.getHeight()).get(0).getBounds();

        double screenWidth = screenBounds.getWidth();
        double screenHeight = screenBounds.getHeight();

        double newWidth = screenWidth * 0.88;
        double newHeight = screenHeight * 0.88;

      stage.setX(screenBounds.getMinX() + (screenWidth - newWidth) / 2);
       stage.setY(screenBounds.getMinY() + (screenHeight - newHeight) / 2);
    }


    private void updateSize(Stage stage, Region root) {
        if (Screen.getScreensForRectangle(stage.getX(), stage.getY(), stage.getWidth(), stage.getHeight()).isEmpty()) {
            return;
        }

        Rectangle2D screenBounds = Screen.getScreensForRectangle(
                stage.getX(), stage.getY(), stage.getWidth(), stage.getHeight()).get(0).getBounds();

        double screenWidth = screenBounds.getWidth();
        double screenHeight = screenBounds.getHeight();

        double newWidth = screenWidth * 0.88;
        double newHeight = screenHeight * 0.88;

        stage.setWidth(newWidth);
        stage.setHeight(newHeight);

//        stage.setX(screenBounds.getMinX() + (screenWidth - newWidth) / 2);
//        stage.setY(screenBounds.getMinY() + (screenHeight - newHeight) / 2);

        root.setPrefWidth(newWidth);
        root.setPrefHeight(newHeight);
    }



    public static void main(String[] args) {
        launch();
    }
}