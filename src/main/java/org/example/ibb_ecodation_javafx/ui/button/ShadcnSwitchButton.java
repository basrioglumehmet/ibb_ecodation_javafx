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
    private boolean value; // Not initialized here
    private boolean isDarkMode;

    private final PublishSubject<Boolean> stateSubject = PublishSubject.create();
    private Disposable disposable;

    // Colors (Using Provided Hex Codes and UI Image Accent)
    private static final Color DARK_OFF_BG = Color.web("#2c2c30");
    private static final Color DARK_HOVER_BG = Color.web("#38383c");
    private static final Color LIGHT_OFF_BG = Color.web("#121214");
    private static final Color LIGHT_HOVER_BG = Color.web("#252529");
    private static final Color ON_BG = Color.web("#F5A623");
    private static final Color BUTTON_COLOR = Color.web("#FFFFFF");
    private static final Color FOCUS_OUTLINE = Color.web("#F5A623");

    // Position Constants (Conventional Behavior - Matches New Image)
    private static final double OFF_POSITION = -15;  // Left side for off state
    private static final double ON_POSITION = 15;   // Right side for on state

    public ShadcnSwitchButton() {
        // Initialize Components
        background = new Rectangle(60, 30);
        background.setArcWidth(30);
        background.setArcHeight(30);

        button = new Circle(10, BUTTON_COLOR);
        // Set initial position to off state (safe default)
        button.setTranslateX(OFF_POSITION);

        transition = new TranslateTransition(Duration.millis(250), button);
        transition.setCycleCount(1);

        // Set Initial Colors (Based on DarkModeState, but no state update yet)
        isDarkMode = Store.getInstance().getCurrentState(DarkModeState.class).isEnabled();
        background.setFill(isDarkMode ? DARK_OFF_BG : LIGHT_OFF_BG);
        button.setFill(BUTTON_COLOR);

        // Event Handlers
        this.setOnMouseClicked(event -> toggle());
        addHoverAndFocusEffects();

        // Subscribe to Dark Mode Changes (For Background Color Updates)
        disposable = Store.getInstance().getState().subscribe(stateRegistry -> {
            isDarkMode = Store.getInstance().getCurrentState(DarkModeState.class).isEnabled();
            if (!value) { // Only update background if switch is off
                background.setFill(isDarkMode ? DARK_OFF_BG : LIGHT_OFF_BG);
            }
        });

        getChildren().addAll(background, button);
    }

    private void toggle() {
        setValue(!value);
    }

    private void updateSwitchState() {
        // Update Position
        transition.setToX(value ? ON_POSITION : OFF_POSITION);
        transition.play();

        // Ensure the button's position is set even if transition doesn't play
        button.setTranslateX(value ? ON_POSITION : OFF_POSITION);

        // Update Colors
        if (value) {
            background.setFill(ON_BG);
        } else {
            background.setFill(isDarkMode ? DARK_OFF_BG : LIGHT_OFF_BG);
        }
        button.setFill(BUTTON_COLOR); // Always white for consistency
    }

    private void addHoverAndFocusEffects() {
        // Hover Effect
        this.setOnMouseEntered(event -> {
            if (!value) { // Only apply hover when off
                background.setFill(isDarkMode ? DARK_HOVER_BG : LIGHT_HOVER_BG);
            }
        });
        this.setOnMouseExited(event -> {
            if (!value) { // Reset to off state color
                background.setFill(isDarkMode ? DARK_OFF_BG : LIGHT_OFF_BG);
            }
        });

        // Focus Effect
        background.focusedProperty().addListener((obs, wasFocused, isFocused) -> {
            if (isFocused) {
                background.setStroke(FOCUS_OUTLINE);
                background.setStrokeWidth(1.5);
            } else {
                background.setStroke(null);
                background.setStrokeWidth(0);
            }
        });

        // Ensure the switch is focusable
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

    // Cleanup
    public void dispose() {
        if (disposable != null && !disposable.isDisposed()) {
            disposable.dispose();
        }
    }
}