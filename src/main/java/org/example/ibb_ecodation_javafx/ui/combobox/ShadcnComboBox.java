package org.example.ibb_ecodation_javafx.ui.combobox;

import io.reactivex.rxjava3.subjects.PublishSubject;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.geometry.Pos;
import javafx.util.Pair;
import org.example.ibb_ecodation_javafx.statemanagement.Store;
import org.example.ibb_ecodation_javafx.statemanagement.state.DarkModeState;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import static org.example.ibb_ecodation_javafx.utils.ThemeUtil.*;

public class ShadcnComboBox<T> extends Button {

    private final PublishSubject<Pair<String, T>> subject = PublishSubject.create();
    private final Map<String, T> itemMap = new HashMap<>();
    private String selectedKey = "";
    private T selectedValue = null;
    private final Function<T, String> displayConverter;
    private String title = "OPERATIONS";
    private Label titleLabel;
    private final Store store = Store.getInstance();
    private final ContextMenu menu;

    public ShadcnComboBox(Function<T, String> displayConverter) {
        this.displayConverter = displayConverter;
        this.menu = new ContextMenu();

        // Initial button styling
        setStyle("-fx-background-radius: 3px;");
        setContextMenu(menu);
        loadLabel();

        // Button click to show menu
        setOnMouseClicked(event -> {
            if (event.getButton() == javafx.scene.input.MouseButton.PRIMARY) {
                double screenX = this.localToScreen(this.getLayoutX(), this.getLayoutY()).getX();
                double screenY = this.localToScreen(this.getLayoutX(), this.getLayoutY()).getY();
                menu.show(this, screenX, screenY + getHeight());
            }
        });


        store.getState().subscribe(stateRegistry -> {
            boolean isDarkMode = stateRegistry.getState(DarkModeState.class).isEnabled();
            updateDarkModeStyles(isDarkMode);
        });


        updateDarkModeStyles(store.getCurrentState(DarkModeState.class).isEnabled());
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
        menu.getItems().clear();
        boolean isDarkMode = store.getCurrentState(DarkModeState.class).isEnabled();
        itemMap.forEach((key, value) -> menu.getItems().add(createMenuItem(key, value, isDarkMode)));
    }

    private void loadLabel() {
        HBox header = new HBox(5);
        header.setAlignment(Pos.CENTER_LEFT);

        titleLabel = new Label(title);
        titleLabel.setStyle("-fx-font-weight: bold;");
        boolean isDarkMode = store.getCurrentState(DarkModeState.class).isEnabled();

        changeTextColorPrimary(isDarkMode, titleLabel);

        header.getChildren().add(titleLabel);
        setGraphic(header);
    }

    private MenuItem createMenuItem(String key, T value, boolean isDarkMode) {
        HBox content = new HBox(10);
        content.setAlignment(Pos.CENTER_LEFT);
        content.setStyle("-fx-padding: 5; -fx-background-radius: 3px;");
        content.setPrefWidth(160);

        Label label = new Label(displayConverter.apply(value));
        changeTextColor(isDarkMode, label);

        content.getChildren().add(label);

        CustomMenuItem item = new CustomMenuItem(content);
        item.setHideOnClick(true);
        item.getStyleClass().clear();

        // Hover effects
        content.setOnMouseEntered(e -> {
            content.setStyle("-fx-background-color: #8dd80a; -fx-padding: 5; -fx-background-radius: 3px;");
            label.setStyle("-fx-text-fill: #000;");
        });
        content.setOnMouseExited(e -> {
            content.setStyle("-fx-background-color: transparent; -fx-padding: 5; -fx-background-radius: 3px;");
            changeTextColor(isDarkMode, label);
        });

        item.setOnAction(event -> publish(key, value));

        store.getState().subscribe(stateRegistry -> {
            boolean darkMode = stateRegistry.getState(DarkModeState.class).isEnabled();
            changeTextColor(darkMode, label);
            content.setStyle("-fx-background-color: transparent; -fx-padding: 5; -fx-background-radius: 3px;");
        });

        return item;
    }

    public void publish(String key, T value) {
        subject.onNext(new Pair<>(key, value));
    }

    public PublishSubject<Pair<String, T>> watchSelection() {
        return subject;
    }

    private void updateDarkModeStyles(boolean isDarkMode) {

        if (titleLabel != null) {
            changeTextColorPrimary(isDarkMode, titleLabel);
        }

        changeContextMenuBackground(isDarkMode, menu);

        refreshMenuItems();
    }
}