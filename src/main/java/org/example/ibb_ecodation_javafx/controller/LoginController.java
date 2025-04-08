package org.example.ibb_ecodation_javafx.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.StackPane;
import org.example.ibb_ecodation_javafx.statemanagement.Store;

public class LoginController {
    @FXML
    private StackPane loginContent;

    @FXML
    private ScrollPane scrollPane;

    private final Store store = Store.getInstance();

    @FXML
    public void initialize() {
        // Set scroll speed
        final double SPEED = 0.01;
        scrollPane.getContent().setOnScroll(scrollEvent -> {
            double deltaY = scrollEvent.getDeltaY() * SPEED;
            scrollPane.setVvalue(scrollPane.getVvalue() - deltaY);
        });

        // Make ScrollPane completely transparent and invisible
        scrollPane.setStyle(
                "-fx-background-color: transparent;" +
                        "-fx-border-color: transparent;"
        );

        // Remove scrollbar visibility while keeping functionality
        Platform.runLater(() -> {
            scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
            scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
            scrollPane.setPannable(true); // Allows mouse dragging to scroll
        });
    }
}