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
import org.example.ibb_ecodation_javafx.constants.ViewPathConstant;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;

@Component
public class SceneUtil {
    private static final Logger LOGGER = Logger.getLogger(SceneUtil.class.getName());
    private static final double ASPECT_RATIO = 16.0 / 9.0;
    private static final double MIN_WIDTH = 800;
    private static final double MIN_HEIGHT = 450;
    private static final double MAX_SCREEN_USAGE = 0.9;

    private final ApplicationContext springContext;
    private double xOffset = 0;
    private double yOffset = 0;

    public SceneUtil(ApplicationContext springContext) {
        this.springContext = springContext;
        loadFonts();
    }

    private void loadFonts() {
        List<String> fontPaths = List.of(
                "/org/example/ibb_ecodation_javafx/ui/assets/fonts/Poppins-Regular.ttf",
                "/org/example/ibb_ecodation_javafx/ui/assets/fonts/Poppins-Bold.ttf",
                "/org/example/ibb_ecodation_javafx/ui/assets/fonts/Poppins-Medium.ttf"
        );

        for (String fontPath : fontPaths) {
            try {
                if (Font.loadFont(getClass().getResourceAsStream(fontPath), 12) == null) {
                    LOGGER.warning("Failed to load font: " + fontPath);
                }
            } catch (Exception e) {
                LOGGER.severe("Error loading font " + fontPath + ": " + e.getMessage());
            }
        }
    }

    public void loadScene(Class<?> clazz, Stage stage, String fxmlPath, String title) throws IOException {
        Objects.requireNonNull(clazz, "Class cannot be null");
        Objects.requireNonNull(stage, "Stage cannot be null");
        Objects.requireNonNull(fxmlPath, "FXML path cannot be null");
        Objects.requireNonNull(title, "Title cannot be null");

        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
        loader.setControllerFactory(springContext::getBean);
        if (loader.getLocation() == null) {
            throw new IOException("FXML file not found: " + fxmlPath);
        }
        Parent root = loader.load();

        Scene scene = new Scene(root);
        scene.setFill(Color.TRANSPARENT);

        if (!stage.isShowing()) {
            stage.initStyle(StageStyle.TRANSPARENT);
        }

        stage.setScene(scene);
        stage.setTitle(title);

        makeDraggable(stage, root);

        Platform.runLater(() -> {
            Screen targetScreen = determineTargetScreen(stage);
            initializeStageSize(stage, root, targetScreen);
            addResponsiveListeners(stage, root);
        });

        loadIcons(stage);

        stage.show();
    }

    private void loadIcons(Stage stage) {
        List<String> iconPaths = List.of(
                "/org/example/ibb_ecodation_javafx/assets/favicon/favicon-16x16.png",
                "/org/example/ibb_ecodation_javafx/assets/favicon/favicon-32x32.png",
                "/org/example/ibb_ecodation_javafx/assets/favicon/favicon-96x96.png",
                "/org/example/ibb_ecodation_javafx/assets/favicon/apple-icon-180x180.png",
                "/org/example/ibb_ecodation_javafx/assets/favicon/favicon.ico"
        );

        for (String iconPath : iconPaths) {
            try {
                Image icon = new Image(Objects.requireNonNull(getClass().getResourceAsStream(iconPath)));
                stage.getIcons().add(icon);
                LOGGER.info("Loaded icon: " + iconPath);
            } catch (Exception e) {
                LOGGER.warning("Failed to load icon " + iconPath + ": " + e.getMessage());
            }
        }
    }

    private Screen determineTargetScreen(Stage stage) {
        if (Double.isNaN(stage.getX()) || Double.isNaN(stage.getY())) {
            return Screen.getPrimary();
        }
        return Screen.getScreensForRectangle(stage.getX(), stage.getY(), 1, 1)
                .stream()
                .findFirst()
                .orElse(Screen.getPrimary());
    }

    private void initializeStageSize(Stage stage, Parent root, Screen screen) {
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

    private void centerStage(Stage stage, Rectangle2D screenBounds, double width, double height) {
        double centerX = screenBounds.getMinX() + (screenBounds.getWidth() - width) / 2;
        double centerY = screenBounds.getMinY() + (screenBounds.getHeight() - height) / 2;

        stage.setX(Math.max(screenBounds.getMinX(), Math.min(centerX, screenBounds.getMaxX() - width)));
        stage.setY(Math.max(screenBounds.getMinY(), Math.min(centerY, screenBounds.getMaxY() - height)));
    }

    private void makeDraggable(Stage stage, Parent root) {
        root.setOnMousePressed(event -> {
            xOffset = event.getSceneX();
            yOffset = event.getSceneY();
        });

        root.setOnMouseDragged(event -> {
            stage.setX(event.getScreenX() - xOffset);
            stage.setY(event.getScreenY() - yOffset);
        });

        root.setOnMouseReleased(event -> {
            Screen newScreen = determineTargetScreen(stage);
            resizeForNewScreen(stage, root, newScreen);
        });
    }

    private void resizeForNewScreen(Stage stage, Parent root, Screen newScreen) {
        initializeStageSize(stage, root, newScreen);
    }

    private void addResponsiveListeners(Stage stage, Parent root) {
        if (!(root instanceof Region region)) {
            return;
        }

        stage.widthProperty().addListener((obs, oldVal, newVal) -> {
            double newWidth = newVal.doubleValue();
            double newHeight = Math.max(MIN_HEIGHT, newWidth / ASPECT_RATIO);
            stage.setHeight(newHeight);
            region.setPrefWidth(newWidth);
            region.setPrefHeight(newHeight);
        });

        stage.heightProperty().addListener((obs, oldVal, newVal) -> {
            double newHeight = newVal.doubleValue();
            double newWidth = Math.max(MIN_WIDTH, newHeight * ASPECT_RATIO);
            stage.setWidth(newWidth);
            region.setPrefWidth(newWidth);
            region.setPrefHeight(newHeight);
        });

        stage.addEventHandler(WindowEvent.WINDOW_SHOWN, e -> adjustToCurrentScreen(stage));
    }

    private void adjustToCurrentScreen(Stage stage) {
        Screen currentScreen = determineTargetScreen(stage);
        Rectangle2D bounds = currentScreen.getVisualBounds();

        double width = stage.getWidth();
        double height = stage.getHeight();

        double newX = Math.max(bounds.getMinX(), Math.min(stage.getX(), bounds.getMaxX() - width));
        double newY = Math.max(bounds.getMinY(), Math.min(stage.getY(), bounds.getMaxY() - height));

        stage.setX(newX);
        stage.setY(newY);
    }

    public void loadContent(String fxmlPath, StackPane contentArea) throws IOException {
        Objects.requireNonNull(fxmlPath, "FXML path cannot be null");
        Objects.requireNonNull(contentArea, "Content area cannot be null");

        cleanupNode(contentArea);
        contentArea.getChildren().clear();

        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));

        if (loader.getLocation() == null) {
            throw new IOException("FXML file not found: " + fxmlPath);
        }
        loader.setControllerFactory(springContext::getBean);
        StackPane newContent = loader.load();
        contentArea.getChildren().setAll(newContent);
    }

    public Parent loadParent(String fxmlPath) throws IOException {
        Objects.requireNonNull(fxmlPath, "FXML path cannot be null");

        LOGGER.info("Loading Parent FXML: " + fxmlPath);
        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
        loader.setControllerFactory(clazz -> springContext.getBean(clazz));
        if (loader.getLocation() == null) {
            throw new IOException("FXML file not found: " + fxmlPath);
        }

        Parent parent = loader.load();
        LOGGER.info("Loaded Parent FXML: " + fxmlPath);
        return parent;
    }

    public void loadSlidingContent(StackPane rootPane, String viewName) throws IOException {
        String fxmlPath = String.format(ViewPathConstant.FORMAT, viewName);
        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
        loader.setControllerFactory(springContext::getBean); // 💉 Fix injection
        Parent newRoot = loader.load();

        if (!rootPane.getChildren().isEmpty()) {
            Node oldRoot = rootPane.getChildren().get(0);
            cleanupNode(oldRoot);
            oldRoot.setOpacity(0.2);
            GuiAnimationUtil.runSceneSlideAnimation(newRoot, rootPane, oldRoot);
        } else {
            rootPane.getChildren().setAll(newRoot);
        }
    }

    private void cleanupNode(Node node) {
        if (node == null) {
            return;
        }
        node.setOnMouseClicked(null);
        node.setOnMousePressed(null);
        node.setOnMouseDragged(null);
        node.setOnMouseReleased(null);

        if (node instanceof Parent parent) {
            parent.getChildrenUnmodifiable().forEach(this::cleanupNode);
        }
    }
}