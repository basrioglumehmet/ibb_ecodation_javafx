package org.example.ibb_ecodation_javafx.ui.button;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.effect.DropShadow;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXML;
import org.example.ibb_ecodation_javafx.statemanagement.Store;
import org.example.ibb_ecodation_javafx.statemanagement.state.DarkModeState;
import javafx.application.Platform;

import static org.example.ibb_ecodation_javafx.utils.FontAwesomeUtil.getGlyphIcon;

public class ShadcnButton extends Button {
    public enum ButtonType {
        GHOST,
        PRIMARY, SECONDARY, DESTRUCTIVE,SUCCESS
    }

    private final StringProperty type = new SimpleStringProperty("PRIMARY");
    private final BooleanProperty fullWidth = new SimpleBooleanProperty(false);
    private final StringProperty glyphIconName = new SimpleStringProperty("USER");
    private final BooleanProperty isIconOnly = new SimpleBooleanProperty(false);
    private final Store store = Store.getInstance();
    private boolean isLightMode = false;


    private final CompositeDisposable disposables = new CompositeDisposable();

    public ShadcnButton() {
        super();
    }

    public ShadcnButton(String text, ButtonType type, String glyphIconName, Boolean fullWidth, Boolean isIconOnly) {
        super(text);
        setPrefWidth(120);
        this.type.set(type.name());
        this.glyphIconName.set(glyphIconName);
        this.fullWidth.set(fullWidth);
        this.isIconOnly.set(isIconOnly);
        initializeStyle(type);
        updateWidth();
    }

    public void dispose() {
        if (!disposables.isDisposed()) {
            disposables.dispose();
        }
    }

    public StringProperty typeProperty() {
        return type;
    }

    public StringProperty glyphIconNameProperty() {
        return glyphIconName;
    }

    public BooleanProperty fullWidthProperty() {
        return fullWidth;
    }

    public BooleanProperty isIconOnlyProperty() {
        return isIconOnly;
    }

    @FXML
    public void setFullWidth(boolean fullWidth) {
        this.fullWidth.set(fullWidth);
        updateWidth();
    }

    @FXML
    public void setIsIconOnly(boolean isIconOnly) {
        this.isIconOnly.set(isIconOnly);
        updateWidth();
    }

    public boolean isFullWidth() {
        return fullWidth.get();
    }

    public boolean isIconOnly() {
        return isIconOnly.get();
    }

    @FXML
    public void setType(String type) {
        try {
            ButtonType buttonType = ButtonType.valueOf(type.toUpperCase());
            this.type.set(type.toUpperCase());
            initializeStyle(buttonType);
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    public String getType() {
        return type.get();
    }

    @FXML
    public void setGlyphIconName(String glyphIconName) {
        this.glyphIconName.set(glyphIconName);
        FontAwesomeIconView iconView = getGlyphIcon(this.glyphIconName); // Get the updated icon
        setGraphic(iconView); // Update the button's icon graphic
    }

    @FXML
    public String getGlyphIconName() {
        return glyphIconName.get();
    }



    private void initializeStyle(ButtonType type) {
        Disposable stateSubscription = store.getState().subscribe(stateRegistry -> {
            boolean darkModeValue = stateRegistry.getState(DarkModeState.class).isEnabled();

            //  GUI in the GUI thread Güncelle
            Platform.runLater(() -> {
                if (darkModeValue != isLightMode) {
                    isLightMode = darkModeValue;
                    updateButtonStyle(type);
                }
            });
        });

        disposables.add(stateSubscription);

        updateButtonStyle(type);
    }

    private void updateButtonStyle(ButtonType type) {
        String baseStyle = "-fx-background-radius: 8px; " +
                "-fx-padding: 10px 20px; " +
                "-fx-focus-color: transparent; " +
                "-fx-faint-focus-color: transparent;";

        String backgroundColor;
        String hoverColor;
        String textColor, hoverTextColor = "white";

        switch (type) {
            case PRIMARY:
                backgroundColor = "#3b82f6";
                hoverColor = "#3576df";
                textColor = "white";
                break;
            case GHOST:
                backgroundColor = "transparent";
                hoverColor = "#3b82f6";
                textColor = isLightMode ? "black" : "white";
                hoverTextColor = isLightMode ? "white" : "white";
                break;
            case SECONDARY:
                backgroundColor = "#27272a";
                hoverColor = "#212124";
                textColor = "white";
                break;
            case DESTRUCTIVE:
                backgroundColor = "#D32F2F";
                hoverColor = "#B71C1C";
                textColor = "white";
                break;
            case SUCCESS:
                backgroundColor = "#c1e411";
                hoverColor = "#8ba808";
                textColor = isLightMode ? "black" : "black";
                hoverTextColor = isLightMode ? "black" : "black";
                break;
            default:
                backgroundColor = "#1E88E5";
                textColor = "white";
                hoverColor = "#1E3A8A";
        }

        setFont(Font.font("Arial", 16));
        setStyle("-fx-background-color: " + backgroundColor + "; " + "-fx-text-fill: " + textColor + "; " + baseStyle);

        FontAwesomeIconView iconView = getGlyphIcon(this.glyphIconName);
        iconView.setFill(Paint.valueOf(textColor));

        String finalHoverTextColor = hoverTextColor;
        setOnMouseEntered(e -> {
            setStyle("-fx-background-color: " + hoverColor + "; " + "-fx-text-fill: " + finalHoverTextColor + "; " + baseStyle);
            setCursor(Cursor.HAND);
            iconView.setFill(Paint.valueOf(finalHoverTextColor));
        });

        setOnMouseExited(e -> {
            setCursor(Cursor.DEFAULT);
            setStyle("-fx-background-color: " + backgroundColor + "; " + "-fx-text-fill: " + textColor + "; " + baseStyle);
            iconView.setFill(Paint.valueOf(textColor));
        });

        setGraphic(iconView);
        setContentDisplay(ContentDisplay.LEFT);
        setAlignment(Pos.CENTER_LEFT);
        setGraphicTextGap(10);

        updateWidth();
    }

    private void updateWidth() {
        if (fullWidth.get()) {
            setMaxWidth(Double.MAX_VALUE);
        } else {
            setMaxWidth(isIconOnly.get() ? 30: getPrefWidth());
        }
    }

    // Button yok olduğunda bu metod çağrılır
    @FXML
    public void onDestroy() {
        dispose();  // Abonelikleri temizle
    }
}
