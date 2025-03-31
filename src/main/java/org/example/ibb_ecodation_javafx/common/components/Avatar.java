package org.example.ibb_ecodation_javafx.common.components;

import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.scene.layout.StackPane;

import java.net.URL;

public class Avatar extends StackPane {

    private Circle avatarCircle;
    private double avatarSize = 60; // Varsayılan yarıçap (60px)

    public Avatar() {
        this(24); // Varsayılan boyut
    }

    public Avatar(double size) {
        this.avatarSize = size;
        initialize();
    }

    private void initialize() {
        // Yuvarlak avatar oluşturuluyor
        avatarCircle = new Circle(avatarSize);
//        avatarCircle.setEffect(new DropShadow(20, 0, 2, Color.BLACK)); // Gölge efekti

        getChildren().add(avatarCircle);
    }

    public void setImage(URL imagePath) {
        try {
            if (imagePath != null) {
                Image image = new Image(imagePath.toExternalForm(), false);
                avatarCircle.setFill(new ImagePattern(image));
            } else {
                System.out.println("Image not found: " + imagePath);
            }
        } catch (Exception ex) {
            System.out.println("Invalid image path: " + ex.getMessage());
        }
    }

    public void setAvatarSize(double size) {
        this.avatarSize = size;
        avatarCircle.setRadius(size);
    }

    public void setAvatarBorder(Color color) {
        avatarCircle.setStroke(color);
    }

    public Circle getAvatarCircle() {
        return avatarCircle;
    }
}
