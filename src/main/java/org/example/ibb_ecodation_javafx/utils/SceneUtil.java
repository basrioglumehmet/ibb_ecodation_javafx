package org.example.ibb_ecodation_javafx.utils;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
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
import java.net.URL;
import java.util.Objects;

@UtilityClass
public class SceneUtil {
    private static final double ASPECT_RATIO = 16.0 / 9.0;
    private static final double MIN_WIDTH = 800;
    private static final double MIN_HEIGHT = 450;
    private static final double MAX_SCREEN_USAGE = 0.9;

    private static double xOffset = 0;
    private static double yOffset = 0;
    private static boolean isDragging = false;

    public static void loadScene(Class<?> clazz, Stage stage, String fxmlPath, String title) throws IOException {
        Font.loadFont(clazz.getResourceAsStream("/org/example/ibb_ecodation_javafx/assets/fonts/Poppins-Regular.ttf"), 12);
        Font.loadFont(clazz.getResourceAsStream("/org/example/ibb_ecodation_javafx/assets/fonts/Poppins-Bold.ttf"), 12);
        Font.loadFont(clazz.getResourceAsStream("/org/example/ibb_ecodation_javafx/assets/fonts/Poppins-Medium.ttf"), 12);

        FXMLLoader loader = new FXMLLoader(clazz.getResource(fxmlPath));
        Parent root = loader.load();


        Scene scene = new Scene(root);
        scene.setFill(Color.TRANSPARENT);

        if (!stage.isShowing()) {
            stage.initStyle(StageStyle.TRANSPARENT);
        }

        stage.setScene(scene);
        stage.setTitle(title);



        makeDraggable(stage, root);
        GuiAnimationUtil.runAnimation(root);

        Platform.runLater(() -> {
            Screen targetScreen = determineTargetScreen(stage);
            initializeStageSize(stage, root, targetScreen);
            addResponsiveListeners(stage, root);
        });


        // Load multiple icon sizes (similar to HTML favicon approach)
        String[] iconPaths = {
                "/org/example/ibb_ecodation_javafx/assets/favicon/favicon-16x16.png",
                "/org/example/ibb_ecodation_javafx/assets/favicon/favicon-32x32.png",
                "/org/example/ibb_ecodation_javafx/assets/favicon/favicon-96x96.png",
                "/org/example/ibb_ecodation_javafx/assets/favicon/apple-icon-180x180.png",
                "/org/example/ibb_ecodation_javafx/assets/favicon/favicon.ico" // Optional ICO
        };

        for (String iconPath : iconPaths) {
            try {
                java.io.InputStream iconStream = SceneUtil.class.getResourceAsStream(iconPath);
                if (iconStream != null) {
                    Image icon = new Image(iconStream);
                    stage.getIcons().add(icon);
                    System.out.println("Loaded icon: " + iconPath);
                    iconStream.close();
                } else {
                    System.err.println("Icon not found: " + iconPath);
                }
            } catch (Exception e) {
                System.err.println("Failed to load icon " + iconPath + ": " + e.getMessage());
            }
        }

        stage.show();
    }

    private static Screen determineTargetScreen(Stage stage) {
        if (stage.getX() == Double.NaN || stage.getY() == Double.NaN) {
            return Screen.getPrimary();
        }
        return Screen.getScreensForRectangle(
                stage.getX(), stage.getY(), 1, 1
        ).stream().findFirst().orElse(Screen.getPrimary());
    }

    private static void initializeStageSize(Stage stage, Parent root, Screen screen) {
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

        stage.setWidth(finalWidth);
        stage.setHeight(finalHeight);
        centerStage(stage, screenBounds, finalWidth, finalHeight);

        if (root instanceof Region region) {
            region.setPrefSize(finalWidth, finalHeight);
            region.setMinSize(MIN_WIDTH, MIN_HEIGHT);
            region.setMaxSize(screenBounds.getWidth(), screenBounds.getHeight());
        }
    }

    private static void centerStage(Stage stage, Rectangle2D screenBounds, double width, double height) {
        double centerX = screenBounds.getMinX() + (screenBounds.getWidth() - width) / 2;
        double centerY = screenBounds.getMinY() + (screenBounds.getHeight() - height) / 2;

        stage.setX(Math.max(screenBounds.getMinX(), Math.min(centerX, screenBounds.getMaxX() - width)));
        stage.setY(Math.max(screenBounds.getMinY(), Math.min(centerY, screenBounds.getMaxY() - height)));
    }

    private static void makeDraggable(Stage stage, Parent root) {
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
                Screen newScreen = determineTargetScreen(stage);
                resizeForNewScreen(stage, root, newScreen);
                isDragging = false;
            }
        });
    }

    private static void resizeForNewScreen(Stage stage, Parent root, Screen newScreen) {
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
        centerStage(stage, screenBounds, newWidth, newHeight);

        if (root instanceof Region region) {
            region.setPrefSize(newWidth, newHeight);
            region.setMinSize(MIN_WIDTH, MIN_HEIGHT);
            region.setMaxSize(screenBounds.getWidth(), screenBounds.getHeight());
        }
    }

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

    private static void adjustToCurrentScreen(Stage stage) {
        Screen currentScreen = determineTargetScreen(stage);
        Rectangle2D bounds = currentScreen.getVisualBounds();

        double width = stage.getWidth();
        double height = stage.getHeight();

        double newX = Math.max(bounds.getMinX(), Math.min(stage.getX(), bounds.getMaxX() - width));
        double newY = Math.max(bounds.getMinY(), Math.min(stage.getY(), bounds.getMaxY() - height));

        stage.setX(newX);
        stage.setY(newY);
    }

    public static Screen getScreenForNewStage(Stage existingStage) {
        if (existingStage == null || existingStage.getX() == Double.NaN) {
            return Screen.getPrimary();
        }
        return determineTargetScreen(existingStage);
    }

    // Helper method to clean up a node and its children
    private static void cleanupNode(Node node) {
        if (node == null) return;
        // Remove event handlers
        node.setOnMouseClicked(null);
        node.setOnMousePressed(null);
        node.setOnMouseDragged(null);
        node.setOnMouseReleased(null);

        // Recursively clean up children
        if (node instanceof Parent parent) {
            parent.getChildrenUnmodifiable().forEach(SceneUtil::cleanupNode);
        }
    }

    public static void loadContent(String fxmlFile, StackPane contentArea) throws IOException {
        // Clean up existing content
        if (!contentArea.getChildren().isEmpty()) {
            contentArea.getChildren().forEach(SceneUtil::cleanupNode);
            contentArea.getChildren().clear();
        }

        // Load new content
        FXMLLoader loader = new FXMLLoader(SceneUtil.class.getResource(fxmlFile));
        Font.loadFont(SceneUtil.class.getResourceAsStream("/org/example/ibb_ecodation_javafx/assets/fonts/Poppins-Regular.ttf"), 12);
        Font.loadFont(SceneUtil.class.getResourceAsStream("/org/example/ibb_ecodation_javafx/assets/fonts/Poppins-Bold.ttf"), 12);
        Font.loadFont(SceneUtil.class.getResourceAsStream("/org/example/ibb_ecodation_javafx/assets/fonts/Poppins-Medium.ttf"), 12);

        StackPane newContent = loader.load();
        GuiAnimationUtil.runOpacityAnimation(newContent);
        contentArea.getChildren().setAll(newContent);
    }

    public static void loadSlidingContent(StackPane rootPane, String viewName) throws IOException {
        // Load the new FXML file
        String fxmlPath = String.format(ViewPathConstant.FORMAT, viewName);
        FXMLLoader loader = new FXMLLoader(SceneUtil.class.getResource(fxmlPath));
        Parent newRoot = loader.load();

        // Clean up and replace content
        if (!rootPane.getChildren().isEmpty()) {
            Node oldRoot = rootPane.getChildren().get(0);
            cleanupNode(oldRoot); // Clean up old content
            oldRoot.setOpacity(0.2);
            GuiAnimationUtil.runSceneSlideAnimation(newRoot, rootPane, oldRoot);
        } else {
            rootPane.getChildren().setAll(newRoot);
        }
    }
}