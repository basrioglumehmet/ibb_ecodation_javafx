package org.example.ibb_ecodation_javafx.ui.listItem;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import io.reactivex.rxjava3.disposables.Disposable;
import org.example.ibb_ecodation_javafx.statemanagement.Store;
import org.example.ibb_ecodation_javafx.statemanagement.state.DarkModeState;
import org.example.ibb_ecodation_javafx.ui.button.ShadcnSwitchButton;
import org.example.ibb_ecodation_javafx.ui.combobox.ShadcnComboBox;

public class ShadcnListItem extends HBox {

    public enum ListItemType {
        NORMAL,
        WITH_SWITCH,
        WITH_LANGUAGE_OPTION
    }

    private final ObjectProperty<ListItemType> type = new SimpleObjectProperty<>(ListItemType.NORMAL);
    private final StringProperty headerText = new SimpleStringProperty("Lütfen Başlık Giriniz");
    private final StringProperty descriptionText = new SimpleStringProperty("Lütfen açıklama Giriniz");
    private ShadcnSwitchButton switchButton;
    private Disposable switchButtonSubscription;
    private final Store store = Store.getInstance();
    private boolean isLightMode;
    public ShadcnListItem() {
        this(ListItemType.NORMAL,"Lütfen Başlık Giriniz","Lütfen açıklama Giriniz");
    }

    public ShadcnListItem(ListItemType type, String headerText, String descriptionText) {
        this.type.set(type);
        this.headerText.set(headerText);
        this.descriptionText.set(descriptionText);
        initializeStyle(type);
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
        this.setStyle(String.format("-fx-border-color: %s;",isLightMode ? "#f2f2f3":"#303030") +
                " -fx-border-radius: 8px; -fx-padding: 10px;");
    }
    private void initializeStyle(ListItemType type) {
        // Default style for all types
        //Store'a ait ilk değerini al

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

                // Initialize subscription to the switch button's state changes
                subscribeToSwitchButtonState();
                break;

            case WITH_LANGUAGE_OPTION:
                Region spacer2 = new Region();
                HBox.setHgrow(spacer2, Priority.ALWAYS);

                ShadcnComboBox languageComboBox = new ShadcnComboBox();

                HBox rightContainerLanguage = new HBox(languageComboBox);
                rightContainerLanguage.setAlignment(Pos.CENTER_RIGHT);
                rightContainerLanguage.setSpacing(10);

                this.getChildren().addAll(spacer2, rightContainerLanguage);
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
    // Subscribe to the switch button's state changes
    private void subscribeToSwitchButtonState() {
        if (switchButton != null) {
            switchButtonSubscription = switchButton.watchIsActive().subscribe(value -> {
                System.out.println("Switch state changed: " + value);
                // Handle switch state changes, like updating the store or UI
            });
        }
    }

    // Memory leak prevention: Dispose of subscription when not needed
    public void dispose() {
        if (switchButtonSubscription != null && !switchButtonSubscription.isDisposed()) {
            switchButtonSubscription.dispose();
        }
    }
}
