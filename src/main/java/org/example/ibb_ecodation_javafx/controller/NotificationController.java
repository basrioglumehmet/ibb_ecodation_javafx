package org.example.ibb_ecodation_javafx.controller;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollBar;
import javafx.scene.layout.VBox;
import org.example.ibb_ecodation_javafx.ui.listItem.ShadcnListItem;

import static org.example.ibb_ecodation_javafx.utils.LabelUtil.updateLabelStyles;

public class NotificationController {

    @FXML
    private ListView<ShadcnListItem> notificationList;


    public void initialize() {
        // Dummy veriler
        for (int i = 0; i < 20; i++) {
            ShadcnListItem item = new ShadcnListItem(
                    ShadcnListItem.ListItemType.WITH_ICON,
                    "Lütfen Başlık Giriniz",
                    "Lütfen açıklama Giriniz",
                    "SUCCESS"
            );
            notificationList.getItems().add(item);
        }

        // Hücreleri render et
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

        // ListView ve alt arka planlar
        notificationList.setStyle("-fx-background-color: transparent;");

        // ScrollBar'ı erişmek için küçük gecikme (GUI render edilene kadar bekliyoruz)
        Platform.runLater(() -> {
            for (Node node : notificationList.lookupAll(".scroll-bar")) {
                if (node instanceof ScrollBar scrollBar) {
                    scrollBar.setStyle("""
                                -fx-background-color: transparent;
                                -fx-background-insets: 0;
                                -fx-background-radius: 5px;
                            """);

                    // Thumb’ı da saydam yapmak için alt node'ları bulabiliriz ama genelde style yukarıdan geçer
                }
            }
        });
    }
}
