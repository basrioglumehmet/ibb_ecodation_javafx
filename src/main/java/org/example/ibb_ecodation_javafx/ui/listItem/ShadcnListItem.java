package org.example.ibb_ecodation_javafx.ui.listItem;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import io.reactivex.rxjava3.disposables.Disposable;
import javafx.scene.paint.Color;
import org.example.ibb_ecodation_javafx.core.service.LanguageService;
import org.example.ibb_ecodation_javafx.statemanagement.Store;
import org.example.ibb_ecodation_javafx.statemanagement.state.DarkModeState;
import org.example.ibb_ecodation_javafx.ui.button.ShadcnSwitchButton;
import org.example.ibb_ecodation_javafx.ui.combobox.ShadcnLanguageComboBox;

import static org.example.ibb_ecodation_javafx.utils.FontAwesomeUtil.getColor;
import static org.example.ibb_ecodation_javafx.utils.FontAwesomeUtil.getGlyphIcon;

public class ShadcnListItem extends HBox {

    public enum ListItemType {
        NORMAL, WITH_SWITCH, WITH_LANGUAGE_OPTION, WITH_ICON
    }

    private final ObjectProperty<ListItemType> type = new SimpleObjectProperty<>(ListItemType.NORMAL);
    private final StringProperty headerKey = new SimpleStringProperty("default.header");
    private final StringProperty descriptionKey = new SimpleStringProperty("default.description");
    private final StringProperty glyphIconName = new SimpleStringProperty("USER");
    private String languageCode = ShadcnLanguageComboBox.getCurrentLanguageCode();
    private ShadcnSwitchButton switchButton;
    private Disposable switchButtonSubscription;
    private Disposable languageSubscription;
    private final Store store = Store.getInstance();
    private boolean isDarkMode;
    private Label headerLabel;
    private Label detailLabel;
    private ShadcnLanguageComboBox languageComboBox;
    private String baseBackground;
    private String baseBorderColor;
    private String baseTextColor;

    public ShadcnListItem() {
        initializeUI();
    }

    public ShadcnListItem(String languageCode, ListItemType type, String headerKey, String descriptionKey, String glyphIconName) {
        this.languageCode = languageCode;
        this.type.set(type);
        this.headerKey.set(headerKey);
        this.descriptionKey.set(descriptionKey);
        this.glyphIconName.set(glyphIconName);
        initializeUI();
    }

    private void initializeUI() {
        isDarkMode = store.getCurrentState(DarkModeState.class).isEnabled();
        baseBackground = !isDarkMode ? "#f2f2f3" : "#202024";
        baseBorderColor = !isDarkMode ? "#e4e4e7" : "#2c2c30";
        baseTextColor = !isDarkMode ? "black" : "white";

        setContainerStyle();
        setSpacing(15);
        setAlignment(Pos.CENTER_LEFT);
        initializeStyle(type.get());

        store.getState().subscribe(stateRegistry -> {
            isDarkMode = stateRegistry.getState(DarkModeState.class).isEnabled();
            baseBackground = !isDarkMode ? "#f2f2f3" : "#202024";
            baseBorderColor = !isDarkMode ? "#e4e4e7" : "#2c2c30";
            baseTextColor = !isDarkMode ? "black" : "white";
            updateTextStyles(headerLabel, detailLabel);
            setContainerStyle();
        });

        type.addListener((obs, oldType, newType) -> initializeStyle(newType));

        setOnMouseEntered(e -> this.setStyle("-fx-background-color: " + (!isDarkMode ? "#e8e8e8" : "#2c2c30") +
                "; -fx-background-radius: 8; -fx-border-radius: 8; -fx-border-width: 1; -fx-border-color: " + baseBorderColor +
                "; -fx-padding: 12; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 4, 0, 0, 2);"));
        setOnMouseExited(e -> setContainerStyle());
    }

    private void setContainerStyle() {
        this.setStyle("-fx-background-color: " + baseBackground +
                "; -fx-background-radius: 8; -fx-border-radius: 8; -fx-border-width: 1; -fx-border-color: " + baseBorderColor +
                "; -fx-padding: 12; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.05), 2, 0, 0, 1);");
    }

    private void initializeStyle(ListItemType type) {
        headerLabel = new Label(headerKey.get());
        detailLabel = new Label(descriptionKey.get());
        updateTextStyles(headerLabel, detailLabel);

        VBox leftContent;
        if (this.getChildren().isEmpty()) {
            leftContent = new VBox(8);
            this.getChildren().add(leftContent);
        } else {
            leftContent = (VBox) this.getChildren().get(0);
            leftContent.getChildren().clear();
        }
        leftContent.getChildren().addAll(headerLabel, detailLabel);
        leftContent.setAlignment(Pos.CENTER_LEFT);

        if (this.getChildren().size() > 1) {
            this.getChildren().remove(1, this.getChildren().size());
        }

        switch (type) {
            case NORMAL:
                break;

            case WITH_SWITCH:
                Region spacer = new Region();
                HBox.setHgrow(spacer, Priority.ALWAYS);
                switchButton = new ShadcnSwitchButton();
                HBox rightContainer = new HBox(switchButton);
                rightContainer.setAlignment(Pos.CENTER_RIGHT);
                rightContainer.setPadding(new Insets(0, 5, 0, 0));
                this.getChildren().addAll(spacer, rightContainer);
                break;

            case WITH_ICON:
                Region spacer2 = new Region();
                HBox.setHgrow(spacer2, Priority.ALWAYS);
                FontAwesomeIconView iconView = getGlyphIcon(this.glyphIconName.get());
                iconView.setGlyphSize(24);
                iconView.setFill(Color.web(!isDarkMode ? getColor(this.glyphIconName.get()) : "white"));
                StackPane iconWrapper = new StackPane(iconView);
                iconWrapper.setPadding(new Insets(5));
                HBox rightContainer2 = new HBox(iconWrapper);
                rightContainer2.setAlignment(Pos.CENTER_RIGHT);
                this.getChildren().addAll(spacer2, rightContainer2);
                break;

            case WITH_LANGUAGE_OPTION:
                Region spacer3 = new Region();
                HBox.setHgrow(spacer3, Priority.ALWAYS);
                languageComboBox = new ShadcnLanguageComboBox();
                HBox rightContainerLanguage = new HBox(languageComboBox);
                rightContainerLanguage.setAlignment(Pos.CENTER_RIGHT);
                rightContainerLanguage.setPadding(new Insets(0, 5, 0, 0));
                this.getChildren().addAll(spacer3, rightContainerLanguage);

                if (languageSubscription != null && !languageSubscription.isDisposed()) {
                    languageSubscription.dispose();
                }
                languageSubscription = languageComboBox.watchLanguageValue().subscribe(pair -> {
                    this.languageCode = pair.getKey();
                    headerLabel.setText(headerKey.get());
                    detailLabel.setText(descriptionKey.get());
                });
                break;
        }
    }

    private void updateTextStyles(Label header, Label detail) {
        if (header != null && detail != null) {
            header.setText(headerKey.get());
            header.setStyle("-fx-font-weight: bold; -fx-font-family: 'Poppins'; -fx-text-fill: " + baseTextColor +
                    "; -fx-font-size: 16;");
            detail.setText(descriptionKey.get());
            detail.setStyle("-fx-font-family: 'Poppins'; -fx-text-fill: " + baseTextColor +
                    "; -fx-font-size: 13;");
        }
    }

    public StringProperty glyphIconNameProperty() {
        return glyphIconName;
    }

    @FXML
    public void setGlyphIconName(String glyphIconName) {
        this.glyphIconName.set(glyphIconName);
    }

    @FXML
    public String getGlyphIconName() {
        return glyphIconName.get();
    }

    @FXML
    public void setDescriptionText(String description) {
        this.descriptionKey.set(description);
        if (detailLabel != null) {
            detailLabel.setText(description);
        }
    }

    public String getDescriptionText() {
        return descriptionKey.get();
    }

    public ObjectProperty<ListItemType> typeProperty() {
        return type;
    }

    @FXML
    public void setType(String type) {
        this.type.set(ListItemType.valueOf(type));
    }

    @FXML
    public void setType(ListItemType type) {
        this.type.set(type);
    }

    public ListItemType getType() {
        return type.get();
    }

    public ShadcnSwitchButton getSwitchButton() {
        return switchButton;
    }

    @FXML
    public void setHeaderText(String header) {
        this.headerKey.set(header);
        if (headerLabel != null) {
            headerLabel.setText(header);
        }
    }

    public String getHeaderText() {
        return headerKey.get();
    }

    public void resetLanguage() {
        if (type.get() == ListItemType.WITH_LANGUAGE_OPTION && languageComboBox != null) {
            languageComboBox.resetLanguage();
        }
    }

    public void dispose() {
        if (switchButtonSubscription != null && !switchButtonSubscription.isDisposed()) {
            switchButtonSubscription.dispose();
        }
        if (languageSubscription != null && !languageSubscription.isDisposed()) {
            languageSubscription.dispose();
        }
    }
}