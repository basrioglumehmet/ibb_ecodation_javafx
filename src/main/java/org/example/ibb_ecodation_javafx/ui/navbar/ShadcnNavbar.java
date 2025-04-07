package org.example.ibb_ecodation_javafx.ui.navbar;

import io.reactivex.rxjava3.disposables.Disposable;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import org.example.ibb_ecodation_javafx.statemanagement.Store;
import org.example.ibb_ecodation_javafx.statemanagement.state.DarkModeState;
import org.example.ibb_ecodation_javafx.ui.button.ShadcnButton;

import java.util.Objects;

public class ShadcnNavbar extends HBox {

    private final Store store = Store.getInstance();
    private final ImageView logoView;
    private Disposable disposable;
    private boolean isDarkMode;

    public ShadcnNavbar() {
        super(20);

        setStyle("-fx-background-color: black; -fx-padding: 10px; -fx-border-width: 0 0 1px; -fx-border-color:#27272a;");
        setAlignment(Pos.CENTER_LEFT);
        setPrefHeight(60);

        Image logoImage = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/org/example/ibb_ecodation_javafx/assets/logo.png")));
        logoView = new ImageView(logoImage);
        logoView.setFitHeight(28);
        logoView.setPreserveRatio(true);
        Label version = new Label("v1.0.0 - IBB Bootcamp");
        version.setStyle("-fx-font-size: 16px; -fx-text-fill:#7f7f86;");
        ShadcnButton helpButton = new ShadcnButton("Yardım", ShadcnButton.ButtonType.SECONDARY, "QUESTION", true,false,"LEFT");
        ShadcnButton exitButton = new ShadcnButton("Çıkış Yap", ShadcnButton.ButtonType.DESTRUCTIVE, "EXIT", true,false,"LEFT");
        ShadcnButton minimizeButton = new ShadcnButton("", ShadcnButton.ButtonType.GHOST, "MINIMIZE", false,true,"LEFT");
        ShadcnButton fullWindow = new ShadcnButton("", ShadcnButton.ButtonType.GHOST, "MAXIMIZE", false,true,"LEFT");
        ShadcnButton close = new ShadcnButton("", ShadcnButton.ButtonType.GHOST, "CLOSE", false,true,"LEFT");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        getChildren().addAll(logoView,  version,spacer, helpButton,exitButton,fullWindow,minimizeButton,close);

        //Store'a abone ol.
        store.getState().subscribe(stateRegistry -> {
            isDarkMode = stateRegistry.getState(DarkModeState.class).isEnabled();
            updateUI();
        });
    }

    private void updateUI() {
        Image image = new Image(Objects.requireNonNull(getClass().getResourceAsStream(
                String.format("/org/example/ibb_ecodation_javafx/assets/%s.png", isDarkMode ? "logo_dark":"logo")
        )));;
        this.logoView.setImage(image);
    }
}
