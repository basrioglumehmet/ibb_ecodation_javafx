package org.example.ibb_ecodation_javafx.utils;

import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.web.WebView;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import lombok.experimental.UtilityClass;
import org.example.ibb_ecodation_javafx.ui.button.ShadcnButton;

import java.net.URL;
import java.util.Objects;

import static org.example.ibb_ecodation_javafx.utils.GuiAnimationUtil.runAnimation;

@UtilityClass
public class WebViewUtil {

    /** Pencerenin en-boy oranı (16:9) */
    private static final double ASPECT_RATIO = 16.0 / 9.0;
    /** Minimum pencere genişliği (piksel) */
    private static final double MIN_WIDTH = 400;
    /** Minimum pencere yüksekliği (piksel) */
    private static final double MIN_HEIGHT = 225;
    /** Ekranın maksimum kullanım oranı (0-1 arası) */
    private static final double MAX_SCREEN_USAGE = 0.7;

    private static double xOffset = 0;
    private static double yOffset = 0;
    private static boolean isDragging = false;

    /**
     * HTML dosyasını modal bir popup olarak gösterir, ana pencerenin bulunduğu ekranın ortasında.
     *
     * @param htmlResourcePath resources klasörüne göre HTML dosyasının yolu (örnek: "/help.html")
     * @param title Pencere başlığı
     */
    public static void showHelpPopup(String htmlResourcePath, String title) {
        // WebView oluşturuluyor
        WebView webView = new WebView();
        webView.setContextMenuEnabled(false);

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
        ShadcnButton closeButton = new ShadcnButton("Pencereyi Kapat", ShadcnButton.ButtonType.DESTRUCTIVE, "EXIT", true, false, "LEFT");
        closeButton.setOnAction(event -> {
            Stage stage = (Stage) closeButton.getScene().getWindow();
            stage.close();
        });

        // Container oluşturuluyor
        HBox buttonContainer = new HBox();
        buttonContainer.setStyle("-fx-background-color: #121214; -fx-padding: 10px;");
        buttonContainer.setAlignment(Pos.CENTER_RIGHT);
        buttonContainer.setPrefHeight(30);
        buttonContainer.setMaxHeight(Region.USE_PREF_SIZE);

        HBox rightContainer = new HBox(closeButton);
        rightContainer.setAlignment(Pos.CENTER_RIGHT);
        rightContainer.setSpacing(10);
        HBox.setHgrow(rightContainer, Priority.ALWAYS);

        buttonContainer.getChildren().addAll(logoView, rightContainer);
        StackPane.setAlignment(buttonContainer, Pos.TOP_CENTER);

        // Paneli oluşturuyoruz
        StackPane root = new StackPane(webView, buttonContainer);
        root.setStyle("-fx-background-color: transparent;");
        root.setAlignment(Pos.CENTER);

        // Scene'i ayarlıyoruz
        Scene scene = new Scene(root);
        scene.setFill(Color.TRANSPARENT);

        // Stage oluşturuyoruz
        Stage stage = new Stage();
        stage.setTitle(title);
        stage.setScene(scene);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.initStyle(StageStyle.TRANSPARENT);

        // Ana pencerenin bulunduğu ekranı belirle
        Stage parentStage = getParentStage();
        Screen targetScreen = determineTargetScreen(parentStage);
        double[] dimensions = initializeStageSize(stage, root, webView, targetScreen);

        // Boyutları uygula
        stage.setWidth(dimensions[0]);
        stage.setHeight(dimensions[1]);
        webView.setPrefSize(dimensions[0], dimensions[1] - buttonContainer.getPrefHeight());
        Rectangle clip = new Rectangle(0, 0, dimensions[0], dimensions[1]);
        clip.setArcWidth(20);
        clip.setArcHeight(20);
        root.setClip(clip);

        // Ekranın ortasına yerleştir
        centerStage(stage, targetScreen.getVisualBounds(), dimensions[0], dimensions[1]);

        // Popup'ı sürüklenebilir yap
        makeDraggable(stage, root, webView);

        // Responsive dinleyiciler ekle
        addResponsiveListeners(stage, root, webView);

        // Layout'u güncelle
        root.requestLayout();
        root.layout();

        runAnimation(root);
        stage.show();
    }

    /**
     * Ana pencereyi (parent Stage) alır.
     *
     * @return Mevcut ana Stage
     */
    private static Stage getParentStage() {
        return (Stage) javafx.stage.Window.getWindows().stream()
                .filter(window -> window instanceof Stage && window.isShowing())
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("No parent stage found"));
    }

    /**
     * Ana pencerenin bulunduğu ekranı belirler.
     *
     * @param parentStage Ana pencere
     * @return Bulunan ekran veya varsayılan olarak birincil ekran
     */
    private static Screen determineTargetScreen(Stage parentStage) {
        double x = parentStage.getX() + (parentStage.getWidth() / 2); // Ana pencerenin merkezi
        double y = parentStage.getY() + (parentStage.getHeight() / 2);
        return Screen.getScreensForRectangle(x, y, 1, 1)
                .stream()
                .findFirst()
                .orElse(Screen.getPrimary());
    }

    /**
     * Pencerenin başlangıç boyutlarını hesaplar.
     *
     * @param stage Boyutlandırılacak pencere
     * @param root Sahnenin kök düğümü
     * @param webView WebView bileşeni
     * @param screen Hedef ekran
     * @return double[] {finalWidth, finalHeight}
     */
    private static double[] initializeStageSize(Stage stage, StackPane root, WebView webView, Screen screen) {
        Rectangle2D screenBounds = screen.getVisualBounds();

        double optimalWidth = screenBounds.getWidth() * MAX_SCREEN_USAGE;
        double optimalHeight = optimalWidth / ASPECT_RATIO;

        if (optimalHeight > screenBounds.getHeight() * MAX_SCREEN_USAGE) {
            optimalHeight = screenBounds.getHeight() * MAX_SCREEN_USAGE;
            optimalWidth = optimalHeight * ASPECT_RATIO;
        }

        double finalWidth = Math.max(MIN_WIDTH, Math.min(optimalWidth, screenBounds.getWidth()));
        double finalHeight = Math.max(MIN_HEIGHT, Math.min(optimalHeight, screenBounds.getHeight()));

        if (finalHeight < finalWidth / ASPECT_RATIO) {
            finalHeight = finalWidth / ASPECT_RATIO;
        } else if (finalWidth < finalHeight * ASPECT_RATIO) {
            finalWidth = finalHeight * ASPECT_RATIO;
        }

        return new double[]{finalWidth, finalHeight};
    }

    /**
     * Pencereyi ekranın ortasına yerleştirir.
     *
     * @param stage Merkezlenecek pencere
     * @param screenBounds Ekran sınırları
     * @param width Pencere genişliği
     * @param height Pencere yüksekliği
     */
    private static void centerStage(Stage stage, Rectangle2D screenBounds, double width, double height) {
        double centerX = screenBounds.getMinX() + (screenBounds.getWidth() - width) / 2;
        double centerY = screenBounds.getMinY() + (screenBounds.getHeight() - height) / 2;

        stage.setX(centerX);
        stage.setY(centerY);
    }

    /**
     * Pencereyi fare ile sürüklenebilir hale getirir.
     *
     * @param stage Sürüklenecek pencere
     * @param root Sürükleme olaylarının bağlanacağı kök düğüm
     * @param webView WebView bileşeni
     */
    private static void makeDraggable(Stage stage, StackPane root, WebView webView) {
        root.setOnMousePressed(event -> {
            xOffset = event.getSceneX();
            yOffset = event.getSceneY();
            isDragging = false;
        });

        root.setOnMouseDragged(event -> {
            isDragging = true;
            stage.setX(event.getScreenX() - xOffset);
            stage.setY(event.getScreenY() - yOffset);
        });

        root.setOnMouseReleased(event -> {
            if (isDragging) {
                Screen newScreen = Screen.getScreensForRectangle(stage.getX() + stage.getWidth() / 2,
                                stage.getY() + stage.getHeight() / 2, 1, 1)
                        .stream()
                        .findFirst()
                        .orElse(Screen.getPrimary());
                resizeForNewScreen(stage, root, webView, newScreen);
                isDragging = false;
            }
        });
    }

    /**
     * Pencereyi yeni bir ekrana taşındığında yeniden boyutlandırır.
     *
     * @param stage Boyutlandırılacak pencere
     * @param root Kök düğüm
     * @param webView WebView bileşeni
     * @param newScreen Yeni ekran
     */
    private static void resizeForNewScreen(Stage stage, StackPane root, WebView webView, Screen newScreen) {
        Rectangle2D screenBounds = newScreen.getVisualBounds();

        double optimalWidth = screenBounds.getWidth() * MAX_SCREEN_USAGE;
        double optimalHeight = optimalWidth / ASPECT_RATIO;

        if (optimalHeight > screenBounds.getHeight() * MAX_SCREEN_USAGE) {
            optimalHeight = screenBounds.getHeight() * MAX_SCREEN_USAGE;
            optimalWidth = optimalHeight * ASPECT_RATIO;
        }

        double newWidth = Math.max(MIN_WIDTH, Math.min(optimalWidth, screenBounds.getWidth()));
        double newHeight = Math.max(MIN_HEIGHT, Math.min(optimalHeight, screenBounds.getHeight()));

        if (newHeight < newWidth / ASPECT_RATIO) {
            newHeight = newWidth / ASPECT_RATIO;
        } else if (newWidth < newHeight * ASPECT_RATIO) {
            newWidth = newHeight * ASPECT_RATIO;
        }

        stage.setWidth(newWidth);
        stage.setHeight(newHeight);

        // Ekran sınırları içinde kalmasını sağla
        if (stage.getX() + newWidth > screenBounds.getMaxX()) {
            stage.setX(screenBounds.getMaxX() - newWidth);
        }
        if (stage.getY() + newHeight > screenBounds.getMaxY()) {
            stage.setY(screenBounds.getMaxY() - newHeight);
        }
        if (stage.getX() < screenBounds.getMinX()) {
            stage.setX(screenBounds.getMinX());
        }
        if (stage.getY() < screenBounds.getMinY()) {
            stage.setY(screenBounds.getMinY());
        }

        Rectangle clip = (Rectangle) root.getClip();
        clip.setWidth(newWidth);
        clip.setHeight(newHeight);

        HBox buttonContainer = (HBox) root.getChildren().get(1);
        webView.setPrefSize(newWidth, newHeight - buttonContainer.getPrefHeight());

        root.requestLayout();
        root.layout();
    }

    /**
     * Pencere boyut değişikliklerine tepki verecek dinleyiciler ekle.
     *
     * @param stage Dinleyicilerin ekleneceği pencere
     * @param root Kök düğüm
     * @param webView WebView bileşeni
     */
    private static void addResponsiveListeners(Stage stage, StackPane root, WebView webView) {
        stage.widthProperty().addListener((obs, oldVal, newVal) -> {
            if (!isDragging) {
                double newWidth = newVal.doubleValue();
                double newHeight = Math.max(MIN_HEIGHT, newWidth / ASPECT_RATIO);
                stage.setHeight(newHeight);
                Rectangle clip = (Rectangle) root.getClip();
                clip.setWidth(newWidth);
                clip.setHeight(newHeight);
                HBox buttonContainer = (HBox) root.getChildren().get(1);
                webView.setPrefSize(newWidth, newHeight - buttonContainer.getPrefHeight());
                root.requestLayout();
                root.layout();
            }
        });

        stage.heightProperty().addListener((obs, oldVal, newVal) -> {
            if (!isDragging) {
                double newHeight = newVal.doubleValue();
                double newWidth = Math.max(MIN_WIDTH, newHeight * ASPECT_RATIO);
                stage.setWidth(newWidth);
                Rectangle clip = (Rectangle) root.getClip();
                clip.setWidth(newWidth);
                clip.setHeight(newHeight);
                HBox buttonContainer = (HBox) root.getChildren().get(1);
                webView.setPrefSize(newWidth, newHeight - buttonContainer.getPrefHeight());
                root.requestLayout();
                root.layout();
            }
        });
    }
}