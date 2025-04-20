package org.example.ibb_ecodation_javafx.ui.input;

import io.reactivex.rxjava3.disposables.Disposable;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.VBox;
import org.example.ibb_ecodation_javafx.statemanagement.Store;
import org.example.ibb_ecodation_javafx.statemanagement.state.DarkModeState;
import org.example.ibb_ecodation_javafx.ui.ValidatableComponent;

import static org.example.ibb_ecodation_javafx.utils.GuiAnimationUtil.runOpacityAnimation;

public class ShadcnInput extends VBox implements ValidatableComponent {

    private final StringProperty header = new SimpleStringProperty("");
    private final StringProperty error = new SimpleStringProperty("");
    private final StringProperty text = new SimpleStringProperty("");
    private final BooleanProperty password = new SimpleBooleanProperty(false);

    private final Label headerLabel = new Label();
    private final Label errorLabel = new Label();
    private TextField textField;
    private final Tooltip errorTooltip = new Tooltip();

    private final Store store = Store.getInstance();
    private Disposable disposable;
    private TextChangeListener textChangeListener;

    private static final String FONT_FAMILY = "Poppins";
    private static final String HEADER_STYLE_BASE = "-fx-font-family: '" + FONT_FAMILY + "'; -fx-font-size: 13px; -fx-font-weight: 500;";
    private static final String TEXTFIELD_STYLE_BASE = "-fx-font-family: '" + FONT_FAMILY + "'; -fx-font-size: 14px; -fx-padding: 6 10 6 10; -fx-background-radius: 4px; -fx-border-radius: 4px; -fx-border-width: 1px; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 3, 0, 0, 1);";
    private static final String ERROR_STYLE = "-fx-font-family: '" + FONT_FAMILY + "'; -fx-font-size: 11px; -fx-text-fill: #FF5555; -fx-font-weight: 400;";
    private static final String LIGHT_MODE = "-fx-background-color: #f2f2f3; -fx-border-color: #e4e4e7; -fx-text-fill: #1C2526;";
    private static final String LIGHT_MODE_FOCUS = "-fx-border-color: #8dd80a; -fx-background-color: #FFFFFF;";
    private static final String DARK_MODE = "-fx-background-color: #2c2c30; -fx-border-color: #2b2b30; -fx-text-fill: #FFFFFF;";
    private static final String DARK_MODE_FOCUS = "-fx-border-color:#8dd80a; -fx-background-color: #38383c;";
    private static final String DARK_MODE_HOVER = "-fx-background-color: #343438;";
    private static final String ERROR_OUTLINE = "-fx-border-color: #FF5555;";

    public interface TextChangeListener {
        void onTextChanged(String newValue);
    }

    public ShadcnInput() {
        this("", false);
    }

    public ShadcnInput(String headerText) {
        this(headerText, false);
    }

    public ShadcnInput(String headerText, boolean isPassword) {
        super(6);
        setHeader(headerText);
        setPassword(isPassword);
        textField = isPassword ? new PasswordField() : new TextField();
        initializeUI();
        setupBindings();
    }

    private void initializeUI() {
        setMaxWidth(Double.MAX_VALUE);
        setMinWidth(200);

        headerLabel.textProperty().bind(header);
        updateHeaderStyle(false);

        textField.setPrefHeight(36);
        textField.textProperty().bindBidirectional(text);
        updateTextFieldStyle(false, false);

        errorLabel.setStyle(ERROR_STYLE);
        errorLabel.textProperty().bind(error);
        errorLabel.setVisible(false);
        errorTooltip.textProperty().bind(error);
        errorLabel.setTooltip(errorTooltip);

        updateChildren();

        disposable = store.getState().subscribe(stateRegistry -> {
            boolean isDarkMode = store.getCurrentState(DarkModeState.class).isEnabled();
            updateStyles(isDarkMode);
        });

        updateStyles(store.getCurrentState(DarkModeState.class).isEnabled());
    }

    private void updateStyles(boolean isDarkMode) {
        updateHeaderStyle(isDarkMode);
        updateTextFieldStyle(isDarkMode, !error.get().isEmpty());
    }

    private void updateHeaderStyle(boolean isDarkMode) {
        String color = isDarkMode ? "-fx-text-fill: #FFFFFF;" : "-fx-text-fill: #1C2526;";
        headerLabel.setStyle(HEADER_STYLE_BASE + color);
    }

    private void updateTextFieldStyle(boolean isDarkMode, boolean hasError) {
        String baseStyle = TEXTFIELD_STYLE_BASE + (isDarkMode ? DARK_MODE : LIGHT_MODE);
        if (hasError) {
            baseStyle += ERROR_OUTLINE;
        }
        textField.setStyle(baseStyle);

        textField.focusedProperty().removeListener((obs, old, newVal) -> {});
        textField.focusedProperty().addListener((obs, wasFocused, isFocused) -> {
            String style = TEXTFIELD_STYLE_BASE + (isDarkMode ? DARK_MODE : LIGHT_MODE);
            if (isFocused) {
                style += (isDarkMode ? DARK_MODE_FOCUS : LIGHT_MODE_FOCUS);
            } else if (hasError) {
                style += ERROR_OUTLINE;
            }
            textField.setStyle(style);
        });

        if (isDarkMode) {
            textField.setOnMouseEntered(e -> {
                if (!textField.isFocused() && !hasError) {
                    textField.setStyle(TEXTFIELD_STYLE_BASE + DARK_MODE + DARK_MODE_HOVER);
                }
            });
            textField.setOnMouseExited(e -> {
                if (!textField.isFocused() && !hasError) {
                    textField.setStyle(TEXTFIELD_STYLE_BASE + DARK_MODE);
                }
            });
        }
    }

    private void updateChildren() {
        getChildren().clear();
        if (header.get() != null && !header.get().isEmpty()) {
            getChildren().add(headerLabel);
        }
        getChildren().add(textField);
        if (error.get() != null && !error.get().isEmpty()) {
            getChildren().add(errorLabel);
            errorLabel.setVisible(true);
            runOpacityAnimation(errorLabel);
        } else {
            errorLabel.setVisible(false);
        }
        adjustHeight();
    }

    private void adjustHeight() {
        double totalHeight = textField.getPrefHeight() + getSpacing();
        if (header.get() != null && !header.get().isEmpty()) {
            totalHeight += 18 + getSpacing();
        }
        if (error.get() != null && !error.get().isEmpty()) {
            totalHeight += 14 + getSpacing();
        }
        setPrefHeight(totalHeight);
    }

    private void setupBindings() {
        header.addListener((obs, old, newVal) -> updateChildren());
        error.addListener((obs, old, newVal) -> {
            boolean isDarkMode = store.getCurrentState(DarkModeState.class).isEnabled();
            updateTextFieldStyle(isDarkMode, !newVal.isEmpty());
            updateChildren();
        });
        textField.textProperty().addListener((obs, old, newVal) -> {
            if (textChangeListener != null) {
                textChangeListener.onTextChanged(newVal);
            }
            if (!error.get().isEmpty()) {
                clearError();
            }
        });
        password.addListener((obs, oldVal, newVal) -> {
            replaceTextField(newVal);
        });
    }

    private void replaceTextField(boolean isPassword) {
        String currentText = textField.getText();
        TextField newField = isPassword ? new PasswordField() : new TextField();
        newField.setPrefHeight(36);
        newField.textProperty().bindBidirectional(text);
        newField.setStyle(textField.getStyle());
        newField.setOnMouseEntered(textField.getOnMouseEntered());
        newField.setOnMouseExited(textField.getOnMouseExited());
        textField.textProperty().removeListener((obs, old, newVal) -> {});
        textField.focusedProperty().removeListener((obs, old, newVal) -> {});
        getChildren().remove(textField);
        getChildren().add(getChildren().indexOf(headerLabel) + 1, newField);
        newField.setText(currentText);
        textField = newField;
        setupBindings();
        updateTextFieldStyle(store.getCurrentState(DarkModeState.class).isEnabled(), !error.get().isEmpty());
    }

    public void setTextChangeListener(TextChangeListener listener) {
        this.textChangeListener = listener;
    }

    public StringProperty headerProperty() {
        return header;
    }

    public String getHeader() {
        return header.get();
    }

    public void setHeader(String headerText) {
        this.header.set(headerText);
    }

    public String getText() {
        return text.get();
    }

    public void setText(String value) {
        text.set(value);
    }

    public String getErrorText() {
        return error.get();
    }

    @Override
    public void setError(String errorText) {
        error.set(errorText);
    }

    @Override
    public void clearError() {
        error.set("");
    }

    public TextField getTextField() {
        return textField;
    }

    public BooleanProperty passwordProperty() {
        return password;
    }

    public boolean isPassword() {
        return password.get();
    }

    public void setPassword(boolean value) {
        password.set(value);
    }

    public void dispose() {
        if (disposable != null && !disposable.isDisposed()) {
            disposable.dispose();
        }
    }
}