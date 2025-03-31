package org.example.ibb_ecodation_javafx.common.components;

import javafx.animation.TranslateTransition;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

public class IosSwitch extends StackPane {
    private boolean active;
    private final Rectangle background;
    private final Circle button;
    private final TranslateTransition transition;

    public IosSwitch() {
        active = false;
        background = new Rectangle(50, 25, Color.web("#0b41ab"));
        background.setArcWidth(25);
        background.setArcHeight(25);

        button = new Circle(8, Color.web("#fff"));
        button.setTranslateX(-12);

        transition = new TranslateTransition(Duration.millis(200), button);

        this.setOnMouseClicked(event -> toggle());

        getChildren().addAll(background, button);
    }

    private void toggle() {
        active = !active;
        transition.setToX(active ? 12 : -12);

        transition.play();
        button.setFill(Color.web("#fff"));
        background.setFill(active ? Color.web("#81c037") : Color.web("#0b41ab"));
    }
}
