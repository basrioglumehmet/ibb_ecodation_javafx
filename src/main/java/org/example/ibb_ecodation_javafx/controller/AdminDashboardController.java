package org.example.ibb_ecodation_javafx.controller;

import io.reactivex.rxjava3.disposables.Disposable;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import lombok.RequiredArgsConstructor;
import org.example.ibb_ecodation_javafx.constants.ViewPathConstant;
import org.example.ibb_ecodation_javafx.core.config.SendGridConfig;
import org.example.ibb_ecodation_javafx.core.service.LanguageService;
import org.example.ibb_ecodation_javafx.model.dto.UserDetailDto;
import org.example.ibb_ecodation_javafx.model.enums.Role;
import org.example.ibb_ecodation_javafx.statemanagement.Store;
import org.example.ibb_ecodation_javafx.statemanagement.state.DarkModeState;
import org.example.ibb_ecodation_javafx.statemanagement.state.TranslatorState;
import org.example.ibb_ecodation_javafx.statemanagement.state.UserState;
import org.example.ibb_ecodation_javafx.ui.avatar.ShadcnAvatar;
import org.example.ibb_ecodation_javafx.ui.button.ShadcnButton;
import org.example.ibb_ecodation_javafx.ui.combobox.ShadcnLanguageComboBox;
import org.example.ibb_ecodation_javafx.ui.navbar.ShadcnNavbar;
import org.example.ibb_ecodation_javafx.utils.AlertScheduler;
import org.example.ibb_ecodation_javafx.utils.OperationSystemUtil;
import org.example.ibb_ecodation_javafx.utils.SceneUtil;
import org.springframework.stereotype.Controller;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import static org.example.ibb_ecodation_javafx.utils.ThemeUtil.changeNavbarColor;
import static org.example.ibb_ecodation_javafx.utils.ThemeUtil.changeSidebarColor;

@Controller
@RequiredArgsConstructor
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

    private final LanguageService languageService;
    private  Store store = Store.getInstance();
    private final SceneUtil sceneUtil;

    private final SendGridConfig sendGridConfig;

    private Disposable languageSubscription;
    private final AlertScheduler alertScheduler;

    private final OperationSystemUtil operationSystemUtil;

    @FXML
    public void initialize() {
        loadLanguage(store.getCurrentState(TranslatorState.class).countryCode().getCode());
        System.out.println(sendGridConfig.getApiKey());
        alertScheduler.start();
        navbar.setHelpButtonText(languageService.translate("navbar.help"));
        navbar.setExitButtonText(languageService.translate("navbar.exit"));
        navbar.setOnExitButtonClick(stage -> {
            try {
                sceneUtil.loadScene(
                        SignInController.class,
                        stage,
                        String.format(ViewPathConstant.FORMAT, "login"),
                        "Login"
                );
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
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
            loadLanguage(stateRegistry.getState(TranslatorState.class).countryCode().getCode());
            updateUIText();
        });

        setAvatarImageSource();
        updateUIText();

        navbar.setLogoutAction(stage -> logout());
        btnLogout.setOnAction(actionEvent -> logout());
        btnCalculator.setOnAction(actionEvent -> operationSystemUtil.openCalculator());


    }

    private void loadLanguage(String code) {
        languageService.loadAll(code);
    }

    private void logout(){
        try{
            store.dispatch(DarkModeState.class,new DarkModeState(true));
            store.dispatch(UserState.class,new UserState(null,false,null,null));
            sceneUtil.loadScene(
                    OtpController.class,
                    (Stage) rootPane.getScene().getWindow(),
                    String.format(ViewPathConstant.FORMAT, "login"),
                    "Login"
            );

            alertScheduler.stop();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
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


    private void changeContentColor(boolean isDarkMode) {
        mainContentArea.setStyle(String.format("-fx-background-radius: 10px 0px 0px 0px; -fx-background-color: %s;", !isDarkMode ? "white" : "#1a1a1e"));
        rootPane.setStyle(String.format("-fx-background-color: %s;", !isDarkMode ? "white" : "#121214"));
    }

    private void updateUIText() {
        var userState = store.getCurrentState(UserState.class);
        var userDetail = userState.getUserDetail();
        try {
            navbar.setHelpButtonText(languageService.translate("navbar.help"));
            navbar.setExitButtonText(languageService.translate("navbar.exit"));
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
            navbar.setHelpButtonText("Help2");
            navbar.setExitButtonText("Exit");
            btnHome.setText("Home");
            btnNotifications.setText("Notifications");
            btnProfile.setText("Profile");
            btnNotes.setText("Notes");
            btnBackup.setText("Backup");
            btnSettings.setText("Settings");
            labelUserRole.setText("Admin");
        }
    }

    // Navigation Handlers
    @FXML private void handleHomeButton() throws IOException {
        sceneUtil.loadContent("/org/example/ibb_ecodation_javafx/views/admin-home-view.fxml", mainContentArea);
    }

    @FXML private void handleNotificationsButton() throws IOException {
        sceneUtil.loadContent("/org/example/ibb_ecodation_javafx/views/admin-notification-view.fxml", mainContentArea);
    }

    @FXML private void handleProfileButton() throws IOException {
        sceneUtil.loadContent("/org/example/ibb_ecodation_javafx/views/admin-profile-view.fxml", mainContentArea);
    }

    @FXML private void handleNotesButton() throws IOException {
        sceneUtil.loadContent("/org/example/ibb_ecodation_javafx/views/admin-note-view.fxml", mainContentArea);
    }

    @FXML private void handleBackupButton() throws IOException {
        sceneUtil.loadContent("/org/example/ibb_ecodation_javafx/views/admin-backup-view.fxml", mainContentArea);
    }

    @FXML private void handleConfigButton() throws IOException {
        sceneUtil.loadContent("/org/example/ibb_ecodation_javafx/views/dashboard-configs-view.fxml", mainContentArea);
    }
}
