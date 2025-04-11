package org.example.ibb_ecodation_javafx.controller;

import io.reactivex.rxjava3.disposables.Disposable;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import org.example.ibb_ecodation_javafx.core.context.SpringContext;
import org.example.ibb_ecodation_javafx.core.logger.SecurityLogger;
import org.example.ibb_ecodation_javafx.core.service.LanguageService;
import org.example.ibb_ecodation_javafx.model.dto.UserDetailDto;
import org.example.ibb_ecodation_javafx.statemanagement.Store;
import org.example.ibb_ecodation_javafx.statemanagement.state.DarkModeState;
import org.example.ibb_ecodation_javafx.statemanagement.state.UserState;
import org.example.ibb_ecodation_javafx.ui.avatar.ShadcnAvatar;
import org.example.ibb_ecodation_javafx.ui.button.ShadcnButton;
import org.example.ibb_ecodation_javafx.ui.combobox.ShadcnLanguageComboBox;
import org.example.ibb_ecodation_javafx.ui.navbar.ShadcnNavbar;
import org.example.ibb_ecodation_javafx.utils.WebViewUtil;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ResourceBundle;

import static org.example.ibb_ecodation_javafx.utils.SceneUtil.loadContent;

public class AdminDashboardController {

    @FXML private BorderPane rootPane;
    @FXML private VBox sidebar;
    @FXML private VBox sidebarBottom;
    @FXML private HBox sidebarBottomInsideContainer;
    @FXML private StackPane mainContentArea;

    @FXML private ShadcnAvatar shadcnAvatar;
    @FXML private ShadcnNavbar navbar;

    // Language labels
    @FXML private Label labelUserName;
    @FXML private Label labelUserRole;

    // Sidebar buttons
    @FXML private ShadcnButton btnHome;
    @FXML private ShadcnButton btnNotifications;
    @FXML private ShadcnButton btnProfile;
    @FXML private ShadcnButton btnNotes;
    @FXML private ShadcnButton btnBackup;
    @FXML private ShadcnButton btnSettings;
    @FXML private ShadcnButton btnDocumentation;
    @FXML private ShadcnButton btnLogout;

    private final LanguageService languageService = SpringContext.getContext().getBean(LanguageService.class);
    private final SecurityLogger securityLogger = SpringContext.getContext().getBean(SecurityLogger.class);
    private final Store store = Store.getInstance();

    private Disposable languageSubscription;

    @FXML
    public void initialize() {
        store.getState().subscribe(stateRegistry -> {
            boolean darkModeEnabled = stateRegistry.getState(DarkModeState.class).isEnabled();
            changeSidebarColor(darkModeEnabled);
            changeNavbarColor(darkModeEnabled);
            changeContentColor(darkModeEnabled);
            setAvatarImageSource();
        });

        setAvatarImageSource();
        updateUIText(ShadcnLanguageComboBox.getCurrentLanguageCode());

        languageSubscription = ShadcnLanguageComboBox.watchLanguageValue().subscribe(pair -> {
            String newLangCode = pair.getKey();
            Platform.runLater(() -> updateUIText(newLangCode));
        });
    }

    private void updateUIText(String languageCode) {
        ResourceBundle bundle = languageService.loadAll(languageCode);
        try {
            btnHome.setText(bundle.getString("dashboard.home"));
            btnNotifications.setText(bundle.getString("dashboard.notifications"));
            btnProfile.setText(bundle.getString("dashboard.profile"));
            btnNotes.setText(bundle.getString("dashboard.notes"));
            btnBackup.setText(bundle.getString("dashboard.backup"));
            btnSettings.setText(bundle.getString("dashboard.config"));
            btnDocumentation.setText(bundle.getString("dashboard.docs"));
            labelUserRole.setText(bundle.getString("dashboard.role.admin"));
        } catch (Exception e) {
            btnHome.setText("Home");
            btnNotifications.setText("Notifications");
            btnProfile.setText("Profile");
            btnNotes.setText("Notes");
            btnBackup.setText("Backup");
            btnSettings.setText("Settings");
            btnDocumentation.setText("Documentation");
            labelUserRole.setText("Admin");
        }
    }

    private void changeContentColor(boolean lightMode) {
        mainContentArea.setStyle(String.format("-fx-background-radius: 10px 0px 0px 0px; -fx-background-color: %s;", lightMode ? "white" : "#1a1a1e"));
        rootPane.setStyle(String.format("-fx-background-color: %s;", lightMode ? "white" : "#121214"));
    }

    private void changeNavbarColor(boolean lightMode) {
        navbar.setStyle(String.format("-fx-background-color: %s;", lightMode ? "white" : "#121214") + "-fx-padding: 10px 20px 10px 20px;");
    }

    private void changeSidebarColor(boolean lightMode) {
        sidebar.setStyle(String.format("-fx-background-color: %s;", lightMode ? "white" : "#121214"));
        sidebarBottom.setStyle("-fx-padding: 10;");
        sidebarBottomInsideContainer.setStyle("-fx-background-radius: 14px; -fx-padding: 10;" +
                String.format("-fx-background-color: %s;", lightMode ? "#f2f2f3" : "#202024"));
        shadcnAvatar.setAvatarBorder(lightMode ? javafx.scene.paint.Color.web("#f2f2f3") : javafx.scene.paint.Color.web("#202024"));
    }

    private void setAvatarImageSource() {
        try {
            UserDetailDto userDetail = store.getCurrentState(UserState.class).getUserDetail();
            if (userDetail != null && userDetail.getProfilePicture() != null) {
                byte[] imageBytes = userDetail.getProfilePicture();
                if (imageBytes.length > 0) {
                    Image image = new Image(new ByteArrayInputStream(imageBytes));
                    shadcnAvatar.setImage(image);
                    labelUserName.setText(userDetail.getUsername());
                    return;
                }
            }
            shadcnAvatar.setImage(AdminDashboardController.class.getResource("/org/example/ibb_ecodation_javafx/assets/avatar.jpg"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void shutdown() {
        if (languageSubscription != null && !languageSubscription.isDisposed()) {
            languageSubscription.dispose();
        }
    }

    // Navigation Handlers
    @FXML private void handleHomeButton() throws IOException {
        loadContent("/org/example/ibb_ecodation_javafx/views/admin-home-view.fxml", mainContentArea);
    }

    @FXML private void handleNotificationsButton() throws IOException {
        loadContent("/org/example/ibb_ecodation_javafx/views/admin-notification-view.fxml", mainContentArea);
    }

    @FXML private void handleProfileButton() throws IOException {
        loadContent("/org/example/ibb_ecodation_javafx/views/admin-profile-view.fxml", mainContentArea);
    }

    @FXML private void handleNotesButton() throws IOException {
        loadContent("/org/example/ibb_ecodation_javafx/views/admin-note-view.fxml", mainContentArea);
    }

    @FXML private void handleBackupButton() throws IOException {
        loadContent("/org/example/ibb_ecodation_javafx/views/admin-backup-view.fxml", mainContentArea);
    }

    @FXML private void handleConfigButton() throws IOException {
        loadContent("/org/example/ibb_ecodation_javafx/views/admin-configs-view.fxml", mainContentArea);
    }

    @FXML private void openDocument() {
        WebViewUtil.showHelpPopup("/org/example/ibb_ecodation_javafx/html/uidoc.html", "Yardım Kılavuzu");
        securityLogger.logOperation("Kılavuz okuma");
    }
}
