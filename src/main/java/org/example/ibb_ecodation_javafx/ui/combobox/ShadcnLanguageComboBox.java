package org.example.ibb_ecodation_javafx.ui.combobox;

import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.subjects.PublishSubject;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.geometry.Pos;
import javafx.util.Pair;
import org.example.ibb_ecodation_javafx.statemanagement.Store;
import org.example.ibb_ecodation_javafx.statemanagement.state.DarkModeState;

import java.io.InputStream;
import java.util.Map;

import static org.example.ibb_ecodation_javafx.utils.ThemeUtil.*;

public class ShadcnLanguageComboBox extends Button {

    private static final PublishSubject<Pair<String, String>> subject = PublishSubject.create();
    private static String currentLanguageCode = "en";
    private static final Map<String, String[]> languages = Map.of(
            "Türkçe", new String[]{"tr", "tr.png"},
            "English", new String[]{"en", "en.png"}
    );

    private final Store store = Store.getInstance();
    private Label label;
    private ImageView flag;
    private boolean isDarkMode;
    private Disposable disposable;
    private final ContextMenu menu = new ContextMenu();

    public ShadcnLanguageComboBox() {
        initializeStyles();
        setContextMenu(menu);

        // Initialize dark mode state
        isDarkMode = store.getCurrentState(DarkModeState.class).isEnabled();

        loadFlagAndLabel();
        loadLanguages();
        updateDarkModeStyles(isDarkMode);

        setupClickHandler();
        setupDarkModeSubscription();
    }

    private void initializeStyles() {
        setStyle("-fx-background-radius: 6px; -fx-border-radius: 6px;");
    }

    private void updateDarkModeStyles(boolean isDarkMode) {
        // Button styling with primary background
        changeLanguageComboBoxBackground(isDarkMode, this); // #f27a1a (light) or #2c2c30 (dark)
        if (label != null) {
            changeContextMenuTextColor(isDarkMode, label); // Always #fff for consistency
        }

        // ContextMenu styling
        changeContextMenuBackground(isDarkMode, menu); // #fbfbfb (light) or #121214 (dark)

        // Update menu items
        menu.getItems().forEach(item -> {
            HBox content = (HBox) ((CustomMenuItem) item).getContent();
            Label itemLabel = (Label) content.getChildren().get(1);
            changeTextColor(isDarkMode, itemLabel); // #000 (light) or #fff (dark)
            content.setStyle("-fx-padding: 5; -fx-background-radius: 3px; -fx-border-radius: 3px; -fx-background-color: transparent;");
        });
    }

    private void loadFlagAndLabel() {
        HBox header = new HBox(5);
        header.setAlignment(Pos.CENTER_LEFT);

        String initialLanguage = languages.entrySet().stream()
                .filter(entry -> entry.getValue()[0].equals(currentLanguageCode))
                .map(Map.Entry::getKey)
                .findFirst()
                .orElse("English");

        String initialCode = languages.get(initialLanguage)[0];
        flag = createImageView(languages.get(initialLanguage)[1], 34, 26);
        label = new Label(initialLanguage);
        changeContextMenuTextColor(isDarkMode, label); // Always #fff

        header.getChildren().addAll(flag, label);
        setGraphic(header);

        watchLanguageValue().subscribe(pair -> {
            Image newFlag = loadImage(pair.getKey() + ".png");
            if (newFlag != null) {
                flag.setImage(newFlag);
                label.setText(pair.getValue());
                currentLanguageCode = pair.getKey();
                changeContextMenuTextColor(isDarkMode, label); // Ensure text color stays consistent
            }
        });
    }

    private void setupClickHandler() {
        setOnMouseClicked(event -> {
            if (event.getButton() == javafx.scene.input.MouseButton.PRIMARY) {
                double screenX = this.localToScreen(0, 0).getX();
                double screenY = this.localToScreen(0, 0).getY() + this.getHeight();
                menu.show(this, screenX, screenY);
            }
        });
    }

    private void loadLanguages() {
        menu.getItems().clear();
        languages.forEach((language, data) -> {
            menu.getItems().add(createMenuItem(language, data[0], data[1]));
        });
    }

    private MenuItem createMenuItem(String language, String countryCode, String flagPath) {
        HBox content = new HBox(10);
        content.setAlignment(Pos.CENTER_LEFT);
        content.setStyle("-fx-padding: 5; -fx-background-radius: 3px; -fx-border-radius: 3px; -fx-background-color: transparent;");
        content.setPrefWidth(160);

        ImageView flag = createImageView(flagPath, 32, 24);
        Label label = new Label(language + " (" + countryCode.toUpperCase() + ")");
        changeTextColor(isDarkMode, label); // #000 (light) or #fff (dark)

        content.getChildren().addAll(flag, label);

        CustomMenuItem item = new CustomMenuItem(content);
        item.setHideOnClick(true);
        item.getStyleClass().clear();

        // Hover effects
        content.setOnMouseEntered(e -> {
            content.setStyle("-fx-background-color: #f27a1a; -fx-padding: 5; -fx-border-radius: 3px; -fx-background-radius: 3px;");
            label.setStyle("-fx-text-fill: #fff;"); // White text on hover
        });
        content.setOnMouseExited(e -> {
            content.setStyle("-fx-background-color: transparent; -fx-padding: 5; -fx-border-radius: 3px; -fx-background-radius: 3px;");
            changeTextColor(isDarkMode, label); // Revert to default
        });

        item.setOnAction(event -> publish(countryCode, language));
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

    private void setupDarkModeSubscription() {
        disposable = store.getState().subscribe(stateRegistry -> {
            boolean newDarkMode = stateRegistry.getState(DarkModeState.class).isEnabled();
            if (isDarkMode != newDarkMode) {
                isDarkMode = newDarkMode;
                updateDarkModeStyles(isDarkMode);
            }
        });
    }

    public void publish(String code, String value) {
        subject.onNext(new Pair<>(code, value));
    }

    public static synchronized PublishSubject<Pair<String, String>> watchLanguageValue() {
        return subject;
    }

    public static synchronized String getCurrentLanguageCode() {
        return currentLanguageCode != null ? currentLanguageCode : "en";
    }

    public void dispose() {
        if (disposable != null && !disposable.isDisposed()) {
            disposable.dispose();
        }
    }
}