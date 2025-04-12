package org.example.ibb_ecodation_javafx.controller;

import io.reactivex.rxjava3.disposables.Disposable;
import javafx.fxml.FXML;
import javafx.scene.layout.VBox;
import org.example.ibb_ecodation_javafx.statemanagement.Store;
import org.example.ibb_ecodation_javafx.statemanagement.state.DarkModeState;
import org.example.ibb_ecodation_javafx.ui.navbar.ShadcnNavbar;

import static org.example.ibb_ecodation_javafx.utils.ThemeUtil.*;

public class VatPrinterDialogController {
    @FXML private ShadcnNavbar navbar;
    @FXML private VBox rootPane;

    private Store store;
    private Disposable darkModeDisposable;

    @FXML
    public void initialize() {
        store = Store.getInstance();

        // Initialize dark mode
        boolean initialDarkMode = store.getCurrentState(DarkModeState.class).isEnabled();
        updateDarkModeStyles(initialDarkMode);

        // Dark mode subscription
        darkModeDisposable = store.getState().subscribe(stateRegistry -> {
            boolean darkModeValue = stateRegistry.getState(DarkModeState.class).isEnabled();
            updateDarkModeStyles(darkModeValue);
        });
    }

    private void updateDarkModeStyles(boolean darkModeValue) {
        changeNavbarColor(darkModeValue, navbar);
        changeRootPaneColor(darkModeValue, rootPane);
    }

    private boolean isValidNumber(String text) {
        return text != null && !text.trim().isEmpty() && text.matches("\\d+(\\.\\d+)?");
    }

    @FXML
    private void closeDialog() {
        dispose();
        if (rootPane != null && rootPane.getScene() != null && rootPane.getScene().getWindow() != null) {
            rootPane.getScene().getWindow().hide();
        }
    }

    private void dispose() {
        if (darkModeDisposable != null && !darkModeDisposable.isDisposed()) {
            darkModeDisposable.dispose();
        }
    }
}