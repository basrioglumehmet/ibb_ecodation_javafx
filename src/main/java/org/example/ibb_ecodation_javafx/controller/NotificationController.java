package org.example.ibb_ecodation_javafx.controller;

import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollBar;
import javafx.fxml.FXML;
import org.example.ibb_ecodation_javafx.core.context.SpringContext;
import org.example.ibb_ecodation_javafx.core.service.LanguageService;
import org.example.ibb_ecodation_javafx.model.UserNotification;
import org.example.ibb_ecodation_javafx.service.UserNotificationService;
import org.example.ibb_ecodation_javafx.ui.listItem.ShadcnListItem;

import java.util.List;

import static org.example.ibb_ecodation_javafx.utils.LabelUtil.updateLabelStyles;

public class NotificationController {

    @FXML
    private ListView<ShadcnListItem> notificationList;
    @FXML
    private Label notificationsLabel;

    private final UserNotificationService userNotificationService;
    private final LanguageService languageService;

    public NotificationController() {
        userNotificationService = SpringContext.getContext().getBean(UserNotificationService.class);
        languageService = SpringContext.getContext().getBean(LanguageService.class);
    }

    public void initialize() {
        // Load language resources (e.g., "tr" for Turkish, "en" for English)
        String languageCode = "en";
        languageService.loadAll(languageCode);

        // Set translated label text
        notificationsLabel.setText(languageService.translate("label.notifications"));
        updateLabelStyles(notificationsLabel, languageCode); // Assuming LabelUtil supports language-specific styles

        // Fetch notifications (assuming user ID 1 for now)
        List<UserNotification> data = userNotificationService.readAll(1);
        for (UserNotification notification : data) {
            ShadcnListItem item = new ShadcnListItem(
                    ShadcnListItem.ListItemType.WITH_ICON,
                    notification.getHeader(),
                    notification.getDescription(),
                    notification.getType()
            );
            notificationList.getItems().add(item);
        }

        // Cell factory for rendering list items
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
}