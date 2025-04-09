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
import org.example.ibb_ecodation_javafx.statemanagement.Store;
import org.example.ibb_ecodation_javafx.statemanagement.state.DarkModeState;
import org.example.ibb_ecodation_javafx.ui.button.ShadcnSwitchButton;
import org.example.ibb_ecodation_javafx.ui.combobox.ShadcnComboBox;
import org.example.ibb_ecodation_javafx.ui.combobox.ShadcnLanguageComboBox;

import static org.example.ibb_ecodation_javafx.utils.FontAwesomeUtil.getGlyphIcon;

public class ShadcnListItem extends HBox {

    public enum ListItemType {
        NORMAL,
        WITH_SWITCH,
        WITH_LANGUAGE_OPTION,
        WITH_ICON
    }

    private final ObjectProperty<ListItemType> type = new SimpleObjectProperty<>(ListItemType.NORMAL);
    private final StringProperty headerText = new SimpleStringProperty("Lütfen Başlık Giriniz");
    private final StringProperty descriptionText = new SimpleStringProperty("Lütfen açıklama Giriniz");
    private final StringProperty glyphIconName = new SimpleStringProperty("USER");
    private ShadcnSwitchButton switchButton;
    private Disposable switchButtonSubscription;
    private final Store store = Store.getInstance();
    private boolean isLightMode;
    public ShadcnListItem() {
        this(ListItemType.NORMAL,"Lütfen Başlık Giriniz","Lütfen açıklama Giriniz","USER");
    }

    public ShadcnListItem(ListItemType type, String headerText, String descriptionText,String glyphIconName) {
        this.type.set(type);
        this.headerText.set(headerText);
        this.descriptionText.set(descriptionText);
        this.glyphIconName.set(glyphIconName);
        setContainerStyle();
        initializeStyle(type);
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
    public void setDescriptionText(String descriptionText) {
        this.descriptionText.set(descriptionText);
    }

    public String getDescriptionText() {
        return descriptionText.get();
    }

    public ObjectProperty<ListItemType> typeProperty() {
        return type;
    }

    public StringProperty headerTextProperty() {
        return headerText;
    }

    public ShadcnSwitchButton getSwitchButton(){
        return switchButton;
    }

    public void setType(ListItemType type) {
        this.type.set(type);
        initializeStyle(type);
    }

    public void setHeaderText(String headerText) {
        this.headerText.set(headerText);
    }

    public String getHeaderText() {
        return headerText.get();
    }

    public ListItemType getType() {
        return type.get();
    }
    private void setContainerStyle(){
        this.setStyle(String.format("-fx-background-color: %s;",isLightMode ? "#f2f2f3":"#202024") +
                " -fx-background-radius: 8px; -fx-padding: 10px;");
    }
    private void initializeStyle(ListItemType type) {

        Label header = new Label(headerText.get());
        Label detail = new Label(descriptionText.get());
        isLightMode = store.getCurrentState(DarkModeState.class).isEnabled();
        //Store'a abone ol.
        store.getState().subscribe(stateRegistry -> {
            isLightMode = stateRegistry.getState(DarkModeState.class).isEnabled();
            updateTextStyles(header,detail);
            setContainerStyle();
        });
        VBox leftContent;
        if (this.getChildren().isEmpty()) {
            leftContent = new VBox();
            this.getChildren().add(leftContent);
        } else {
            leftContent = (VBox) this.getChildren().get(0);
        }

        leftContent.getChildren().clear();
        leftContent.getChildren().addAll(header, detail);


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
                break;
        }
    }
    private void updateTextStyles(Label header,Label detail){
        header.setStyle("-fx-font-weight: bold; " +
                String.format("-fx-text-fill: %s;",isLightMode ? "black":"white") +
                " -fx-font-size: 16px;");
        detail.setStyle( String.format("-fx-text-fill: %s;",isLightMode ? "black":"white") +
                " -fx-font-size: 14px;");
    }


    // Memory leak
    public void dispose() {
        if (switchButtonSubscription != null && !switchButtonSubscription.isDisposed()) {
            switchButtonSubscription.dispose();
        }
    }
}
