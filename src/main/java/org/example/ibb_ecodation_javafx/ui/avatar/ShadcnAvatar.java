package org.example.ibb_ecodation_javafx.ui.avatar;

import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.scene.layout.StackPane;
import javafx.scene.control.Button;

import java.net.URL;

public class ShadcnAvatar extends Button {

    private Circle avatarCircle;
    private Circle paddingCircle;
    private double avatarSize = 24;
    private double padding = 5;

    public ShadcnAvatar() {
        this(24);
    }

    public ShadcnAvatar(double size) {
        this.avatarSize = size;
        initialize();
    }

    private void initialize() {
        // Create the padding circle
        paddingCircle = new Circle(avatarSize);
        paddingCircle.setFill(Color.TRANSPARENT);
        paddingCircle.setStrokeWidth(0);

        // Create the avatar circle (this will be the circle with the image)
        avatarCircle = new Circle(avatarSize - padding);
        avatarCircle.setFill(Color.LIGHTGRAY); // Default fill color before an image is set
        avatarCircle.setEffect(new DropShadow(5, Color.BLACK)); // Add a shadow effect for the avatar
        // Make the button transparent
        this.setStyle("-fx-background-color: transparent; -fx-border-color: transparent;");

        // Add both circles to the StackPane
        StackPane stackPane = new StackPane();
        stackPane.getChildren().addAll(paddingCircle, avatarCircle);

        // Set the stackPane as the button's graphic
        this.setGraphic(stackPane);
    }

    public void setImage(URL imagePath) {
        try {
            if (imagePath != null) {
                Image image = new Image(imagePath.toExternalForm(), false);
                avatarCircle.setFill(new ImagePattern(image)); // Set image to avatar circle
            } else {
                System.out.println("Resim Bulunamadı: " + imagePath);
            }
        } catch (Exception ex) {
            System.out.println("Geçersiz Resim Adresi: " + ex.getMessage());
        }
    }

    public void setAvatarSize(double size) {
        this.avatarSize = size;
        paddingCircle.setRadius(size);
        avatarCircle.setRadius(size - padding); // Adjust avatar circle radius accordingly
    }

    public void setAvatarBorder(Color color) {
        avatarCircle.setStroke(color);
    }

    public void setPadding(double padding) {
        this.padding = padding;
        avatarCircle.setRadius(avatarSize - padding);
    }

    public Circle getAvatarCircle() {
        return avatarCircle;
    }

    public Circle getPaddingCircle() {
        return paddingCircle;
    }
}
