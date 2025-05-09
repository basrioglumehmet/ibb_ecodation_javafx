package org.example.ibb_ecodation_javafx.ui.navbar;

import io.reactivex.rxjava3.disposables.Disposable;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
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
import javafx.stage.Stage;
import javafx.util.Duration;
import org.example.ibb_ecodation_javafx.statemanagement.Store;
import org.example.ibb_ecodation_javafx.statemanagement.state.DarkModeState;
import org.example.ibb_ecodation_javafx.statemanagement.state.UserState;
import org.example.ibb_ecodation_javafx.ui.button.ShadcnButton;
import org.example.ibb_ecodation_javafx.ui.combobox.ShadcnLanguageComboBox;
import org.example.ibb_ecodation_javafx.utils.WebViewUtil;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.function.Consumer;

public class ShadcnNavbar extends HBox {

    private final Store store = Store.getInstance();
    private ImageView logoView;
    private Disposable darkModeDisposable;
    private Disposable languageDisposable;
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
    private Timeline timeline;
    private Consumer<Stage> onExitButtonClick;
    private Consumer<Stage> onLogoutAction; // Added for logout action
    private String helpButtonText = "Help";
    private String exitButtonText = "Exit";
    private String dateFormatPattern = "yyyy-MM-dd HH:mm:ss";

    public ShadcnNavbar() {
        this(false);
    }

    public ShadcnNavbar(boolean hideButtons) {
        super(20);
        this.hideButtons.set(hideButtons);
        setAlignment(Pos.CENTER_LEFT);
        setPrefHeight(60);

        Image logoImage = new Image(Objects.requireNonNull(
                getClass().getResourceAsStream("/org/example/ibb_ecodation_javafx/assets/logo.png")
        ));
        logoView = new ImageView(logoImage);
        logoView.setFitHeight(28);
        logoView.setPreserveRatio(true);

        version = new Label(VERSION_TEXT_CONSTANT);
        version.setStyle("-fx-font-size: 16px; -fx-text-fill: #7f7f86;");

        spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        timeline = new Timeline(
                new KeyFrame(Duration.seconds(1), e -> {
                    LocalDateTime now = LocalDateTime.now();
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern(dateFormatPattern);
                    version.setText(VERSION_TEXT_CONSTANT + " - " + now.format(formatter));
                })
        );
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();

        helpButton = new ShadcnButton(helpButtonText, ShadcnButton.ButtonType.SECONDARY, "QUESTION", false, false, "LEFT");
        exitButton = new ShadcnButton(exitButtonText, ShadcnButton.ButtonType.DESTRUCTIVE, "EXIT", false, false, "LEFT");
        if (!store.getCurrentState(UserState.class).isLoggedIn()) {
            exitButton.setVisible(false);
            exitButton.setManaged(false);
        }
        minimizeButton = new ShadcnButton("", ShadcnButton.ButtonType.GHOST, "MINIMIZE", false, true, "LEFT");
        fullWindowButton = new ShadcnButton("", ShadcnButton.ButtonType.GHOST, "MAXIMIZE", false, true, "LEFT");
        closeButton = new ShadcnButton("", ShadcnButton.ButtonType.GHOST, "CLOSE", false, true, "LEFT");
        closeButton.setVisible(true);

        updateButtonVisibility();

        darkModeDisposable = store.getState().subscribe(stateRegistry -> {
            isDarkMode = stateRegistry.getState(DarkModeState.class).isEnabled();
            updateUI();
        });

        languageDisposable = ShadcnLanguageComboBox.watchLanguageValue().subscribe(pair -> {
            updateTranslations();
        });

        helpButton.setOnAction(actionEvent -> {
            WebViewUtil.showUiDoc();
        });
        fullWindowButton.setOnAction(actionEvent -> {
            Stage stage = (Stage) fullWindowButton.getScene().getWindow();
            stage.setMaximized(!stage.isMaximized());
        });
        minimizeButton.setOnAction(actionEvent -> {
            Stage stage = (Stage) minimizeButton.getScene().getWindow();
            stage.setIconified(true);
        });
        closeButton.setOnAction(actionEvent -> {
            Platform.exit();
            System.exit(0);
        });

        // Set action for exit button
        exitButton.setOnAction(actionEvent -> {
            Stage stage = (Stage) exitButton.getScene().getWindow();
            if (onLogoutAction != null) {
                onLogoutAction.accept(stage);
            } else if (onExitButtonClick != null) {
                onExitButtonClick.accept(stage);
            }
        });

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

    public void setOnExitButtonClick(Consumer<Stage> handler) {
        this.onExitButtonClick = handler;
    }

    // New method to set logout action
    public void setLogoutAction(Consumer<Stage> handler) {
        this.onLogoutAction = handler;
    }

    // Getter and Setter for help button text
    public String getHelpButtonText() {
        return helpButtonText;
    }

    public void setHelpButtonText(String helpButtonText) {
        this.helpButtonText = helpButtonText;
        if (helpButton != null) {
            helpButton.setText(helpButtonText);
        }
    }

    // Getter and Setter for exit button text
    public String getExitButtonText() {
        return exitButtonText;
    }

    public void setExitButtonText(String exitButtonText) {
        this.exitButtonText = exitButtonText;
        if (exitButton != null) {
            exitButton.setText(exitButtonText);
        }
    }

    // Getter and Setter for date format pattern
    public String getDateFormatPattern() {
        return dateFormatPattern;
    }

    public void setDateFormatPattern(String dateFormatPattern) {
        this.dateFormatPattern = dateFormatPattern;
    }

    private void updateUI() {
        String logoPath = !isDarkMode ?
                "/org/example/ibb_ecodation_javafx/assets/logo.png" :
                "/org/example/ibb_ecodation_javafx/assets/logo_dark.png";
        Image image = new Image(Objects.requireNonNull(
                getClass().getResourceAsStream(logoPath)
        ));
        this.logoView.setImage(image);
    }

    private void updateTranslations() {
        helpButton.setText(helpButtonText);
        exitButton.setText(exitButtonText);
    }

    private void updateButtonVisibility() {
        getChildren().clear();
        getChildren().addAll(logoView, version, spacer);
        if (!hideButtons.get()) {
            getChildren().addAll(helpButton, exitButton, fullWindowButton, minimizeButton, closeButton);
        }
        requestLayout();
    }

    public void dispose() {
        if (darkModeDisposable != null && !darkModeDisposable.isDisposed()) {
            darkModeDisposable.dispose();
        }
        if (languageDisposable != null && !languageDisposable.isDisposed()) {
            languageDisposable.dispose();
        }
        if (timeline != null) {
            timeline.stop();
        }
    }
}