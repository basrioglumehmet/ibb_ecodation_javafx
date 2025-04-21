package org.example.ibb_ecodation_javafx.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import lombok.RequiredArgsConstructor;
import org.example.ibb_ecodation_javafx.core.service.LanguageService;
import org.example.ibb_ecodation_javafx.statemanagement.Store;
import org.example.ibb_ecodation_javafx.statemanagement.state.TranslatorState;
import org.example.ibb_ecodation_javafx.ui.combobox.ShadcnLanguageComboBox;
import org.example.ibb_ecodation_javafx.ui.navbar.ShadcnNavbar;
import org.springframework.stereotype.Controller;

import static org.example.ibb_ecodation_javafx.utils.ThemeUtil.changeNavbarColor;

@Controller
@RequiredArgsConstructor
public class LoginController {
    @FXML
    private StackPane loginContent;

    @FXML
    private ScrollPane scrollPane;

    @FXML
    private VBox languageArea;

    @FXML
    private ShadcnNavbar navbar;

    private  Store store = Store.getInstance();
    private final LanguageService languageService;

    @FXML
    public void initialize() {
        // Set scroll speed
        final double SPEED = 0.01;
        scrollPane.getContent().setOnScroll(scrollEvent -> {
            double deltaY = scrollEvent.getDeltaY() * SPEED;
            scrollPane.setVvalue(scrollPane.getVvalue() - deltaY);
        });
        ShadcnLanguageComboBox languageComboBox = new ShadcnLanguageComboBox();
        updateUI(store.getCurrentState(TranslatorState.class).countryCode().getCode());
        this.languageArea.getChildren().add(languageComboBox);
        // Make ScrollPane completely transparent and invisible
        scrollPane.setStyle(
                "-fx-background-color: #1a1a1e;" +
                        "-fx-border-color: #1a1a1e;"
        );


        store.getState().subscribe(stateRegistry -> {
            updateUI(stateRegistry.getState(TranslatorState.class).countryCode().getCode());
        });

        // Remove scrollbar visibility while keeping functionality
        Platform.runLater(() -> {
            scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
            scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
            scrollPane.setPannable(true); // Allows mouse dragging to scroll
            changeNavbarColor(true,navbar);
        });
    }

    private void updateUI(String code) {
        languageService.loadAll(code);
        navbar.setHelpButtonText(languageService.translate("navbar.help"));
        navbar.setExitButtonText(languageService.translate("navbar.exit"));
    }
}
