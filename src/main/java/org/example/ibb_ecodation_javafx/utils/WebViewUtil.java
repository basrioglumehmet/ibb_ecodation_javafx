package org.example.ibb_ecodation_javafx.utils;

import javafx.geometry.Pos;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.*;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.web.WebView;
import javafx.scene.shape.Rectangle;
import javafx.scene.paint.Paint;
import java.net.URL;
import java.util.Objects;

import lombok.experimental.UtilityClass;
import org.example.ibb_ecodation_javafx.ui.button.ShadcnButton;

import static org.example.ibb_ecodation_javafx.utils.GuiAnimationUtil.runAnimation;

@UtilityClass
public class WebViewUtil {

    /**
     * HTML dosyasını modal bir popup olarak gösterir.
     *
     * @param htmlResourcePath resources klasörüne göre HTML dosyasının yolu (örnek: "/help.html")
     * @param title Pencere başlığı
     * @param width Genişlik
     * @param height Yükseklik
     */
    public static void showHelpPopup(String htmlResourcePath, String title, int width, int height) {
        // WebView oluşturuluyor
        WebView webView = new WebView();
        webView.setContextMenuEnabled(false);
        webView.setPrefSize(width, height);

        // Scrollbar'ları gizle ama scroll özelliği kalsın
        webView.getEngine().setUserStyleSheetLocation(
                WebViewUtil.class.getResource("/org/example/ibb_ecodation_javafx/css/style.css").toExternalForm()
        );

        URL url = WebViewUtil.class.getResource(htmlResourcePath);
        if (url != null) {
            webView.getEngine().load(url.toExternalForm());
        } else {
            webView.getEngine().loadContent("<h2>Yardım dosyası bulunamadı!</h2>");
        }
        ImageView logoView;
        Image logoImage = new Image(Objects.requireNonNull(WebViewUtil.class.getResourceAsStream("/org/example/ibb_ecodation_javafx/assets/logo_dark.png")));
        logoView = new ImageView(logoImage);
        logoView.setFitHeight(28);
        logoView.setPreserveRatio(true);

        // Kapatma butonu
        ShadcnButton closeButton = new ShadcnButton("Pencereyi Kapat", ShadcnButton.ButtonType.DESTRUCTIVE, "EXIT", true,false,"LEFT");

        closeButton.setOnAction(event -> {
            Stage stage = (Stage) closeButton.getScene().getWindow();
            stage.close();
        });

        // Container oluşturuluyor
        HBox buttonContainer = new HBox();
        buttonContainer.setStyle("-fx-background-color: #121214; -fx-padding: 10px;");
        buttonContainer.setAlignment(Pos.CENTER_RIGHT); // Buton container içinde ortalanmış olacak
        buttonContainer.setPrefHeight(30);
        buttonContainer.setMaxHeight(Region.USE_PREF_SIZE);

        HBox rightContainer = new HBox(closeButton);
        rightContainer.setAlignment(Pos.CENTER_RIGHT);
        rightContainer.setSpacing(10);
        HBox.setHgrow(rightContainer, Priority.ALWAYS);

        buttonContainer.getChildren().addAll(logoView,rightContainer);

        // Kapatma butonunun container'ını en üste yerleştiriyoruz
        StackPane.setAlignment(buttonContainer, Pos.TOP_CENTER);

        // Paneli oluşturuyoruz
        StackPane root = new StackPane(webView, buttonContainer);
        root.setStyle("-fx-background-color: transparent;");
        root.setAlignment(Pos.CENTER);

        // Rectangle ile köşe yuvarlama efekti
        Rectangle clip = new Rectangle(0, 0, width, height);
        clip.setArcWidth(20);  // Yükseklik için radius
        clip.setArcHeight(20); // Genişlik için radius
        root.setClip(clip);

        // Scene'i ayarlıyoruz
        Scene scene = new Scene(root, width, height);
        scene.setFill(Color.TRANSPARENT); // Arkaplan transparan

        // Stage oluşturuyoruz
        Stage stage = new Stage();
        stage.setTitle(title);
        stage.setScene(scene);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.initStyle(StageStyle.TRANSPARENT); // Cam gibi görünüm
        runAnimation(root);
        stage.show();
    }
}
