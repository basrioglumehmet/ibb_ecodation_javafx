package org.example.ibb_ecodation_javafx.ui.input;

import io.reactivex.rxjava3.disposables.Disposable;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import org.example.ibb_ecodation_javafx.statemanagement.Store;
import org.example.ibb_ecodation_javafx.statemanagement.state.DarkModeState;

import static org.example.ibb_ecodation_javafx.utils.GuiAnimationUtil.runOpacityAnimation;

public class ShadcnInput extends VBox {

    private final StringProperty header = new SimpleStringProperty(null);
    private final Label headerLabel = new Label();
    private final Label errorLabel = new Label();
    private final TextField textField = new TextField();
    private final Store store = Store.getInstance();
    private boolean isLightMode;
    private final String BASE_HEADER_STYLE = "-fx-font-weight: bold; -fx-font-size: 14px; " +
            "-fx-font-family: 'Poppins';";
    private Disposable disposable;
    private TextChangeListener textChangeListener;

    // Callback interface for external usage
    public interface TextChangeListener {
        void onTextChanged(String newValue);
    }

    public ShadcnInput() {
        this(null);
    }

    public ShadcnInput(String headerText) {
        super(5); // Spacing 0, elemanlar arasında boşluk yok
        this.header.set(headerText);
        initializeUI();
        setupBindings();
    }

    private void initializeUI() {
        dispose();
        setFillWidth(true);
        setMaxWidth(Double.MAX_VALUE);

        // Header Label ayarları
        headerLabel.setStyle(BASE_HEADER_STYLE + String.format("-fx-text-fill: %s;", isLightMode ? "black" : "white"));
        if (header.get() != null) {
            headerLabel.setText(header.get());
        }

        // Error Label ayarları
        errorLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: red; -fx-font-family: 'Poppins';");
        errorLabel.setVisible(false);

        // TextField ayarları
        textField.setPrefWidth(getPrefWidth());
        textField.setPrefHeight(30); // Sabit yükseklik tanımlıyoruz
        textField.setStyle("-fx-background-color: white; -fx-font-family: 'Poppins'; -fx-font-size: 16px;");

        // Çocuk elemanları ekle
        updateChildren();

        // Store'dan tema değişikliğini dinle
        disposable = store.getState().subscribe(stateRegistry -> {
            isLightMode = stateRegistry.getState(DarkModeState.class).isEnabled();
            updateUI();
        });

        // Yüksekliği içeriğe göre ayarla
        adjustHeight();
    }

    private void updateChildren() {
        getChildren().clear();
        if (header.get() != null && !header.get().isEmpty()) {
            getChildren().add(headerLabel);
        }
        getChildren().add(textField);
        if (errorLabel.isVisible()) {
            getChildren().add(errorLabel);
        }
    }

    private void adjustHeight() {
        // VBox yüksekliğini içeriğe göre ayarla
        double totalHeight = 0;
        if (header.get() != null && !header.get().isEmpty()) {
            totalHeight += headerLabel.prefHeight(-1); // Header'ın tahmini yüksekliği
        }
        totalHeight += textField.getPrefHeight(); // TextField yüksekliği
        if (errorLabel.isVisible()) {
            totalHeight += errorLabel.prefHeight(-1); // ErrorLabel yüksekliği
        }
        setPrefHeight(totalHeight);
    }

    public void updateUI() {
        if (header.get() != null && !header.get().isEmpty()) {
            headerLabel.setStyle(BASE_HEADER_STYLE + String.format("-fx-text-fill: %s;", isLightMode ? "black" : "white"));
        }
        textField.setStyle(
                String.format("-fx-background-color: %s;", isLightMode ? "white" : "white") +
                        "-fx-background-radius: 6px; -fx-border-radius: 6px; -fx-font-size: 16px; " +
                        "-fx-border-width: 1; -fx-border-color: #e4e4e7; -fx-font-family: 'Poppins';"
        );
    }

    public void dispose() {
        if (disposable != null && !disposable.isDisposed()) {
            disposable.dispose();
        }
    }

    private void setupBindings() {
        header.addListener((obs, oldVal, newVal) -> {
            headerLabel.setText(newVal);
            updateChildren();
            adjustHeight();
        });

        textField.textProperty().addListener((observable, oldVal, newVal) -> {
            if (textChangeListener != null) {
                textChangeListener.onTextChanged(newVal);
            }
        });
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
        return textField.getText();
    }

    public String getErrorText() {
        return errorLabel.getText();
    }

    public void setError(String error) {
        errorLabel.setText(error);
        errorLabel.setVisible(true);
        updateChildren();
        adjustHeight();
        runOpacityAnimation(errorLabel);
    }

    public void clearError() {
        errorLabel.setText("");
        errorLabel.setVisible(false);
        updateChildren();
        adjustHeight();
    }

    public void setText(String value) {
        textField.setText(value);
    }

    public TextField getTextField() {
        return textField;
    }

    @FXML
    public void onDestroy() {
        dispose();
    }
}