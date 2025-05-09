package org.example.ibb_ecodation_javafx.ui.button;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXML;
import org.example.ibb_ecodation_javafx.statemanagement.Store;
import org.example.ibb_ecodation_javafx.statemanagement.state.DarkModeState;
import javafx.application.Platform;
import org.example.ibb_ecodation_javafx.ui.spinner.LoadingSpinner;
import org.example.ibb_ecodation_javafx.utils.GuiAnimationUtil;
import javafx.scene.layout.Region;

import static org.example.ibb_ecodation_javafx.utils.FontAwesomeUtil.getGlyphIcon;
import static org.example.ibb_ecodation_javafx.utils.GuiAnimationUtil.runAnimationForNode;

public class ShadcnButton extends Button {
    public enum ButtonType {
        GHOST, PRIMARY, SECONDARY, DESTRUCTIVE, SUCCESS
    }

    private final StringProperty type = new SimpleStringProperty("PRIMARY");
    private final BooleanProperty fullWidth = new SimpleBooleanProperty(false);
    private final StringProperty glyphIconName = new SimpleStringProperty("");
    private final BooleanProperty isIconOnly = new SimpleBooleanProperty(false);
    private final StringProperty align = new SimpleStringProperty("CENTER");
    private final Store store = Store.getInstance();
    private boolean isDarkMode = false;
    private final CompositeDisposable disposables = new CompositeDisposable();
    private final BooleanProperty isLoading = new SimpleBooleanProperty(false);
    private LoadingSpinner loadingSpinner;
    private String originalText;
    private FontAwesomeIconView originalIcon;

    public ShadcnButton() {
        super();
        initializeLoadingBehavior();
        initializeBindings();
    }

    public ShadcnButton(String text, ButtonType type, String glyphIconName, Boolean fullWidth, Boolean isIconOnly, String align) {
        super(text);
        setPrefWidth(120);
        this.type.set(type.name());
        this.glyphIconName.set(glyphIconName);
        this.fullWidth.set(fullWidth);
        this.isIconOnly.set(isIconOnly);
        this.align.set(align.toUpperCase());
        this.originalText = text;
        initializeStyle(type);
        initializeLoadingBehavior();
        initializeBindings();
    }

    private void initializeBindings() {
        // Add listener for fullWidth and isIconOnly properties
        fullWidth.addListener((obs, oldVal, newVal) -> updateWidth());
        isIconOnly.addListener((obs, oldVal, newVal) -> updateWidth());

        // Bind width to parent when fullWidth is true, once parent is available
        parentProperty().addListener((obs, oldParent, newParent) -> {
            if (newParent != null) {
                updateWidth(); // Reapply width settings when parent changes
            } else {
                // Unbind if parent is removed
                prefWidthProperty().unbind();
                updateWidth();
            }
        });
    }

    public void dispose() {
        if (!disposables.isDisposed()) {
            disposables.dispose();
        }
    }

    // Property getters and setters remain the same...
    public StringProperty typeProperty() { return type; }
    public StringProperty glyphIconNameProperty() { return glyphIconName; }
    public BooleanProperty fullWidthProperty() { return fullWidth; }
    public BooleanProperty isIconOnlyProperty() { return isIconOnly; }
    public StringProperty alignProperty() { return align; }
    public BooleanProperty isLoadingProperty() { return isLoading; }

    @FXML
    public void setFullWidth(boolean fullWidth) {
        this.fullWidth.set(fullWidth);
    }

    @FXML
    public void setIsLoading(boolean loading) {
        this.isLoading.set(loading);
    }

    public boolean getIsLoading() { return this.isLoading.get(); }

    @FXML
    public void setIsIconOnly(boolean isIconOnly) {
        this.isIconOnly.set(isIconOnly);
    }

    public boolean isFullWidth() { return fullWidth.get(); }
    public boolean isIconOnly() { return isIconOnly.get(); }

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

    public String getType() { return type.get(); }

    @FXML
    public void setGlyphIconName(String glyphIconName) {
        this.glyphIconName.set(glyphIconName);
        FontAwesomeIconView iconView = getGlyphIcon(this.glyphIconName.get());
        setGraphic(iconView);
    }

    public String getGlyphIconName() { return glyphIconName.get(); }

    @FXML
    public void setAlign(String align) {
        this.align.set(align.toUpperCase());
        updateButtonStyle(ButtonType.valueOf(getType()));
    }

    public String getAlign() { return align.get(); }

    private void initializeStyle(ButtonType type) {
        Disposable stateSubscription = store.getState().subscribe(stateRegistry -> {
            boolean darkModeValue = stateRegistry.getState(DarkModeState.class).isEnabled();
            Platform.runLater(() -> {
                if (darkModeValue != isDarkMode) {
                    isDarkMode = darkModeValue;
                    updateButtonStyle(type);
                }
            });
        });
        disposables.add(stateSubscription);
        updateButtonStyle(type);
    }

    private void initializeLoadingBehavior() {
        loadingSpinner = new LoadingSpinner(20, Color.BLACK, Color.BLACK);
        isLoading.addListener((obs, oldValue, newValue) -> {
            Platform.runLater(() -> {
                if (newValue) {
                    originalText = getText();
                    originalIcon = (FontAwesomeIconView) getGraphic();
                    setText("");
                    setGraphic(loadingSpinner);
                    setDisable(true);
                } else {
                    setText(originalText);
                    setGraphic(originalIcon);
                    setDisable(false);
                }
            });
        });
    }

    private void updateButtonStyle(ButtonType type) {
        String baseStyle = "-fx-background-radius: 8px; " +
                "-fx-padding: 10px 20px; " +
                "-fx-focus-color: transparent; " +
                "-fx-faint-focus-color: transparent; -fx-font-family: 'Poppins'; -fx-font-size: 16px; -fx-font-weight: bold; ";

        String backgroundColor, hoverColor, textColor, hoverTextColor = "white";

        switch (type) {
            case PRIMARY:
                backgroundColor = "#8dd80a";
                hoverColor = "#6cad03";
                textColor = "black";
                break;
            case GHOST:
                backgroundColor = "transparent";
                hoverColor = "#8dd80a";
                textColor = !isDarkMode ? "#82838b" : "#fff";
                hoverTextColor = "black";
                break;
            case SECONDARY:
                backgroundColor = "#0f35f0";
                hoverColor = "#1c45ff";
                textColor = "#fff";
                hoverTextColor = "#fff";
                break;
            case DESTRUCTIVE:
                backgroundColor = "#ed4245";
                hoverColor = "#c03537";
                textColor = "white";
                break;
            case SUCCESS:
                backgroundColor = "#8dd80a";
                hoverColor = "#6cad03";
                textColor = "black";
                hoverTextColor = "black";
                break;
            default:
                backgroundColor = "#1E88E5";
                textColor = "white";
                hoverColor = "#1E3A8A";
        }

        setStyle("-fx-background-color: " + backgroundColor + "; " + "-fx-text-fill: " + textColor + "; " + baseStyle);
        setWrapText(true);

        FontAwesomeIconView iconView = !glyphIconName.get().isEmpty() ? getGlyphIcon(this.glyphIconName.get()) : null;
        if (iconView != null) {
            iconView.setFill(Paint.valueOf(textColor));
        }

        String finalHoverTextColor = hoverTextColor;
        setOnMouseEntered(e -> {
            runAnimationForNode(this, GuiAnimationUtil.HoverType.HOVERING);
            setStyle("-fx-background-color: " + hoverColor + "; " + "-fx-text-fill: " + finalHoverTextColor + "; " + baseStyle);
            setCursor(Cursor.HAND);
            if (iconView != null) {
                iconView.setFill(Paint.valueOf(finalHoverTextColor));
            }
        });

        setOnMouseExited(e -> {
            setCursor(Cursor.DEFAULT);
            runAnimationForNode(this, GuiAnimationUtil.HoverType.EXIT);
            setStyle("-fx-background-color: " + backgroundColor + "; " + "-fx-text-fill: " + textColor + "; " + baseStyle);
            if (iconView != null) {
                iconView.setFill(Paint.valueOf(textColor));
            }
        });

        if (!isLoading.get()) {
            setGraphic(iconView);
            setContentDisplay(ContentDisplay.LEFT);
        }

        switch (align.get().toUpperCase()) {
            case "LEFT": setAlignment(Pos.CENTER_LEFT); break;
            case "RIGHT": setAlignment(Pos.CENTER_RIGHT); break;
            case "CENTER": default: setAlignment(Pos.CENTER); break;
        }

        setGraphicTextGap(10);
        updateWidth();
    }

    private void updateWidth() {
        if (fullWidth.get()) {
            setMinWidth(0);
            setMaxWidth(Double.MAX_VALUE);
            Parent parent = getParent();
            if (parent instanceof Region) {
                Region regionParent = (Region) parent;
                prefWidthProperty().bind(regionParent.widthProperty());
            } else {

                setPrefWidth(USE_COMPUTED_SIZE);
            }
        } else {
            prefWidthProperty().unbind();
            setMinWidth(USE_COMPUTED_SIZE);
            setMaxWidth(isIconOnly.get() ? 30 : Double.MAX_VALUE);
            setPrefWidth(isIconOnly.get() ? 30 : USE_COMPUTED_SIZE);
        }
    }

    @FXML
    public void onDestroy() {
        dispose();
    }
}