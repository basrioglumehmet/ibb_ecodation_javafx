package org.example.ibb_ecodation_javafx.ui.button;

import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.subjects.PublishSubject;
import javafx.animation.TranslateTransition;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import org.example.ibb_ecodation_javafx.statemanagement.Store;
import org.example.ibb_ecodation_javafx.statemanagement.state.DarkModeState;

public class ShadcnSwitchButton extends StackPane {
    private final Rectangle background;
    private final Circle button;
    private final TranslateTransition transition;
    private boolean value;
    private boolean isDarkMode;

    private final PublishSubject<Boolean> stateSubject = PublishSubject.create();
    private Disposable disposable;

    private static final Color DARK_OFF_BG = Color.web("#2c2c30");
    private static final Color DARK_HOVER_BG = Color.web("#38383c");
    private static final Color LIGHT_OFF_BG = Color.web("#121214");
    private static final Color LIGHT_HOVER_BG = Color.web("#252529");
    private static final Color ON_BG = Color.web("#5865f2");
    private static final Color BUTTON_COLOR = Color.web("#FFFFFF");
    private static final Color FOCUS_OUTLINE = Color.web("#5865f2");

    private static final double OFF_POSITION = -15;
    private static final double ON_POSITION = 15;

    public ShadcnSwitchButton() {
        background = new Rectangle(60, 30);
        background.setArcWidth(30);
        background.setArcHeight(30);

        button = new Circle(10, BUTTON_COLOR);
        button.setTranslateX(OFF_POSITION);

        transition = new TranslateTransition(Duration.millis(250), button);
        transition.setCycleCount(1);

        isDarkMode = Store.getInstance().getCurrentState(DarkModeState.class).isEnabled();
        background.setFill(isDarkMode ? DARK_OFF_BG : LIGHT_OFF_BG);
        button.setFill(BUTTON_COLOR);

        this.setOnMouseClicked(event -> toggle());
        addHoverAndFocusEffects();

        disposable = Store.getInstance().getState().subscribe(stateRegistry -> {
            isDarkMode = Store.getInstance().getCurrentState(DarkModeState.class).isEnabled();
            if (!value) {
                background.setFill(isDarkMode ? DARK_OFF_BG : LIGHT_OFF_BG);
            }
        });

        getChildren().addAll(background, button);
    }

    private void toggle() {
        setValue(!value);
    }

    private void updateSwitchState() {
        transition.setToX(value ? ON_POSITION : OFF_POSITION);
        transition.play();
        button.setTranslateX(value ? ON_POSITION : OFF_POSITION);

        if (value) {
            background.setFill(ON_BG);
        } else {
            background.setFill(isDarkMode ? DARK_OFF_BG : LIGHT_OFF_BG);
        }
        button.setFill(BUTTON_COLOR);
    }

    private void addHoverAndFocusEffects() {
        this.setOnMouseEntered(event -> {
            if (!value) {
                background.setFill(isDarkMode ? DARK_HOVER_BG : LIGHT_HOVER_BG);
            }
        });
        this.setOnMouseExited(event -> {
            if (!value) {
                background.setFill(isDarkMode ? DARK_OFF_BG : LIGHT_OFF_BG);
            }
        });

        background.focusedProperty().addListener((obs, wasFocused, isFocused) -> {
            if (isFocused) {
                background.setStroke(FOCUS_OUTLINE);
                background.setStrokeWidth(1.5);
            } else {
                background.setStroke(null);
                background.setStrokeWidth(0);
            }
        });

        this.setFocusTraversable(true);
    }

    public void setValue(boolean value) {
        if (this.value != value) {
            this.value = value;
            updateSwitchState();
            stateSubject.onNext(value);
        }
    }

    public boolean getValue() {
        return value;
    }

    public PublishSubject<Boolean> watchIsActive() {
        return stateSubject;
    }

    public void dispose() {
        if (disposable != null && !disposable.isDisposed()) {
            disposable.dispose();
        }
    }
}
