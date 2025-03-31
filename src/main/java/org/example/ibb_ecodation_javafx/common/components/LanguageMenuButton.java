package org.example.ibb_ecodation_javafx.common.components;

import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.control.Label;
import java.util.Map;

public class LanguageMenuButton extends MenuButton {

    public LanguageMenuButton() {
        setStyle("-fx-background-color: transparent; -fx-background-radius: 9999px;");

        loadFlagAndLabel();

        loadLanguages();
    }

    private void loadFlagAndLabel() {
        // Bayrak ve "Dil" metnini koyan bir HBox oluştur
        HBox header = new HBox();
        header.setAlignment(javafx.geometry.Pos.CENTER_LEFT);

        // Türkiye bayrağını ekle (örneğin "tr.png")
        ImageView flag = new ImageView(new Image(getClass().getResourceAsStream("/org/example/ibb_ecodation_javafx/assets/flags/tr.png")));
        flag.setFitWidth(34);
        flag.setFitHeight(26);

        // "Dil" metnini ekle
//        Label label = new Label("Arayüz Dili");
//        label.getStyleClass().add("label"); // Burada .label stil sınıfını ekledik
//        label.setStyle("-fx-font-weight: bold");

        // Bayrak ve Label'i HBox'a ekle
        header.getChildren().addAll(flag);

        // MenuButton içindeki grafik olarak header'ı ekle
        setGraphic(header);
    }

    private void loadLanguages() {
        Map<String, String[]> languages = Map.of(
                "Türkçe", new String[]{"tr", "tr.png"},
                "English", new String[]{"en", "en.png"}
        );

        languages.forEach((language, data) -> {
            String countryCode = data[0];
            String flagPath = data[1];

            MenuItem item = createMenuItem(language, countryCode, flagPath);
            getItems().add(item);
        });
    }
    private MenuItem createMenuItem(String language, String countryCode, String flagPath) {
        MenuItem item = new MenuItem();
        HBox content = new HBox(10);

        content.setAlignment(javafx.geometry.Pos.CENTER_LEFT);

        ImageView flag = new ImageView(new Image(getClass().getResourceAsStream("/org/example/ibb_ecodation_javafx/assets/flags/" + flagPath)));
        flag.setFitWidth(32);
        flag.setFitHeight(24);

        // Label oluşturuluyor
        Label label = new Label(language + " (" + countryCode.toUpperCase() + ")");
        label.setStyle("-fx-font-weight: bold;");  // Label stilini belirledik

        // MenuItem içerisine flag ve label'ı ekliyoruz
        content.getChildren().addAll(flag, label);
        item.setGraphic(content);


        return item;
    }

}
