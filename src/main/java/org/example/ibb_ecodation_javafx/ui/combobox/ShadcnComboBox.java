package org.example.ibb_ecodation_javafx.ui.combobox;

import io.reactivex.rxjava3.subjects.PublishSubject;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.geometry.Pos;
import javafx.util.Pair;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class ShadcnComboBox<T> extends Button {

    private  final PublishSubject<Pair<String,T>> subject = PublishSubject.create();
    private final Map<String, T> itemMap = new HashMap<>();
    private String selectedKey = "";
    private T selectedValue = null;
    private final Function<T, String> displayConverter;
    private String title = "OPERATIONS";
    private Label titleLabel;
    public ShadcnComboBox(Function<T, String> displayConverter) {
        this.displayConverter = displayConverter;
        setStyle("-fx-background-color: #222225;" +
                "-fx-background-radius: 6px;" +
                "-fx-border-radius: 6px;   " +
                "-fx-border-color: #2e2e2e; " +
                "-fx-border-width:1px;   " +
                "-fx-text-fill: #fff;");

        ContextMenu menu = new ContextMenu();

        menu.setStyle("-fx-background-color: #222225;" +
                "-fx-background-radius: 6px;" +
                "-fx-border-radius: 6px;   " +
                "-fx-border-color: #2e2e2e; " +
                "-fx-border-width:1px;   " +
                "-fx-padding:5;"+
                "-fx-text-fill: #fff;");
        setContextMenu(menu);
        loadLabel();
        setOnMouseClicked(event -> {
            if (event.getButton() == javafx.scene.input.MouseButton.PRIMARY) {
                double screenX = this.localToScreen(this.getLayoutX(), this.getLayoutY()).getX();
                double screenY = this.localToScreen(this.getLayoutX(), this.getLayoutY()).getY();
                menu.show(this, screenX, screenY + getHeight());
            }
        });
    }

    public void addItem(String key, T value) {
        itemMap.put(key, value);
        refreshMenuItems();
    }

    public void setTitle(String newTitle) {
        this.title = newTitle;
        if (titleLabel != null) {
            titleLabel.setText(newTitle);
        } else {
            loadLabel();
        }
    }
    public void setItems(Map<String, T> items) {
        this.itemMap.clear();
        this.itemMap.putAll(items);
        refreshMenuItems();
    }

    public void removeItem(String key) {
        itemMap.remove(key);
        if (key.equals(selectedKey)) {
            selectedKey = "";
            selectedValue = null;
            loadLabel();
        }
        refreshMenuItems();
    }
    private void refreshMenuItems() {
        ContextMenu menu = getContextMenu();
        menu.getItems().clear();
        itemMap.forEach((key, value) -> {
            menu.getItems().add(createMenuItem(key, value));
        });
    }


    private void loadLabel() {
        HBox header = new HBox(5);
        header.setAlignment(Pos.CENTER_LEFT);

        titleLabel = new Label(title);
        titleLabel.setStyle("-fx-text-fill:white; -fx-font-weight:bold;");

        Label selectionLabel = new Label(selectedValue != null ? displayConverter.apply(selectedValue) : "Select an option");
        selectionLabel.setStyle("-fx-text-fill:white;");

        header.getChildren().addAll(titleLabel, selectionLabel);
        setGraphic(header);

    }


    private MenuItem createMenuItem(String key, T value) {
        HBox content = new HBox(10);
        content.setAlignment(Pos.CENTER_LEFT);
        content.setStyle("-fx-padding: 5; -fx-background-radius: 3px;");

        // Sabit genişlik ver (örneğin 160 piksel)
        content.setPrefWidth(160);

        Label label = new Label(displayConverter.apply(value));
        label.setStyle("-fx-text-fill:white;");
        content.getChildren().addAll(label);

        CustomMenuItem item = new CustomMenuItem(content);
        item.setHideOnClick(true);
        item.getStyleClass().clear();
        item.setStyle("-fx-background-color: transparent;");

        // Hover efekti
        content.setOnMouseEntered(e -> content.setStyle("-fx-background-color: #f27a1a; -fx-padding: 5; -fx-background-radius: 3px;"));
        content.setOnMouseExited(e -> content.setStyle("-fx-background-color: transparent; -fx-padding: 5; -fx-background-radius: 3px;"));

        item.setOnAction(event -> publish(key,value));
        return item;
    }


    public void publish(String key, T value) {

        subject.onNext(new Pair<>(key, value));
    }

    public PublishSubject<Pair<String, T>> watchSelection() {
        return subject;
    }


}