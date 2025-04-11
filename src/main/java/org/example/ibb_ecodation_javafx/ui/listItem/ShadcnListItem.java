package org.example.ibb_ecodation_javafx.ui.listItem;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import io.reactivex.rxjava3.disposables.Disposable;
import javafx.scene.paint.Paint;
import org.example.ibb_ecodation_javafx.core.context.SpringContext;
import org.example.ibb_ecodation_javafx.core.service.LanguageService;
import org.example.ibb_ecodation_javafx.statemanagement.Store;
import org.example.ibb_ecodation_javafx.statemanagement.state.DarkModeState;
import org.example.ibb_ecodation_javafx.ui.button.ShadcnSwitchButton;
import org.example.ibb_ecodation_javafx.ui.combobox.ShadcnLanguageComboBox;

import static org.example.ibb_ecodation_javafx.utils.FontAwesomeUtil.getGlyphIcon;

public class ShadcnListItem extends HBox {

    public enum ListItemType {
        NORMAL, WITH_SWITCH, WITH_LANGUAGE_OPTION, WITH_ICON
    }

    private final ObjectProperty<ListItemType> type = new SimpleObjectProperty<>(ListItemType.NORMAL);
    private final StringProperty headerKey = new SimpleStringProperty("default.header");
    private final StringProperty descriptionKey = new SimpleStringProperty("default.description");
    private final StringProperty glyphIconName = new SimpleStringProperty("USER");
    private LanguageService languageService;
    private String languageCode = ShadcnLanguageComboBox.getCurrentLanguageCode(); // Default
    private ShadcnSwitchButton switchButton;
    private Disposable switchButtonSubscription;
    private Disposable languageSubscription;
    private final Store store = Store.getInstance();
    private boolean isLightMode;
    private Label headerLabel;
    private Label detailLabel;

    public ShadcnListItem() {
        this.languageService = SpringContext.getContext().getBean(LanguageService.class);

        setContainerStyle();
        initializeStyle(type.get());
    }

    public ShadcnListItem(LanguageService languageService, String languageCode, ListItemType type, String headerKey, String descriptionKey, String glyphIconName) {
        this.languageService = languageService;
        this.languageCode = languageCode;
        languageService.loadAll(languageCode);
        this.type.set(type);
        this.headerKey.set(headerKey);
        this.descriptionKey.set(descriptionKey);
        this.glyphIconName.set(glyphIconName);
        setContainerStyle();
        initializeStyle(type);
    }

    public StringProperty glyphIconNameProperty() { return glyphIconName; }

    @FXML public void setGlyphIconName(String glyphIconName) { this.glyphIconName.set(glyphIconName); }

    @FXML public String getGlyphIconName() { return glyphIconName.get(); }

    @FXML public void setDescriptionText(String description) {
        this.descriptionKey.set(description);
        if (detailLabel != null) {
            detailLabel.setText(description); // Set directly without translation
        }
    }

    public String getDescriptionText() { return descriptionKey.get(); } // Return raw value

    public ObjectProperty<ListItemType> typeProperty() { return type; }

    public ShadcnSwitchButton getSwitchButton() { return switchButton; }

    public void setType(ListItemType type) {
        this.type.set(type);
        initializeStyle(type);
    }

    @FXML public void setHeaderText(String header) {
        this.headerKey.set(header);
        if (headerLabel != null) {
            headerLabel.setText(header); // Set directly without translation
        }
    }

    public String getHeaderText() { return headerKey.get(); } // Return raw value

    public ListItemType getType() { return type.get(); }

    private void setContainerStyle() {
        this.setStyle(String.format("-fx-background-color: %s;", isLightMode ? "#f2f2f3" : "#202024") +
                " -fx-background-radius: 8px; -fx-padding: 10px;");
    }

    private void initializeStyle(ListItemType type) {
        if (languageService == null) {
            languageService = SpringContext.getContext().getBean(LanguageService.class);
            languageService.loadAll(languageCode);
        }
        isLightMode = store.getCurrentState(DarkModeState.class).isEnabled();
        // Translate only during initialization
        headerLabel = new Label(headerKey.get());
        detailLabel = new Label(descriptionKey.get());

        store.getState().subscribe(stateRegistry -> {
            isLightMode = stateRegistry.getState(DarkModeState.class).isEnabled();
            updateTextStyles(headerLabel, detailLabel);
            setContainerStyle();
        });

        VBox leftContent;
        if (this.getChildren().isEmpty()) {
            leftContent = new VBox();
            this.getChildren().add(leftContent);
        } else {
            leftContent = (VBox) this.getChildren().get(0);
            leftContent.getChildren().clear();
        }
        leftContent.getChildren().addAll(headerLabel, detailLabel);

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
                rightContainer.setSpacing(10);
                this.getChildren().addAll(spacer, rightContainer);
                break;

            case WITH_ICON:
                Region spacer2 = new Region();
                HBox.setHgrow(spacer2, Priority.ALWAYS);
                FontAwesomeIconView iconView = getGlyphIcon(this.glyphIconName);
                iconView.setGlyphSize(40);
                iconView.setFill(Paint.valueOf("white"));
                StackPane iconWrapper = new StackPane(iconView);
                HBox rightContainer2 = new HBox(iconWrapper);
                rightContainer2.setAlignment(Pos.CENTER_RIGHT);
                rightContainer2.setSpacing(10);
                this.getChildren().addAll(spacer2, rightContainer2);
                break;

            case WITH_LANGUAGE_OPTION:
                Region spacer3 = new Region();
                HBox.setHgrow(spacer3, Priority.ALWAYS);
                ShadcnLanguageComboBox languageComboBox = new ShadcnLanguageComboBox();
                HBox rightContainerLanguage = new HBox(languageComboBox);
                rightContainerLanguage.setAlignment(Pos.CENTER_RIGHT);
                rightContainerLanguage.setSpacing(10);
                this.getChildren().addAll(spacer3, rightContainerLanguage);

                if (languageSubscription != null && !languageSubscription.isDisposed()) {
                    languageSubscription.dispose();
                }
                languageSubscription = languageComboBox.watchLanguageValue().subscribe(pair -> {
                    this.languageCode = pair.getKey();
                    languageService.loadAll(this.languageCode);

                });
                break;
        }
    }

    private void updateTextStyles(Label header, Label detail) {
        if (header != null && detail != null) {
            header.setText(headerKey.get()); // Use raw value
            header.setStyle("-fx-font-weight: bold; " +
                    String.format("-fx-text-fill: %s;", isLightMode ? "black" : "white") +
                    " -fx-font-size: 16px;");
            detail.setText(descriptionKey.get()); // Use raw value
            detail.setStyle(String.format("-fx-text-fill: %s;", isLightMode ? "black" : "white") +
                    " -fx-font-size: 14px;");
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