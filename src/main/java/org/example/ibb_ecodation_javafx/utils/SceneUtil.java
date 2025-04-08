package org.example.ibb_ecodation_javafx.utils;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;
import lombok.experimental.UtilityClass;
import org.example.ibb_ecodation_javafx.constants.ViewPathConstant;

import java.io.IOException;

import static org.example.ibb_ecodation_javafx.utils.GuiAnimationUtil.runOpacityAnimation;
import static org.example.ibb_ecodation_javafx.utils.GuiAnimationUtil.runSceneSlideAnimation;

/**
 * Sahne (Scene) ve pencere (Stage) yönetimini kolaylaştıran yardımcı sınıf.
 * Bu sınıf, JavaFX uygulamalarında pencere oluşturma, boyutlandırma ve
 * sürükleme gibi işlemleri standardize eder.
 */
@UtilityClass
public class SceneUtil {
    /** Pencerenin en-boy oranı (16:9) */
    private static final double ASPECT_RATIO = 16.0 / 9.0;
    /** Minimum pencere genişliği (piksel) */
    private static final double MIN_WIDTH = 800;
    /** Minimum pencere yüksekliği (piksel) */
    private static final double MIN_HEIGHT = 450;
    /** Ekranın maksimum kullanım oranı (0-1 arası) */
    private static final double MAX_SCREEN_USAGE = 0.9;

    private static double xOffset = 0;  // Fare ile sürükleme için X offset
    private static double yOffset = 0;  // Fare ile sürükleme için Y offset
    private static boolean isDragging = false; // Sürükleme durumu kontrolü

    /**
     * Yeni bir sahne yükler ve pencereyi yapılandırır.
     *
     * @param clazz FXML dosyasını yükleyen sınıf
     * @param stage Gösterilecek pencere
     * @param fxmlPath FXML dosyasının yolu
     * @param title Pencere başlığı
     * @throws IOException FXML dosyası yüklenemezse
     */
    public static void loadScene(Class<?> clazz, Stage stage, String fxmlPath, String title) throws IOException {

        // Load each font weight
        Font.loadFont(clazz.getResourceAsStream("/org/example/ibb_ecodation_javafx/assets/fonts/Poppins-Regular.ttf"), 12);
        Font.loadFont(clazz.getResourceAsStream("/org/example/ibb_ecodation_javafx/assets/fonts/Poppins-Bold.ttf"), 12);
        Font.loadFont(clazz.getResourceAsStream("/org/example/ibb_ecodation_javafx/assets/fonts/Poppins-Medium.ttf"), 12);

        // FXML dosyasını yükle
        FXMLLoader loader = new FXMLLoader(clazz.getResource(fxmlPath));
        Parent root = loader.load();

        // Yeni sahne oluştur ve şeffaf arka plan ayarla
        Scene scene = new Scene(root);
        scene.setFill(Color.TRANSPARENT);

        // Eğer pencere henüz görünür değilse stilini ayarla
        if (!stage.isShowing()) {
            stage.initStyle(StageStyle.TRANSPARENT); // Başlık çubuğunu kaldırır
        }

        // Sahneyi pencereye ata ve başlığı belirle
        stage.setScene(scene);
        stage.setTitle(title);

        // Pencereyi sürüklenebilir yap ve animasyon ekle
        makeDraggable(stage, root);
        GuiAnimationUtil.runAnimation(root);

        // Pencere boyutlarını ve konumu asenkron olarak ayarla
        Platform.runLater(() -> {
            Screen targetScreen = determineTargetScreen(stage);
            initializeStageSize(stage, root, targetScreen);
            addResponsiveListeners(stage, root);
        });

        stage.show(); // Pencereyi göster
    }

    /**
     * Pencerenin hangi ekranda olduğunu belirler.
     *
     * @param stage Kontrol edilecek pencere
     * @return Bulunan ekran veya varsayılan olarak birincil ekran
     */
    private static Screen determineTargetScreen(Stage stage) {
        // Pencerenin konumu tanımsızsa birincil ekranı kullan
        if (stage.getX() == Double.NaN || stage.getY() == Double.NaN) {
            return Screen.getPrimary();
        }
        return Screen.getScreensForRectangle(
                stage.getX(), stage.getY(), 1, 1
        ).stream().findFirst().orElse(Screen.getPrimary());
    }

    /**
     * Pencerenin başlangıç boyutlarını ve konumunu ayarlar.
     *
     * @param stage Boyutlandırılacak pencere
     * @param root Sahnenin kök düğümü
     * @param screen Hedef ekran
     */
    private static void initializeStageSize(Stage stage, Parent root, Screen screen) {
        Rectangle2D screenBounds = screen.getVisualBounds();

        // Optimum boyutları hesapla
        double optimalWidth = screenBounds.getWidth() * MAX_SCREEN_USAGE;
        double optimalHeight = optimalWidth / ASPECT_RATIO;

        // Eğer yükseklik ekranı aşarsa, yüksekliğe göre ayarla
        if (optimalHeight > screenBounds.getHeight() * MAX_SCREEN_USAGE) {
            optimalHeight = screenBounds.getHeight() * MAX_SCREEN_USAGE;
            optimalWidth = optimalHeight * ASPECT_RATIO;
        }

        // Minimum ve maksimum sınırları uygula
        double finalWidth = Math.max(MIN_WIDTH, Math.min(optimalWidth, screenBounds.getWidth()));
        double finalHeight = Math.max(MIN_HEIGHT, Math.min(optimalHeight, screenBounds.getHeight()));

        // En-boy oranını koru
        if (finalHeight < finalWidth / ASPECT_RATIO) {
            finalHeight = finalWidth / ASPECT_RATIO;
        } else if (finalWidth < finalHeight * ASPECT_RATIO) {
            finalWidth = finalHeight * ASPECT_RATIO;
        }

        // Boyutları uygula ve pencereyi merkeze al
        stage.setWidth(finalWidth);
        stage.setHeight(finalHeight);
        centerStage(stage, screenBounds, finalWidth, finalHeight);

        // Kök düğümün boyutlarını ayarla
        if (root instanceof Region region) {
            region.setPrefSize(finalWidth, finalHeight);
            region.setMinSize(MIN_WIDTH, MIN_HEIGHT);
            region.setMaxSize(screenBounds.getWidth(), screenBounds.getHeight());
        }
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

        // Pencerenin ekran dışına çıkmasını önle
        stage.setX(Math.max(screenBounds.getMinX(), Math.min(centerX, screenBounds.getMaxX() - width)));
        stage.setY(Math.max(screenBounds.getMinY(), Math.min(centerY, screenBounds.getMaxY() - height)));
    }

    /**
     * Pencereyi fare ile sürüklenebilir hale getirir.
     *
     * @param stage Sürüklenecek pencere
     * @param root Sürükleme olaylarının bağlanacağı kök düğüm
     */
    private static void makeDraggable(Stage stage, Parent root) {
        root.setOnMousePressed(event -> {
            xOffset = event.getSceneX(); // Başlangıç X koordinatı
            yOffset = event.getSceneY(); // Başlangıç Y koordinatı
            isDragging = false;
        });

        root.setOnMouseDragged(event -> {
            isDragging = true;
            stage.setX(event.getScreenX() - xOffset); // Yeni X pozisyonu
            stage.setY(event.getScreenY() - yOffset); // Yeni Y pozisyonu
        });

        root.setOnMouseReleased(event -> {
            if (isDragging) {
                Screen newScreen = determineTargetScreen(stage);
                resizeForNewScreen(stage, root, newScreen);
                isDragging = false;
            }
        });
    }

    /**
     * Pencereyi yeni bir ekrana taşındığında yeniden boyutlandırır.
     *
     * @param stage Boyutlandırılacak pencere
     * @param root Kök düğüm
     * @param newScreen Yeni ekran
     */
    private static void resizeForNewScreen(Stage stage, Parent root, Screen newScreen) {
        Rectangle2D screenBounds = newScreen.getVisualBounds();

        // Yeni ekran için optimum boyutları hesapla
        double optimalWidth = screenBounds.getWidth() * MAX_SCREEN_USAGE;
        double optimalHeight = optimalWidth / ASPECT_RATIO;

        if (optimalHeight > screenBounds.getHeight() * MAX_SCREEN_USAGE) {
            optimalHeight = screenBounds.getHeight() * MAX_SCREEN_USAGE;
            optimalWidth = optimalHeight * ASPECT_RATIO;
        }

        double newWidth = Math.max(MIN_WIDTH, Math.min(optimalWidth, screenBounds.getWidth()));
        double newHeight = Math.max(MIN_HEIGHT, Math.min(optimalHeight, screenBounds.getHeight()));

        // En-boy oranını koru
        if (newHeight < newWidth / ASPECT_RATIO) {
            newHeight = newWidth / ASPECT_RATIO;
        } else if (newWidth < newHeight * ASPECT_RATIO) {
            newWidth = newHeight * ASPECT_RATIO;
        }

        // Yeni boyutları uygula
        stage.setWidth(newWidth);
        stage.setHeight(newHeight);
        centerStage(stage, screenBounds, newWidth, newHeight);

        if (root instanceof Region region) {
            region.setPrefSize(newWidth, newHeight);
            region.setMinSize(MIN_WIDTH, MIN_HEIGHT);
            region.setMaxSize(screenBounds.getWidth(), screenBounds.getHeight());
        }
    }

    /**
     * Pencere boyut değişikliklerine tepki verecek dinleyiciler ekler.
     *
     * @param stage Dinleyicilerin ekleneceği pencere
     * @param root Kök düğüm
     */
    private static void addResponsiveListeners(Stage stage, Parent root) {
        if (!(root instanceof Region region)) return;

        stage.widthProperty().addListener((obs, oldVal, newVal) -> {
            if (!isDragging) {
                double newWidth = newVal.doubleValue();
                double newHeight = Math.max(MIN_HEIGHT, newWidth / ASPECT_RATIO);
                stage.setHeight(newHeight);
                region.setPrefWidth(newWidth);
                region.setPrefHeight(newHeight);
            }
        });

        stage.heightProperty().addListener((obs, oldVal, newVal) -> {
            if (!isDragging) {
                double newHeight = newVal.doubleValue();
                double newWidth = Math.max(MIN_WIDTH, newHeight * ASPECT_RATIO);
                stage.setWidth(newWidth);
                region.setPrefHeight(newHeight);
                region.setPrefWidth(newWidth);
            }
        });

        stage.addEventHandler(WindowEvent.WINDOW_SHOWN, e -> adjustToCurrentScreen(stage));
    }

    /**
     * Pencereyi mevcut ekrana göre ayarlar.
     *
     * @param stage Ayarlanacak pencere
     */
    private static void adjustToCurrentScreen(Stage stage) {
        Screen currentScreen = determineTargetScreen(stage);
        Rectangle2D bounds = currentScreen.getVisualBounds();

        double width = stage.getWidth();
        double height = stage.getHeight();

        // Pencerenin ekran sınırları içinde kalmasını sağla
        double newX = Math.max(bounds.getMinX(), Math.min(stage.getX(), bounds.getMaxX() - width));
        double newY = Math.max(bounds.getMinY(), Math.min(stage.getY(), bounds.getMaxY() - height));

        stage.setX(newX);
        stage.setY(newY);
    }

    /**
     * Yeni bir pencere için uygun ekranı belirler.
     *
     * @param existingStage Mevcut pencere (referans)
     * @return Uygun ekran
     */
    public static Screen getScreenForNewStage(Stage existingStage) {
        if (existingStage == null || existingStage.getX() == Double.NaN) {
            return Screen.getPrimary();
        }
        return determineTargetScreen(existingStage);
    }

    // İçeriği dinamik olarak yükleme fonksiyonu
    public static void loadContent(String fxmlFile, StackPane contentArea) throws IOException {
        FXMLLoader loader = new FXMLLoader(SceneUtil.class.getResource(fxmlFile));
        // Load each font weight
        Font.loadFont(SceneUtil.class.getResourceAsStream("/org/example/ibb_ecodation_javafx/assets/fonts/Poppins-Regular.ttf"), 12);
        Font.loadFont(SceneUtil.class.getResourceAsStream("/org/example/ibb_ecodation_javafx/assets/fonts/Poppins-Bold.ttf"), 12);
        Font.loadFont(SceneUtil.class.getResourceAsStream("/org/example/ibb_ecodation_javafx/assets/fonts/Poppins-Medium.ttf"), 12);

        StackPane newContent = loader.load();
        runOpacityAnimation(newContent);
        contentArea.getChildren().setAll(newContent);
    }

    public static void loadSlidingContent(StackPane rootPane, String viewName) throws IOException {
        // Load the new FXML file (adjust the path to your target FXML)
        FXMLLoader loader = new FXMLLoader(SceneUtil.class.getResource(String.format(ViewPathConstant.FORMAT, viewName)));
        Parent newRoot = loader.load();

        // Get the current root node to be replaced
        Node oldRoot = rootPane.getChildren().get(0);
        oldRoot.setOpacity(0.2);

        // Run the slide animation
        runSceneSlideAnimation(newRoot, rootPane, oldRoot);
    }
}