package org.example.ibb_ecodation_javafx.common.util;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import lombok.experimental.UtilityClass;

@UtilityClass
public class SceneUtil {
    public void loadScene(Class<?> currentClass){
        // Load the FXML for the user dashboard screen
        FXMLLoader fxmlLoader = new FXMLLoader(currentClass.getResource("/org/example/ibb_ecodation_javafx/user-view.fxml"));
        Parent parent = fxmlLoader.load();  // This returns the root node of the FXML file

        // Get the current stage and change the scene
        Stage stage = (Stage) usernameField.getScene().getWindow();

        Scene scene = new Scene(parent);
        scene.setFill(Color.TRANSPARENT); // Optional: makes the background transparent
        stage.setScene(scene);
        stage.centerOnScreen();

        // Set the title of the window
        stage.setTitle("User Dashboard");

        // Run any animations you want for the transition (assuming GuiAnimationUtil handles this)
        GuiAnimationUtil.runAnimation(parent);

        // Show the new scene
        stage.show();
    }
}
