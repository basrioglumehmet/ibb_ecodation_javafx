package org.example.ibb_ecodation_javafx.ui.button;

import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.subjects.PublishSubject;
import javafx.animation.TranslateTransition;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

public class ShadcnSwitchButton extends StackPane {
    private final Rectangle background;
    private final Circle button;
    private final TranslateTransition transition;
    private boolean value;

    private final PublishSubject<Boolean> stateSubject = PublishSubject.create();

    public ShadcnSwitchButton() {
        background = new Rectangle(50, 25, Color.web("#27272a"));
        background.setArcWidth(25);
        background.setArcHeight(25);

        button = new Circle(8, Color.web("#fff"));
        button.setTranslateX(-12);

        transition = new TranslateTransition(Duration.millis(200), button);

        this.setOnMouseClicked(event -> toggle());

        getChildren().addAll(background, button);
    }

    private void toggle() {
        setValue(!value);  // Toggling the current value on click
    }

    private void updateSwitchState() {
        transition.setToX(value ? 12 : -12);
        transition.play();
        button.setFill(value ? Color.web("black") : Color.web("#fff"));
        background.setFill(value ? Color.web("#c1e411") : Color.web("#27272a"));
        stateSubject.onNext(value);  // Emit the updated value
    }

    public void setValue(boolean value) {
        if (this.value != value) {  // Only update if the value has changed
            this.value = value;
            System.out.println("Value updated to: " + value);  // Debug statement
            updateSwitchState();  // Update UI and emit the value
        }
    }

    public boolean getValue() {
        return value;
    }

    public PublishSubject<Boolean> watchIsActive() {
        return stateSubject;
    }
}
