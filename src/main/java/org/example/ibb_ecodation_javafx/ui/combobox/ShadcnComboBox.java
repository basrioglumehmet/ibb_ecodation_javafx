package org.example.ibb_ecodation_javafx.ui.combobox;

import io.reactivex.rxjava3.subjects.PublishSubject;
import javafx.application.Platform;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.geometry.Pos;
import javafx.util.Pair;

import java.io.InputStream;
import java.util.Map;

public class ShadcnComboBox extends Button {

    private static final PublishSubject<Pair<String,String>> subject = PublishSubject.create();

    public ShadcnComboBox() {
        setStyle("-fx-background-color: black;" +
                "-fx-background-radius: 6px;" +
                "-fx-border-radius: 6px;   " +
                "-fx-border-color: #2e2e2e; " +
                "-fx-border-width:1px;   " +
                "-fx-text-fill: #fff;");

        ContextMenu menu = new ContextMenu();
        menu.  setStyle("-fx-background-color: black;" +
                "-fx-background-radius: 6px;" +
                "-fx-border-radius: 6px;   " +
                "-fx-border-color: #2e2e2e; " +
                "-fx-border-width:1px;   " +
                "-fx-padding:5;"+
                "-fx-text-fill: #fff;");
        setContextMenu(menu);
        loadFlagAndLabel();
        loadLanguages();
        setOnMouseClicked(event -> {
            if (event.getButton() == javafx.scene.input.MouseButton.PRIMARY) {
                // Get the position of the button in screen coordinates
                double screenX = this.localToScreen(this.getLayoutX(), this.getLayoutY()).getX();
                double screenY = this.localToScreen(this.getLayoutX(), this.getLayoutY()).getY();

                // Open the context menu below the button
                menu.show(this, screenX, screenY + getHeight());
            }
        });
    }

    private void loadFlagAndLabel() {
        HBox header = new HBox(5);
        header.setAlignment(Pos.CENTER_LEFT);

        ImageView flag = createImageView("tr.png", 34, 26);
        Label label = new Label("Türkçe");
        label.setStyle("-fx-text-fill:white;");
        header.getChildren().addAll(flag, label);
        setGraphic(header);

        watchLanguageValue().subscribe(pair -> {
            Image newFlag = loadImage(pair.getKey() + ".png");
            if (newFlag != null) {
                flag.setImage(newFlag);
                label.setText(pair.getValue());
            }
        });

    }

    private void loadLanguages() {
        Map<String, String[]> languages = Map.of(
                "Türkçe", new String[]{"tr", "tr.png"},
                "English", new String[]{"en", "en.png"}
        );

        ContextMenu menu = getContextMenu();  // Get the ContextMenu associated with the MenuButton
        languages.forEach((language, data) -> {
            menu.getItems().add(createMenuItem(language, data[0], data[1]));  // Add items to the ContextMenu
        });
    }

    private MenuItem createMenuItem(String language, String countryCode, String flagPath) {
        MenuItem item = new MenuItem();
        HBox content = new HBox(10);
        content.setAlignment(Pos.CENTER_LEFT);

        ImageView flag = createImageView(flagPath, 32, 24);
        Label label = new Label(language + " (" + countryCode.toUpperCase() + ")");

        content.getChildren().addAll(flag, label);
        item.setGraphic(content);
        item.setOnAction(event -> publish(countryCode,language));

        return item;
    }

    private ImageView createImageView(String fileName, int width, int height) {
        Image image = loadImage(fileName);
        ImageView imageView = new ImageView(image);
        imageView.setFitWidth(width);
        imageView.setFitHeight(height);
        return imageView;
    }

    private Image loadImage(String fileName) {
        String path = "/org/example/ibb_ecodation_javafx/assets/flags/" + fileName;
        InputStream stream = getClass().getResourceAsStream(path);
        return (stream != null) ? new Image(stream) : null;
    }

    public void publish(String code,String value) {
        subject.onNext(new Pair<>(code,value));
    }

    public static synchronized  PublishSubject<Pair<String,String>> watchLanguageValue() {
        return subject;
    }
}