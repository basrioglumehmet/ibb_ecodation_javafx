package org.example.ibb_ecodation_javafx.common.util;

import javafx.animation.ScaleTransition;
import javafx.scene.Parent;
import javafx.util.Duration;
import lombok.experimental.UtilityClass;

@UtilityClass
public class GuiAnimationUtil {
    public static void runAnimation(Parent parent){
        // Scale transition (0 to 1 animation)
        ScaleTransition scaleTransition = new ScaleTransition(Duration.seconds(0.5), parent);
        scaleTransition.setFromX(0);
        scaleTransition.setFromY(0);
        scaleTransition.setToX(1);
        scaleTransition.setToY(1);
        scaleTransition.setCycleCount(1);
        scaleTransition.setAutoReverse(false);

        // Play the animation
        scaleTransition.play();
    }
}
