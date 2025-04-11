package org.example.ibb_ecodation_javafx.controller;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import org.example.ibb_ecodation_javafx.core.context.SpringContext;
import org.example.ibb_ecodation_javafx.core.logger.SecurityLogger;
import org.example.ibb_ecodation_javafx.model.User;
import org.example.ibb_ecodation_javafx.model.dto.UserDetailDto;
import org.example.ibb_ecodation_javafx.statemanagement.action.IncrementAction;
import org.example.ibb_ecodation_javafx.statemanagement.Store;
import org.example.ibb_ecodation_javafx.statemanagement.enums.CountryCode;
import org.example.ibb_ecodation_javafx.statemanagement.state.DarkModeState;
import org.example.ibb_ecodation_javafx.statemanagement.state.TranslatorState;
import org.example.ibb_ecodation_javafx.statemanagement.state.UserState;
import org.example.ibb_ecodation_javafx.ui.avatar.ShadcnAvatar;
import org.example.ibb_ecodation_javafx.ui.navbar.ShadcnNavbar;
import org.example.ibb_ecodation_javafx.utils.WebViewUtil;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Stack;

import static org.example.ibb_ecodation_javafx.utils.GuiAnimationUtil.runOpacityAnimation;
import static org.example.ibb_ecodation_javafx.utils.LabelUtil.updateLabelStyles;
import static org.example.ibb_ecodation_javafx.utils.SceneUtil.loadContent;
import static org.example.ibb_ecodation_javafx.utils.TrayUtil.showTrayNotification;


public class AdminDashboardController {

    @FXML
    private BorderPane rootPane;

    @FXML
    private ShadcnAvatar shadcnAvatar;
    @FXML
    private StackPane contentArea; // StackPane olarak tanımlandığından emin olun
    @FXML
    private StackPane mainContentArea;

    private Store store;

    @FXML
    private VBox sidebar;

    @FXML
    private ShadcnNavbar navbar;

    @FXML
    private VBox sidebarBottom;

    @FXML
    private HBox sidebarBottomInsideContainer;

    private final SecurityLogger securityLogger;

    public AdminDashboardController() {
        this.securityLogger = SpringContext.getContext().getBean(SecurityLogger.class);
    }

    public void initialize(){
        store = Store.getInstance();
        store.getState().subscribe(stateRegistry -> {
            var darkModeValue = stateRegistry.getState(DarkModeState.class).isEnabled();
            System.out.println(String.format("Dark mode değiştirildi (Dashboard): %s",darkModeValue));
            changeSidebarColor(darkModeValue);
            changeNavbarColor(darkModeValue);
            changeContentColor(darkModeValue);
            String textColor = darkModeValue ? "#121214" : "white";
            setAvatarImageSource();
        });
        setAvatarImageSource();
    }

    private void changeContentColor(boolean lightModeValue) {
        this.contentArea.setStyle(String.format("-fx-background-radius: 10px 0px 0px 0px; -fx-background-color: %s;", lightModeValue ? "white":"#1a1a1e"));
        this.mainContentArea.setStyle(String.format("-fx-background-radius: 10px 0px 0px 0px; -fx-background-color: %s;", lightModeValue ? "white":"#1a1a1e"));
        this.rootPane.setStyle(String.format("-fx-background-color: %s;", lightModeValue ? "white":"#121214"));

    }

    private void changeNavbarColor(boolean lightModeValue) {
        this.navbar.setStyle(String.format("-fx-background-color: %s;", lightModeValue ? "white":"#121214") +
                "-fx-padding: 10px 20px 10px 20px;");
    }

    private void changeSidebarColor(boolean value){
        this.sidebar.setStyle(String.format("-fx-background-color: %s;", value ? "white":"#121214"));
        this.sidebarBottom.setStyle( "-fx-padding: 10;"
        );
        this.sidebarBottomInsideContainer.setStyle(
                "-fx-background-radius: 14px; -fx-padding: 10; " +
                        String.format("-fx-background-color:%s;",!value ? "#202024": "#f2f2f3")
        );
        this.shadcnAvatar.setAvatarBorder(Color.web(!value ? "#202024": "#f2f2f3"));
    }

    @FXML
    private void openDocument(ActionEvent actionEvent) {
        Node source = (Node) actionEvent.getSource();
        Bounds bounds = source.localToScreen(source.getBoundsInLocal());
        double x = bounds.getMinX();
        double y = bounds.getMinY();

        WebViewUtil.showHelpPopup(
                "/org/example/ibb_ecodation_javafx/html/uidoc.html",
                "Yardım Kılavuzu"
        );
        securityLogger.logOperation("Kılavuz okuma");
    }
    // Home butonuna tıklama işlemi
    @FXML
    private void handleHomeButton() throws IOException {
        loadContent("/org/example/ibb_ecodation_javafx/views/admin-home-view.fxml",contentArea);
    }

    // About butonuna tıklama işlemi
    @FXML
    private void handleNotificationsButton() throws IOException {
        loadContent("/org/example/ibb_ecodation_javafx/views/admin-notification-view.fxml",contentArea);
    }

    // Contact butonuna tıklama işlemi
    @FXML
    private void handleProfileButton() throws IOException {
        loadContent("/org/example/ibb_ecodation_javafx/views/admin-profile-view.fxml",contentArea);
    }

    // Contact butonuna tıklama işlemi
    @FXML
    private void handleNotesButton() throws IOException {
        loadContent("/org/example/ibb_ecodation_javafx/views/admin-note-view.fxml",contentArea);
    }

    // Contact butonuna tıklama işlemi
    @FXML
    private void handleBackupButton() throws IOException {
        loadContent("/org/example/ibb_ecodation_javafx/views/admin-backup-view.fxml",contentArea);
    }

    // Contact butonuna tıklama işlemi
    @FXML
    private void handleConfigButton() throws IOException {
        loadContent("/org/example/ibb_ecodation_javafx/views/admin-configs-view.fxml",contentArea);
    }



    private void setAvatarImageSource() {
        try {
            if(store.getCurrentState(UserState.class).getUserDetail() != null){

                byte[] imageBytes = store.getCurrentState(UserState.class).getUserDetail().getProfilePicture();

                if(imageBytes != null && imageBytes.length > 0){

                    Image image = new Image(new ByteArrayInputStream(imageBytes));
                    shadcnAvatar.setImage(image);
                }
                else{
                    shadcnAvatar.setImage(AdminDashboardController.class.getResource("/org/example/ibb_ecodation_javafx/assets/avatar.jpg"));
                }
            }
            //shadcnAvatar.setImage(AdminDashboardController.class.getResource("/org/example/ibb_ecodation_javafx/assets/avatar.jpg"));
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }
}
