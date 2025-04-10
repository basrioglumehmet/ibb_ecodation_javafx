package org.example.ibb_ecodation_javafx.ui.navbar;

import io.reactivex.rxjava3.disposables.Disposable;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.util.Duration;
import org.example.ibb_ecodation_javafx.statemanagement.Store;
import org.example.ibb_ecodation_javafx.statemanagement.state.DarkModeState;
import org.example.ibb_ecodation_javafx.ui.button.ShadcnButton;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class ShadcnNavbar extends HBox {

    private final Store store = Store.getInstance();
    private ImageView logoView;
    private Disposable disposable;
    private boolean isDarkMode;
    private static final String VERSION_TEXT_CONSTANT = "v1.0.0 - IBB Bootcamp";
    private final BooleanProperty hideButtons = new SimpleBooleanProperty(false);
    private ShadcnButton helpButton;
    private ShadcnButton exitButton;
    private ShadcnButton minimizeButton;
    private ShadcnButton fullWindowButton;
    private ShadcnButton closeButton;
    private Label version;
    private Region spacer;

    // Default constructor
    public ShadcnNavbar() {
        this(false);
    }

    public ShadcnNavbar(boolean hideButtons) {
        super(20);
        this.hideButtons.set(hideButtons);
        setAlignment(Pos.CENTER_LEFT);
        setPrefHeight(60);

        // Initialize logo
        Image logoImage = new Image(Objects.requireNonNull(
                getClass().getResourceAsStream("/org/example/ibb_ecodation_javafx/assets/logo.png"),
                "Logo image not found"
        ));
        logoView = new ImageView(logoImage);
        logoView.setFitHeight(28);
        logoView.setPreserveRatio(true);

        // Version label with timestamp
        version = new Label(VERSION_TEXT_CONSTANT);
        version.setStyle("-fx-font-size: 16px; -fx-text-fill: #7f7f86;");

        // Flexible spacer
        spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        // Real-time clock
        Timeline timeline = new Timeline(
                new KeyFrame(Duration.seconds(1), e -> {
                    LocalDateTime now = LocalDateTime.now();
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy  HH:mm:ss");
                    version.setText(VERSION_TEXT_CONSTANT + " - " + now.format(formatter));
                })
        );
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();

        // Initialize buttons
        closeButton = new ShadcnButton("", ShadcnButton.ButtonType.GHOST, "CLOSE", false, true, "LEFT");
        helpButton = new ShadcnButton("Yardım", ShadcnButton.ButtonType.SECONDARY, "QUESTION", false, false, "LEFT");
        exitButton = new ShadcnButton("Çıkış Yap", ShadcnButton.ButtonType.DESTRUCTIVE, "EXIT", false, false, "LEFT");
        minimizeButton = new ShadcnButton("", ShadcnButton.ButtonType.GHOST, "MINIMIZE", false, true, "LEFT");
        fullWindowButton = new ShadcnButton("", ShadcnButton.ButtonType.GHOST, "MAXIMIZE", false, true, "LEFT");

        // Ensure closeButton is visible by default
        closeButton.setVisible(true);

        // Initial button visibility
        updateButtonVisibility();

        // Subscribe to store updates
        disposable = store.getState().subscribe(stateRegistry -> {
            isDarkMode = stateRegistry.getState(DarkModeState.class).isEnabled();
            updateUI();
        });

        // Listen for changes to hideButtons property
        this.hideButtons.addListener((obs, oldValue, newValue) -> updateButtonVisibility());
    }

    @FXML
    public void setHideButtons(boolean status) {
        this.hideButtons.set(status);
    }

    @FXML
    public boolean getHideButtons() {
        return this.hideButtons.get();
    }

    private void updateUI() {
        String logoPath = isDarkMode ?
                "/org/example/ibb_ecodation_javafx/assets/logo.png" :
                "/org/example/ibb_ecodation_javafx/assets/logo_dark.png";

        Image image = new Image(Objects.requireNonNull(
                getClass().getResourceAsStream(logoPath),
                "Logo image not found"
        ));
        this.logoView.setImage(image);
    }

    private void updateButtonVisibility() {
        getChildren().clear(); // Clear existing children
        getChildren().addAll(logoView, version, spacer); // Always add core components

        if (!hideButtons.get()) {
            // Add all buttons when hideButtons is false
            getChildren().addAll(helpButton, exitButton, fullWindowButton, minimizeButton, closeButton);
        }

        // Force layout update
        requestLayout();
    }

    // Clean up resources
    public void dispose() {
        if (disposable != null && !disposable.isDisposed()) {
            disposable.dispose();
        }
    }
}