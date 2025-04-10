package org.example.ibb_ecodation_javafx.utils;

import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import lombok.experimental.UtilityClass;

import java.io.IOException;

import static org.example.ibb_ecodation_javafx.utils.GuiAnimationUtil.runAnimation;

@UtilityClass
public class DialogUtil {

    private static final double ASPECT_RATIO = 16.0 / 9.0;
    private static final double MIN_WIDTH = 200;
    private static final double MIN_HEIGHT = 225;
    private static final double MAX_SCREEN_USAGE = 0.5;

    private static double xOffset = 0;
    private static double yOffset = 0;
    private static boolean isDragging = false;
    private static Stage currentDialogStage = null; // Track the current dialog stage

    public static void showHelpPopup(String fxmlResourcePath, String title) {
        try {
            // FXML dosyasını yükle
            FXMLLoader loader = new FXMLLoader(DialogUtil.class.getResource(fxmlResourcePath));
            VBox contentPane = loader.load();

            // Ana container
            StackPane root = new StackPane(contentPane);
            root.setStyle("-fx-background-color: transparent; -fx-border-width:1; -fx-border-color:#222225;");
            root.setAlignment(javafx.geometry.Pos.CENTER);

            // Scene ayarları
            Scene scene = new Scene(root);
            scene.setFill(Color.TRANSPARENT);

            // Stage ayarları
            Stage stage = new Stage();
            stage.setTitle(title);
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initStyle(StageStyle.TRANSPARENT);

            // Store the stage as the current dialog
            currentDialogStage = stage;

            // Ekran ve boyut ayarları
            Stage parentStage = getParentStage();
            Screen targetScreen = determineTargetScreen(parentStage);
            double[] dimensions = initializeStageSize(stage, root, contentPane, targetScreen);

            stage.setWidth(dimensions[0]);
            stage.setHeight(dimensions[1]);
            contentPane.setPrefSize(dimensions[0], dimensions[1]);
//
//            Rectangle clip = new Rectangle(0, 0, dimensions[0], dimensions[1]);
//            clip.setArcWidth(20);
//            clip.setArcHeight(20);
//            root.setClip(clip);



            centerStage(stage, targetScreen.getVisualBounds(), dimensions[0], dimensions[1]);
            makeDraggable(stage, root, contentPane);
            addResponsiveListeners(stage, root, contentPane);

            // Clear the reference when the stage is closed
            stage.setOnCloseRequest(event -> currentDialogStage = null);

            root.requestLayout();
            root.layout();

            runAnimation(root);
            stage.show();
        } catch (IOException e) {
            throw new RuntimeException("Failed to load FXML file: " + fxmlResourcePath, e);
        }
    }

    // Static method to close the current dialog
    public static void closeDialog() {
        if (currentDialogStage != null && currentDialogStage.isShowing()) {
            currentDialogStage.close();
            currentDialogStage = null; // Clear the reference after closing
        }
    }

    private static Stage getParentStage() {
        return (Stage) javafx.stage.Window.getWindows().stream()
                .filter(window -> window instanceof Stage && window.isShowing())
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("No parent stage found"));
    }

    private static Screen determineTargetScreen(Stage parentStage) {
        double x = parentStage.getX() + (parentStage.getWidth() / 2);
        double y = parentStage.getY() + (parentStage.getHeight() / 2);
        return Screen.getScreensForRectangle(x, y, 1, 1)
                .stream()
                .findFirst()
                .orElse(Screen.getPrimary());
    }

    private static double[] initializeStageSize(Stage stage, StackPane root, VBox contentPane, Screen screen) {
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

    private static void centerStage(Stage stage, Rectangle2D screenBounds, double width, double height) {
        double centerX = screenBounds.getMinX() + (screenBounds.getWidth() - width) / 2;
        double centerY = screenBounds.getMinY() + (screenBounds.getHeight() - height) / 2;

        stage.setX(centerX);
        stage.setY(centerY);
    }

    private static void makeDraggable(Stage stage, StackPane root, VBox contentPane) {
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
                resizeForNewScreen(stage, root, contentPane, newScreen);
                isDragging = false;
            }
        });
    }

    private static void resizeForNewScreen(Stage stage, StackPane root, VBox contentPane, Screen newScreen) {
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

        contentPane.setPrefSize(newWidth, newHeight);

        root.requestLayout();
        root.layout();
    }

    private static void addResponsiveListeners(Stage stage, StackPane root, VBox contentPane) {
        stage.widthProperty().addListener((obs, oldVal, newVal) -> {
            if (!isDragging) {
                double newWidth = newVal.doubleValue();
                double newHeight = Math.max(MIN_HEIGHT, newWidth / ASPECT_RATIO);
                stage.setHeight(newHeight);
                contentPane.setPrefSize(newWidth, newHeight);
                root.requestLayout();
                root.layout();
            }
        });

        stage.heightProperty().addListener((obs, oldVal, newVal) -> {
            if (!isDragging) {
                double newHeight = newVal.doubleValue();
                double newWidth = Math.max(MIN_WIDTH, newHeight * ASPECT_RATIO);
                stage.setWidth(newWidth);
                contentPane.setPrefSize(newWidth, newHeight);
                root.requestLayout();
                root.layout();
            }
        });
    }
}