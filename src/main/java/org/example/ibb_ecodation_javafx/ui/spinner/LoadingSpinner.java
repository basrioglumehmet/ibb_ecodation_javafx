package org.example.ibb_ecodation_javafx.ui.spinner;

import javafx.animation.RotateTransition;
import javafx.geometry.Pos;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.StrokeType;
import javafx.util.Duration;

public class LoadingSpinner extends StackPane {

    public LoadingSpinner(double size, Color strokeColor, Color armColor) {
        // Calculate scaling factor based on the original size (56 diameter outer ring)
        double baseSize = 56.0; // Original outer ring diameter
        double scale = size / baseSize; // Scale factor based on requested size

        // Inner circle (original diameter 15.7, radius 7.85)
        Circle innerCircle = new Circle(7.85 * scale, strokeColor != null ? strokeColor : Color.web("#474bff"));

        // Outer ring (original diameter 56, radius 28)
        Circle outerRing = new Circle(28 * scale);
        outerRing.setFill(Color.TRANSPARENT);
        outerRing.setStrokeWidth(11.2 * scale); // Scale stroke width proportionally
        outerRing.setStrokeType(StrokeType.CENTERED);
        outerRing.setStroke(armColor != null ? armColor : Color.web("#474bff"));
        outerRing.getStrokeDashArray().addAll(44.0 * scale, 44.0 * scale); // Scale dash array proportionally

        // Animation
        RotateTransition rotate = new RotateTransition(Duration.seconds(1), outerRing);
        rotate.setByAngle(360);
        rotate.setCycleCount(RotateTransition.INDEFINITE);
        rotate.setInterpolator(javafx.animation.Interpolator.LINEAR);
        rotate.play();

        // Layout
        this.setAlignment(Pos.CENTER);
        this.getChildren().addAll(outerRing, innerCircle);
    }

    // Default constructor
    public LoadingSpinner() {
        this(56, Color.LIGHTBLUE, Color.DODGERBLUE); // Default size set to 56 (original diameter)
    }
}