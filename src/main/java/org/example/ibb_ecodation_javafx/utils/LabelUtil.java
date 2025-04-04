package org.example.ibb_ecodation_javafx.utils;

import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import lombok.experimental.UtilityClass;

@UtilityClass
public class LabelUtil {
    public static void updateLabelStyles(Parent parent, String textColor) {
        if (parent == null) return;

        for (Node child : parent.getChildrenUnmodifiable()) {
            if (child instanceof Label) {
                // Get the existing style (if any)
                String existingStyle = ((Label) child).getStyle();

                // If there's no existing style, just add the new text color
                if (existingStyle == null || existingStyle.isEmpty()) {
                    ((Label) child).setStyle("-fx-text-fill: " + textColor + ";");
                } else {
                    // Otherwise, append the new style to the existing styles
                    ((Label) child).setStyle(existingStyle + "-fx-text-fill: " + textColor + ";");
                }
            }

            if (child instanceof Parent) {
                // Recursively update styles for all child containers
                updateLabelStyles((Parent) child, textColor);
            }
        }
    }
}
