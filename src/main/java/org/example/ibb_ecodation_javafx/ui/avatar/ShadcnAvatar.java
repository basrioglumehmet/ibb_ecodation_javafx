package org.example.ibb_ecodation_javafx.ui.avatar;

import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.scene.layout.StackPane;
import javafx.scene.control.Button;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.net.URL;

public class ShadcnAvatar extends Button {

    private Circle avatarCircle;
    private Circle statusCircle;
    private double avatarSize = 24;
    private double padding = 5;
    private DropShadow borderEffect;

    public ShadcnAvatar() {
        this(24);
    }

    public ShadcnAvatar(double size) {
        this.avatarSize = size;
        initialize();
    }

    private void initialize() {
        // Create the avatar circle
        avatarCircle = new Circle(avatarSize - padding);
        avatarCircle.setFill(Color.LIGHTGRAY);

        // Create the status circle for active status
        double avatarRadius = avatarSize - padding;
        double statusSize = avatarRadius / 3;
        statusCircle = new Circle(statusSize);
        statusCircle.setFill(Color.web("#3ba55c"));

        double statusRadius = statusSize;

        double translateX = avatarRadius - statusRadius;
        double translateY = avatarRadius - statusRadius;
        statusCircle.setTranslateX(translateX);
        statusCircle.setTranslateY(translateY);

        borderEffect = new DropShadow();
        borderEffect.setColor(Color.web("#1a1a1e"));
        borderEffect.setRadius(2);
        borderEffect.setSpread(0.8);
        statusCircle.setEffect(borderEffect);

        // Make button transparent
        this.setStyle("-fx-background-color: transparent; -fx-border-color: transparent;");

        // Add circles to StackPane
        StackPane stackPane = new StackPane();
        stackPane.getChildren().addAll(avatarCircle, statusCircle);

        // Set as button graphic
        this.setGraphic(stackPane);
    }

    public void setImage(Image image) {
        try {
            avatarCircle.setFill(new ImagePattern(image));
        } catch (Exception ex) {
            System.out.println("Geçersiz Resim Adresi: " + ex.getMessage());
        }
    }

    public void setImage(BufferedImage bufferedImage) {
        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            ImageIO.write(bufferedImage, "png", byteArrayOutputStream);
            byte[] imageBytes = byteArrayOutputStream.toByteArray();
            Image image = new Image(new ByteArrayInputStream(imageBytes));
            avatarCircle.setFill(new ImagePattern(image));
        } catch (Exception ex) {
            System.out.println("Geçersiz Resim Adresi: " + ex.getMessage());
        }
    }

    public void setImage(URL imagePath) {
        try {
            if (imagePath != null) {
                Image image = new Image(imagePath.toExternalForm(), false);
                avatarCircle.setFill(new ImagePattern(image));
            } else {
                System.out.println("Resim Bulunamadı: " + imagePath);
            }
        } catch (Exception ex) {
            System.out.println("Geçersiz Resim Adresi: " + ex.getMessage());
        }
    }

    public void setAvatarSize(double size) {
        this.avatarSize = size;
        avatarCircle.setRadius(size - padding);
        // Update status circle
        double avatarRadius = size - padding;
        double statusSize = avatarRadius / 3;
        statusCircle.setRadius(statusSize);

        double statusRadius = statusSize;
        double translateX = avatarRadius - statusRadius;
        double translateY = avatarRadius - statusRadius;
        statusCircle.setTranslateX(translateX);
        statusCircle.setTranslateY(translateY);
    }

    public void setAvatarBorder(Color color) {
        this.borderEffect.setColor(color);
    }

    public void setPadding(double padding) {
        this.padding = padding;
        avatarCircle.setRadius(avatarSize - padding);
        // Update status circle
        double avatarRadius = avatarSize - padding;
        double statusSize = avatarRadius / 3;
        statusCircle.setRadius(statusSize);

        double statusRadius = statusSize;
        double translateX = avatarRadius - statusRadius;
        double translateY = avatarRadius - statusRadius;
        statusCircle.setTranslateX(translateX);
        statusCircle.setTranslateY(translateY);
    }

    public Circle getAvatarCircle() {
        return avatarCircle;
    }

    public void setStatusActive(boolean isActive) {
        statusCircle.setVisible(isActive);
    }

    public Circle getStatusCircle() {
        return statusCircle;
    }
}