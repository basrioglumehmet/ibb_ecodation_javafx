package org.example.ibb_ecodation_javafx.ui.dragndrop;

import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.DragEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.*;
import javafx.scene.shape.Rectangle;

public class Upload extends StackPane {

    private String droppedImagePath;
    private Label dragLabel;
    private HBox insideContainer;
    private Region region;
    private String BASE_STYLE = "-fx-background-radius: 4px; -fx-padding: 10;";

    public Upload() {

        this.setStyle("-fx-background-color: #1a1a1e; -fx-border-width:2px; -fx-border-color:#2c2c30;" +
                "-fx-border-style: dashed;-fx-border-insets: 10; -fx-border-radius: 8px;");
        this.setPrefSize(Double.MAX_VALUE, 120);
        this.setPadding(new javafx.geometry.Insets(10));

        insideContainer = new HBox();

        // Region oluştur
        region = new Region();
        HBox.setHgrow(region, Priority.ALWAYS); // Region'un mümkün olduğunca genişlemesini sağlar

        dragLabel = new Label("Resim Sürükle Bırak");
        dragLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill:white;");

        // Elemanları sırayla ekle
        insideContainer.getChildren().addAll(dragLabel);
        this.getChildren().add(insideContainer);




        this.setOnDragOver(event -> handleDragOver(event));
        this.setOnDragDropped(event -> handleDragDropped(event));
        this.setOnDragExited(event -> handleDragExited(event));
    }

    private void handleDragOver(DragEvent event) {
        if (event.getGestureSource() != this && event.getDragboard().hasFiles()) {
            event.acceptTransferModes(TransferMode.COPY);
        }
        event.consume();
    }

    private void handleDragDropped(DragEvent event) {
        var dragboard = event.getDragboard();
        if (dragboard.hasFiles()) {
            dragboard.getFiles().forEach(file -> {
                if (isImageFile(file)) {
                    droppedImagePath  = file.getAbsolutePath();
                    Image image = new Image(file.toURI().toString());
                    ImageView imageView = new ImageView(image);
                    imageView.setFitWidth(80);
                    imageView.setFitHeight(60);

                    // Köşe yuvarlamak için clip
                    Rectangle clip = new Rectangle(60, 60);
                    clip.setArcWidth(8);
                    clip.setArcHeight(8);
                    imageView.setClip(clip);

                    StackPane imageContainer = new StackPane(imageView);
                    imageContainer.setStyle("-fx-background-color: transparent;");

                    insideContainer.getChildren().removeAll( dragLabel);
                    insideContainer.getChildren().addAll(imageContainer, region, dragLabel);



                    // Label güncelle
                    dragLabel.setText("Resim Seçildi");
                } else {
                    System.out.println("Resim formatında olmalıdır.");
                }
            });
            event.setDropCompleted(true);
        }
        event.consume();
    }

    private void handleDragExited(DragEvent event) {
        event.consume();
    }

    private boolean isImageFile(java.io.File file) {
        String fileName = file.getName().toLowerCase();
        return fileName.endsWith(".png") || fileName.endsWith(".jpg") || fileName.endsWith(".jpeg") || fileName.endsWith(".gif");
    }

    public String getDroppedImagePath() {
        return droppedImagePath;
    }
}
