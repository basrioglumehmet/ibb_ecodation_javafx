package org.example.ibb_ecodation_javafx.controller;

import javafx.fxml.FXML;
import javafx.scene.layout.StackPane;
import org.example.ibb_ecodation_javafx.common.components.AnimatedAvatar;
import org.example.ibb_ecodation_javafx.common.components.Avatar;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class AdminController {
    @FXML
   private Avatar avatarImageView;

    @FXML
    private StackPane avatarContainer; // FXML'deki StackPane ile bağlantı
    public void initialize() {
        List<URL> imageUrls = new ArrayList<>();

        for (int i = 0; i < 19; i++) {
            String imagePath = String.format("/org/example/ibb_ecodation_javafx/assets/agi%s.png", i);
            URL imageUrl = getClass().getResource(imagePath);

            if (imageUrl != null) {
                imageUrls.add(imageUrl);
            } else {
                System.out.println("Resim bulunamadı: " + imagePath);
            }
        }

        AnimatedAvatar animatedAvatar = new AnimatedAvatar(28, imageUrls);
        avatarContainer.getChildren().add(animatedAvatar); // Avatar'ı ekle
        // Avatar resmini yüklemek
        avatarImageView.setImage(AdminController.class.getResource("/org/example/ibb_ecodation_javafx/assets/avatar.jpg"));

    }
}
