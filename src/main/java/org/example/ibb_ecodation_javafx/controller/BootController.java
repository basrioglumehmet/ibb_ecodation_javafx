package org.example.ibb_ecodation_javafx.controller;

import javafx.animation.ScaleTransition;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;
import lombok.RequiredArgsConstructor;
import org.example.ibb_ecodation_javafx.core.service.LanguageService;
import org.example.ibb_ecodation_javafx.service.*;
import org.example.ibb_ecodation_javafx.statemanagement.Store;
import org.example.ibb_ecodation_javafx.statemanagement.state.TranslatorState;
import org.example.ibb_ecodation_javafx.utils.SceneUtil;
import org.springframework.stereotype.Controller;

import java.io.IOException;

@Controller
@RequiredArgsConstructor
public class BootController {

    @FXML
    private VBox root;

    @FXML
    private ImageView bootLogo;

    @FXML
    private Label titleLabel;

    @FXML
    private Label statusLabel;

    @FXML
    private Label errorLabel;

    @FXML
    private Label errorDetailLabel;

    @FXML
    private Label footerLabel;

    private final Store store = Store.getInstance();

    private final BootService bootService;
    private final LanguageService languageService;
    private final VatService appLogService;
    private  boolean isLoading = true;
    private final SceneUtil sceneUtil;



    public void initialize() {
        // Validate UI components
        if (bootLogo == null || titleLabel == null || statusLabel == null || errorLabel == null ||
                errorDetailLabel == null || footerLabel == null) {
            System.out.println("Error: One or more FXML components are null. Check fx:id definitions.");
            return;
        }

        languageService.loadAll(store.getCurrentState(TranslatorState.class).countryCode().getCode());

        bootLogo.setFitWidth(100);
        bootLogo.setFitHeight(100);
        bootLogo.setPreserveRatio(true);
        bootLogo.setVisible(true);

        statusLabel.setTextFill(Color.WHITE);
        titleLabel.setTextFill(Color.WHITE);

        var data = appLogService.findAllById(49);
        System.out.println(data.size());

        titleLabel.setText(languageService.translate("boot.title"));
        statusLabel.setText(languageService.translate("boot.status.initial"));
        errorLabel.setText(languageService.translate("boot.error"));
        footerLabel.setText(languageService.translate("boot.footer"));

        ScaleTransition scaleTransition = new ScaleTransition(Duration.millis(1000), bootLogo);
        scaleTransition.setFromX(1.0);
        scaleTransition.setFromY(1.0);
        scaleTransition.setToX(1.5);
        scaleTransition.setToY(1.5);
        scaleTransition.setCycleCount(ScaleTransition.INDEFINITE);
        scaleTransition.setAutoReverse(true);
        scaleTransition.play();

        var emailTest = bootService.runMailTest();
        if (!emailTest.isSuccess()) {
            statusLabel.setVisible(false);
            errorLabel.setVisible(true);
            errorLabel.setText(languageService.translate("boot.error"));
            errorLabel.setTextFill(Color.web("#ed4245"));
            errorDetailLabel.setVisible(true);
            errorDetailLabel.setTextFill(Color.web("#ed4245"));
            errorDetailLabel.setText(emailTest.getMessage());
        } else {
            // Create a Task for non-blocking delay
            Task<Void> loadingTask = new Task<>() {
                @Override
                protected Void call() throws Exception {
                    Thread.sleep(500); // Simulate loading for 5 seconds
                    return null;
                }
            };

            loadingTask.setOnSucceeded(event -> {
                Platform.runLater(() -> {
                    try {
                        if (root != null) {
                            isLoading = false;
                            sceneUtil.loadScene(
                                    BootController.class,
                                    (Stage) root.getScene().getWindow(),
                                    "/org/example/ibb_ecodation_javafx/views/login-view.fxml",
                                    "App"
                            );
                        }
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
            });

            loadingTask.setOnFailed(event -> {
                Platform.runLater(() -> {
                    errorLabel.setVisible(true);
                    errorLabel.setText("Loading failed");
                    errorLabel.setTextFill(Color.web("#ed4245"));
                });
            });

            // Start the task in a new thread
            new Thread(loadingTask).start();
        }
    }
}