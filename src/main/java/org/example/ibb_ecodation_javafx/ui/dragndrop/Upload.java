package org.example.ibb_ecodation_javafx.ui.dragndrop;

import javafx.scene.control.Label;
import javafx.scene.input.DragEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.StackPane;

public class Upload extends StackPane {

    private String droppedImagePath;  // Field to store the dropped image path
    private Label dragLabel;
    private String BASE_STYLE = "-fx-background-radius: 4px; -fx-padding: 10;";

    public Upload() {

        this.setStyle("-fx-background-color: black; -fx-border-width:2px; -fx-border-color:#27272a;" +
                "-fx-border-style: dashed;-fx-border-insets: 10; -fx-border-radius: 8px;");
        this.setPrefSize(Double.MAX_VALUE, 60);
        this.setPadding(new javafx.geometry.Insets(10));

        dragLabel = new Label("Resim Sürükle Bırak");
        dragLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill:white;");
        this.getChildren().add(dragLabel);

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
                System.out.println("Bırakılan resim: " + file.getAbsolutePath());

                if (isImageFile(file)) {
                    droppedImagePath = file.getAbsolutePath();  // Store the image path
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
