package org.example.ibb_ecodation_javafx.controller;

import io.reactivex.rxjava3.disposables.Disposable;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import org.example.ibb_ecodation_javafx.constants.ViewPathConstant;
import org.example.ibb_ecodation_javafx.core.context.SpringContext;
import org.example.ibb_ecodation_javafx.core.logger.SecurityLogger;
import org.example.ibb_ecodation_javafx.core.service.LanguageService;
import org.example.ibb_ecodation_javafx.model.dto.UserDetailDto;
import org.example.ibb_ecodation_javafx.model.enums.Role;
import org.example.ibb_ecodation_javafx.statemanagement.Store;
import org.example.ibb_ecodation_javafx.statemanagement.state.DarkModeState;
import org.example.ibb_ecodation_javafx.statemanagement.state.UserState;
import org.example.ibb_ecodation_javafx.ui.avatar.ShadcnAvatar;
import org.example.ibb_ecodation_javafx.ui.button.ShadcnButton;
import org.example.ibb_ecodation_javafx.ui.combobox.ShadcnLanguageComboBox;
import org.example.ibb_ecodation_javafx.ui.navbar.ShadcnNavbar;
import org.example.ibb_ecodation_javafx.utils.OperationSystemUtil;
import org.example.ibb_ecodation_javafx.utils.SceneUtil;
import org.example.ibb_ecodation_javafx.utils.WebViewUtil;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ResourceBundle;

import static org.example.ibb_ecodation_javafx.utils.SceneUtil.loadContent;
import static org.example.ibb_ecodation_javafx.utils.ThemeUtil.changeNavbarColor;
import static org.example.ibb_ecodation_javafx.utils.ThemeUtil.changeSidebarColor;

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
    @FXML private ShadcnButton btnLogout;
    @FXML private ShadcnButton btnCalculator;

    private final LanguageService languageService = SpringContext.getContext().getBean(LanguageService.class);
    private final SecurityLogger securityLogger = SpringContext.getContext().getBean(SecurityLogger.class);
    private final Store store = Store.getInstance();

    private Disposable languageSubscription;

    @FXML
    public void initialize() {

        store.getState().subscribe(stateRegistry -> {
            boolean darkModeEnabled = stateRegistry.getState(DarkModeState.class).isEnabled();
            changeSidebarColor(darkModeEnabled,sidebar,sidebarBottom,sidebarBottomInsideContainer,shadcnAvatar);
            changeNavbarColor(darkModeEnabled,navbar);
            changeContentColor(darkModeEnabled);
            setAvatarImageSource();
            labelUserName.setStyle(String.format("-fx-text-fill:%s",darkModeEnabled ? "white":"black"));
            var userDetail = stateRegistry.getState(UserState.class).getUserDetail();
            if(userDetail != null){
                labelUserName.setText(userDetail.getUsername());
                if(userDetail.getRole().equals(Role.USER.toString())){
                    btnBackup.setVisible(false);
                    btnBackup.setManaged(false);
                }
            }

        });
        System.out.println(store.getCurrentState(UserState.class).getUserDetail().toString());
        setAvatarImageSource();
        updateUIText();

        languageSubscription = ShadcnLanguageComboBox.watchLanguageValue().subscribe(pair -> {
            String newLangCode = pair.getKey();
            Platform.runLater(() -> updateUIText());
        });

        btnLogout.setOnAction(actionEvent -> logout());
        btnCalculator.setOnAction(actionEvent -> OperationSystemUtil.openCalculator());


    }

    private void logout(){
        try{
            store.dispatch(DarkModeState.class,new DarkModeState(true));
            store.dispatch(UserState.class,new UserState(null,false,null,null));
            SceneUtil.loadScene(
                    OtpController.class,
                    (Stage) rootPane.getScene().getWindow(),
                    String.format(ViewPathConstant.FORMAT, "login"),
                    "Login"
            );
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void updateUIText() {
       var bundle=  languageService.loadAll(ShadcnLanguageComboBox.getCurrentLanguageCode());
        System.out.println(bundle.getString("dashboard.home"));
        var userState = store.getCurrentState(UserState.class);
        var userDetail = userState.getUserDetail();
        try {
            btnHome.setText(languageService.translate("dashboard.home"));
            btnNotifications.setText(languageService.translate("dashboard.notifications"));
            btnProfile.setText(languageService.translate("dashboard.profile"));
            btnNotes.setText(languageService.translate("dashboard.notes"));
            btnBackup.setText(languageService.translate("dashboard.backup"));
            btnSettings.setText(languageService.translate("dashboard.config"));
            labelUserRole.setText(languageService.translate(userDetail.getRole()));
            btnCalculator.setText(languageService.translate("dashboard.calculator"));
            labelUserName.setText(userDetail.getUsername());
            System.out.println(userDetail.getUsername());
        } catch (Exception e) {
            btnHome.setText("Home");
            btnNotifications.setText("Notifications");
            btnProfile.setText("Profile");
            btnNotes.setText("Notes");
            btnBackup.setText("Backup");
            btnSettings.setText("Settings");
            labelUserRole.setText("Admin");
        }
    }

    private void changeContentColor(boolean isDarkMode) {
        mainContentArea.setStyle(String.format("-fx-background-radius: 10px 0px 0px 0px; -fx-background-color: %s;", !isDarkMode ? "white" : "#1a1a1e"));
        rootPane.setStyle(String.format("-fx-background-color: %s;", !isDarkMode ? "white" : "#121214"));
    }





    private void setAvatarImageSource() {
        try {
            UserDetailDto userDetail = store.getCurrentState(UserState.class).getUserDetail();
            if (userDetail != null && userDetail.getProfilePicture() != null) {
                byte[] imageBytes = userDetail.getProfilePicture();
                if (imageBytes.length > 0) {
                    Image image = new Image(new ByteArrayInputStream(imageBytes));
                    shadcnAvatar.setImage(image);
                    return;
                }
            }
            shadcnAvatar.setImage(AdminDashboardController.class.getResource("/org/example/ibb_ecodation_javafx/assets/avatar.png"));
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
