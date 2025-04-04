package org.example.ibb_ecodation_javafx.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import org.example.ibb_ecodation_javafx.statemanagement.action.IncrementAction;
import org.example.ibb_ecodation_javafx.statemanagement.Store;
import org.example.ibb_ecodation_javafx.statemanagement.enums.CountryCode;
import org.example.ibb_ecodation_javafx.statemanagement.state.DarkModeState;
import org.example.ibb_ecodation_javafx.statemanagement.state.TranslatorState;
import org.example.ibb_ecodation_javafx.statemanagement.state.UserState;
import org.example.ibb_ecodation_javafx.ui.avatar.ShadcnAvatar;
import org.example.ibb_ecodation_javafx.ui.navbar.ShadcnNavbar;

import java.io.IOException;

import static org.example.ibb_ecodation_javafx.utils.LabelUtil.updateLabelStyles;
import static org.example.ibb_ecodation_javafx.utils.TrayUtil.showTrayNotification;


public class AdminDashboardController {

    @FXML
    private ShadcnAvatar shadcnAvatar;
    @FXML
    private StackPane contentArea; // StackPane olarak tanımlandığından emin olun

    private Store store;

    @FXML
    private VBox sidebar;

    @FXML
    private ShadcnNavbar navbar;

    @FXML
    private VBox sidebarBottom;

    public void initialize(){
        store = Store.getInstance();
        store.getState().subscribe(stateRegistry -> {
            var darkModeValue = stateRegistry.getState(DarkModeState.class).isEnabled();
            System.out.println(String.format("Dark mode değiştirildi (Dashboard): %s",darkModeValue));
            changeSidebarColor(darkModeValue);
            changeNavbarColor(darkModeValue);
            changeContentColor(darkModeValue);
            String textColor = darkModeValue ? "black" : "white";
        });

        store.dispatch(UserState.class, new UserState("mehmet", true));
        store.dispatch(DarkModeState.class, new DarkModeState(true));
        setAvatarImageSource();

        store.dispatch(TranslatorState.class, new TranslatorState(CountryCode.EN));
        store.dispatch(DarkModeState.class, new DarkModeState(false));
    }

    private void changeContentColor(boolean lightModeValue) {
        this.contentArea.setStyle(String.format("-fx-background-color: %s;", lightModeValue ? "white":"black"));
    }

    private void changeNavbarColor(boolean lightModeValue) {
        this.navbar.setStyle(String.format("-fx-background-color: %s;", lightModeValue ? "white":"black") +
                " -fx-padding: 10px; -fx-border-width: 0 0 1px; " +
                String.format("-fx-border-color:%s",!lightModeValue ? "#27272a": "#f2f2f3"));
    }

    private void changeSidebarColor(boolean value){
        this.sidebar.setStyle(String.format("-fx-background-color: %s;", value ? "white":"black") +
                "-fx-min-width: 300px;" +
                "-fx-pref-width: 300px;" +
                "-fx-border-width: 0 1;" +
                String.format("-fx-border-color:%s",!value ? "#27272a": "#f2f2f3"));
        this.sidebarBottom.setStyle(
                "-fx-border-width: 1 0; " +
                        String.format("-fx-border-color:%s;",!value ? "#27272a": "#f2f2f3")+
                "-fx-padding: 15;"
        );
    }

    // Home butonuna tıklama işlemi
    @FXML
    private void handleHomeButton() throws IOException {
        loadContent("/org/example/ibb_ecodation_javafx/views/admin-home-view.fxml");
        showTrayNotification("Anasayfada kullanıcı ve kdv yönetimi yapabilirsiniz.", "IBB ve Ecodation Bootcamp Projesi");
    }

    // About butonuna tıklama işlemi
    @FXML
    private void handleNotificationsButton() throws IOException {
        loadContent("/org/example/ibb_ecodation_javafx/views/admin-notification-view.fxml");
    }

    // Contact butonuna tıklama işlemi
    @FXML
    private void handleProfileButton() throws IOException {
        loadContent("/org/example/ibb_ecodation_javafx/views/admin-profile-view.fxml");
    }

    // Contact butonuna tıklama işlemi
    @FXML
    private void handleNotesButton() throws IOException {
        loadContent("/org/example/ibb_ecodation_javafx/views/admin-note-view.fxml");
    }

    // Contact butonuna tıklama işlemi
    @FXML
    private void handleBackupButton() throws IOException {
        loadContent("/org/example/ibb_ecodation_javafx/views/admin-backup-view.fxml");
        showTrayNotification("Yedeklerinizi kayıt etmeyi unutmayın.", "IBB ve Ecodation Bootcamp Projesi");
    }

    // Contact butonuna tıklama işlemi
    @FXML
    private void handleConfigButton() throws IOException {
        loadContent("/org/example/ibb_ecodation_javafx/views/admin-configs-view.fxml");
        showTrayNotification("Ayarları kayıt etmeyi unutmayın.", "IBB ve Ecodation Bootcamp Projesi");
    }

    // İçeriği dinamik olarak yükleme fonksiyonu
    private void loadContent(String fxmlFile) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
        StackPane newContent = loader.load();
        contentArea.getChildren().setAll(newContent);
    }

    private void setAvatarImageSource() {
        try {
            shadcnAvatar.setImage(AdminDashboardController.class.getResource("/org/example/ibb_ecodation_javafx/assets/avatar.jpg"));
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }
}
