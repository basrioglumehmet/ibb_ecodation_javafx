package org.example.ibb_ecodation_javafx.ui.dragndrop;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.DragEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import org.example.ibb_ecodation_javafx.statemanagement.Store;
import org.example.ibb_ecodation_javafx.statemanagement.state.DarkModeState;

import static de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon.CLOUD_UPLOAD;

public class Upload extends StackPane {

    private Store store = Store.getInstance();
    private String droppedImagePath;
    private Label dragLabel;
    private VBox contentContainer;
    private StackPane imageContainer;
    private FontAwesomeIconView iconView;
    private Button removeButton;
    private String baseBackground;
    private String baseBorderColor;
    private String baseTextColor;

    public Upload() {
        baseBackground = store.getCurrentState(DarkModeState.class).isEnabled() ? "#1a1a1e" : "#fff";
        baseBorderColor = store.getCurrentState(DarkModeState.class).isEnabled() ? "#2c2c30" : "#e4e4e7";
        baseTextColor = store.getCurrentState(DarkModeState.class).isEnabled() ? "white" : "black";

        this.setStyle("-fx-border-width: 2; -fx-border-color: " + baseBorderColor + ";" +
                "-fx-border-style: dashed; -fx-border-radius: 8; -fx-background-radius: 8;" +
                "-fx-background-color: " + baseBackground + ";");
        this.setMaxWidth(Double.MAX_VALUE);
        this.setPrefHeight(120);
        this.setPadding(new Insets(10));

        contentContainer = new VBox(8);
        contentContainer.setAlignment(Pos.CENTER);

        iconView = new FontAwesomeIconView(CLOUD_UPLOAD);
        iconView.setSize("24");
        iconView.setFill(Color.valueOf(baseTextColor));

        dragLabel = new Label("Drag & Drop Image");
        dragLabel.setStyle("-fx-font-size: 16; -fx-font-weight: bold; -fx-font-family: 'Poppins';" +
                "-fx-text-fill: " + baseTextColor + ";");

        contentContainer.getChildren().addAll(iconView, dragLabel);
        this.getChildren().add(contentContainer);

        this.setOnDragOver(event -> handleDragOver(event));
        this.setOnDragDropped(event -> handleDragDropped(event));
        this.setOnDragEntered(event -> handleDragEntered(event));
        this.setOnDragExited(event -> handleDragExited(event));
        this.setOnMouseEntered(event -> this.setStyle("-fx-border-width: 2; -fx-border-color: " + baseBorderColor + ";" +
                "-fx-border-style: dashed; -fx-border-radius: 8; -fx-background-radius: 8;" +
                "-fx-background-color: " + (store.getCurrentState(DarkModeState.class).isEnabled() ? "#252529" : "#f5f5f5") + ";"));
        this.setOnMouseExited(event -> this.setStyle("-fx-border-width: 2; -fx-border-color: " + baseBorderColor + ";" +
                "-fx-border-style: dashed; -fx-border-radius: 8; -fx-background-radius: 8;" +
                "-fx-background-color: " + baseBackground + ";"));
    }

    private void handleDragOver(DragEvent event) {
        if (event.getGestureSource() != this && event.getDragboard().hasFiles()) {
            event.acceptTransferModes(TransferMode.COPY);
        }
        event.consume();
    }

    private void handleDragEntered(DragEvent event) {
        if (event.getDragboard().hasFiles()) {
            this.setStyle("-fx-border-width: 2; -fx-border-color: " + baseBorderColor + ";" +
                    "-fx-border-style: solid; -fx-border-radius: 8; -fx-background-radius: 8;" +
                    "-fx-background-color: " + (store.getCurrentState(DarkModeState.class).isEnabled() ? "#2c2c30" : "#8dd80a") + ";");
        }
        event.consume();
    }

    private void handleDragExited(DragEvent event) {
        this.setStyle("-fx-border-width: 2; -fx-border-color: " + baseBorderColor + ";" +
                "-fx-border-style: dashed; -fx-border-radius: 8; -fx-background-radius: 8;" +
                "-fx-background-color: " + baseBackground + ";");
        event.consume();
    }

    private void handleDragDropped(DragEvent event) {
        var dragboard = event.getDragboard();
        if (dragboard.hasFiles()) {
            dragboard.getFiles().forEach(file -> {
                if (isImageFile(file)) {
                    droppedImagePath = file.getAbsolutePath();
                    Image image = new Image(file.toURI().toString());
                    ImageView imageView = new ImageView(image);
                    imageView.setFitWidth(60);
                    imageView.setFitHeight(60);
                    imageView.setPreserveRatio(true);

                    Rectangle clip = new Rectangle(60, 60);
                    clip.setArcWidth(8);
                    clip.setArcHeight(8);
                    imageView.setClip(clip);

                    removeButton = new Button("X");
                    removeButton.setStyle("-fx-background-color: #ff4d4d; -fx-text-fill: white; -fx-font-size: 12;" +
                            "-fx-background-radius: 12; -fx-padding: 2 6;");
                    removeButton.setShape(new Circle(12));
                    removeButton.setMinSize(24, 24);
                    removeButton.setMaxSize(24, 24);

                    imageContainer = new StackPane(imageView, removeButton);
                    StackPane.setAlignment(removeButton, Pos.TOP_RIGHT);
                    StackPane.setMargin(removeButton, new Insets(-12, -12, 0, 0));

                    contentContainer.getChildren().clear();
                    contentContainer.getChildren().addAll(imageContainer, dragLabel);
                    dragLabel.setText("Image Uploaded");

                    removeButton.setOnAction(e -> {
                        droppedImagePath = null;
                        contentContainer.getChildren().clear();
                        contentContainer.getChildren().addAll(iconView, dragLabel);
                        dragLabel.setText("Drag & Drop Image");
                    });
                } else {
                    dragLabel.setText("Invalid Format");
                }
            });
            event.setDropCompleted(true);
        }
        event.consume();
    }

    private boolean isImageFile(java.io.File file) {
        String fileName = file.getName().toLowerCase();
        return fileName.endsWith(".png") || fileName.endsWith(".jpg") || fileName.endsWith(".jpeg") || fileName.endsWith(".gif");
    }

    public void setLabelText(String text) {
        this.dragLabel.setText(text);
    }

    public String getDroppedImagePath() {
        return droppedImagePath;
    }
}