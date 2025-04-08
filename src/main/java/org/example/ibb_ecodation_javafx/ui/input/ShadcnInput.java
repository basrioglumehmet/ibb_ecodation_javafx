package org.example.ibb_ecodation_javafx.ui.input;

import io.reactivex.rxjava3.disposables.Disposable;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import org.example.ibb_ecodation_javafx.statemanagement.Store;
import org.example.ibb_ecodation_javafx.statemanagement.state.DarkModeState;

import static org.example.ibb_ecodation_javafx.utils.GuiAnimationUtil.runOpacityAnimation;

public class ShadcnInput extends VBox {

    private final StringProperty header = new SimpleStringProperty("Header");
    private final Label headerLabel = new Label();
    private final Label errorLabel = new Label();
    private final TextField textField = new TextField();
    private final Store store = Store.getInstance();
    private boolean isLightMode;
    private Disposable disposable;

    public ShadcnInput() {
        this("Header");
    }

    public ShadcnInput(String headerText) {
        super(5);
        this.header.set(headerText);
        initializeUI();
        setupBindings();
    }

    private void initializeUI() {
        dispose();
        setFillWidth(true);
        setMaxWidth(Double.MAX_VALUE);

        headerLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-font-family: 'Poppins';");
        headerLabel.setText(header.get());
        errorLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill:red; -fx-font-family: 'Poppins';");
        errorLabel.setVisible(false);
        textField.setPrefWidth(Double.MAX_VALUE);
        textField.setMaxWidth(Double.MAX_VALUE);
        textField.setStyle("-fx-background-color: white;  -fx-font-family: 'Poppins'; -fx-font-size: 16px;");

        textField.setMinWidth(Region.USE_PREF_SIZE);
        textField.setPrefWidth(Region.USE_COMPUTED_SIZE);

        getChildren().addAll(headerLabel, textField,errorLabel);

        //Store'u dinle
        disposable = store.getState().subscribe(stateRegistry -> {
            isLightMode = stateRegistry.getState(DarkModeState.class).isEnabled();
            updateUI();
        });
    }

    public void updateUI(){

        headerLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-font-family: 'Poppins';");
        textField.setStyle(
                String.format("-fx-background-color: %s;", isLightMode ? "white":"white") +
                "-fx-background-radius: 6px;-fx-border-radius: 6px; -fx-padding:10px;  -fx-font-family: 'Poppins'; -fx-font-size: 16px; " +
                        " -fx-border-width:1; -fx-border-color:#e4e4e7; -fx-font-family: 'Poppins';"
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
        });
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

    public String getErrorText(){
        return errorLabel.getText();
    }

    public void setError(String error){
        errorLabel.setText(error);
        errorLabel.setVisible(true);
        runOpacityAnimation(errorLabel);
    }

    public void clearError(){
        errorLabel.setText("");
        errorLabel.setVisible(false);
    }

    public void setText(String value) {
        textField.setText(value);
    }

    public TextField getTextField() {
        return textField;
    }

    @FXML
    public void onDestroy() {
        dispose();  // Abonelikleri temizle
    }
}
