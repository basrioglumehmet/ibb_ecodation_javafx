package org.example.ibb_ecodation_javafx.controller;

import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollBar;
import javafx.fxml.FXML;
import lombok.RequiredArgsConstructor;
import org.example.ibb_ecodation_javafx.core.context.SpringContext;
import org.example.ibb_ecodation_javafx.core.service.LanguageService;
import org.example.ibb_ecodation_javafx.model.UserNotification;
import org.example.ibb_ecodation_javafx.service.UserNotificationService;
import org.example.ibb_ecodation_javafx.statemanagement.Store;
import org.example.ibb_ecodation_javafx.statemanagement.state.DarkModeState;
import org.example.ibb_ecodation_javafx.statemanagement.state.UserState;
import org.example.ibb_ecodation_javafx.ui.combobox.ShadcnLanguageComboBox;
import org.example.ibb_ecodation_javafx.ui.listItem.ShadcnListItem;
import io.reactivex.rxjava3.disposables.Disposable;
import org.springframework.stereotype.Controller;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class NotificationController {

    @FXML private ListView<ShadcnListItem> notificationList;
    @FXML private Label notificationsLabel;

    private final UserNotificationService userNotificationService;
    private final LanguageService languageService;
    private String languageCode;
    private List<Disposable> subscriptions = new ArrayList<>();
    private Store store = Store.getInstance();



    public void initialize() {

        languageCode = ShadcnLanguageComboBox.getCurrentLanguageCode();


        updateUIWithLanguage();


        loadNotifications();


        notificationList.setCellFactory(lv -> {
            ListCell<ShadcnListItem> cell = new ListCell<>() {
                @Override
                protected void updateItem(ShadcnListItem item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setGraphic(null);
                    } else {
                        setGraphic(item);
                    }
                }
            };
            cell.setStyle("-fx-background-color: transparent;");
            return cell;
        });

        // Style ListView
        notificationList.setStyle("-fx-background-color: transparent;");

        // Style ScrollBar
        Platform.runLater(() -> {
            for (Node node : notificationList.lookupAll(".scroll-bar")) {
                if (node instanceof ScrollBar scrollBar) {
                    scrollBar.setStyle("""
                            -fx-background-color: transparent;
                            -fx-background-insets: 0;
                            -fx-background-radius: 5px;
                        """);
                }
            }
        });


    }

    private void loadNotifications() {
        notificationList.getItems().clear();
        var userDetail = store.getCurrentState(UserState.class).getUserDetail();
        List<UserNotification> data = userNotificationService.findAllById(userDetail.getUserId());
        for (UserNotification notification : data) {

            ShadcnListItem item = new ShadcnListItem(
                    languageService,
                    languageCode,
                    ShadcnListItem.ListItemType.WITH_ICON,
                    notification.getHeader(),
                    notification.getDescription(),
                    notification.getType()
            );
            notificationList.getItems().add(item);
        }
    }

    private void updateUIWithLanguage() {
        notificationsLabel.setText(languageService.translate("label.notifications"));
        notificationsLabel.setStyle(
                "-fx-font-size:24;"+
                        String.format("-fx-text-fill:%s;",
                                store.getCurrentState(DarkModeState.class).isEnabled() ?"white":"black"));

    }

    public void dispose() {
        subscriptions.forEach(sub -> {
            if (!sub.isDisposed()) sub.dispose();
        });
        subscriptions.clear();
    }

    @FXML
    public void onDestroy() {
        dispose();
    }
}