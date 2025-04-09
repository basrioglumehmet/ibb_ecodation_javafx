package org.example.ibb_ecodation_javafx.ui.input;

import javafx.animation.TranslateTransition;
import javafx.geometry.Pos;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;

public class ShadcnOtpInput extends HBox {
    private final List<TextField> inputs = new ArrayList<>();

    private String baseStyle = "-fx-background-radius: 9999px; -fx-background-color:white;" +
            "-fx-border-radius: 99999px; " +
            "-fx-padding: 10px; " +
            "-fx-font-family: 'Poppins'; " +
            "-fx-font-size: 28px; " +
            "-fx-border-width: 1; " +
            "-fx-border-color: #e4e4e7; " +
            "-fx-focus-color: transparent; " +
            "-fx-faint-focus-color: transparent;";

    private String errorStyle = "-fx-background-radius: 9999px; -fx-background-color:white; " +
            "-fx-border-radius: 99999px; " +
            "-fx-padding: 10px; " +
            "-fx-font-family: 'Poppins'; " +
            "-fx-font-size: 28px; " +
            "-fx-border-width: 1; " +
            "-fx-border-color: #f14236; " +
            "-fx-focus-color: transparent; " +
            "-fx-faint-focus-color: transparent;";

    public ShadcnOtpInput() {
        for (int i = 0; i < 5; i++) {
            TextField field = new TextField("2");
            field.setPrefWidth(60);
            field.setPrefHeight(60);
            field.setMaxWidth(USE_PREF_SIZE);
            field.setAlignment(Pos.CENTER);
            field.setMaxHeight(USE_PREF_SIZE);
            setTextLimit(field, 1);
            field.setStyle(baseStyle
            );
            inputs.add(field);
        }
        this.setSpacing(20);
        this.getChildren().addAll(inputs);
    }

    public static void setTextLimit(TextField textField, int length) {
        textField.setOnKeyTyped(event -> {
            String string = textField.getText();
            if (string.length() > length) {
                textField.setText(string.substring(0, length));
                textField.positionCaret(string.length());
            }
        });
    }

    public String getCode() {
        StringBuilder sb = new StringBuilder();
        for (TextField input : inputs) {
            sb.append(input.getText());
        }
        return sb.toString();
    }

    public void setError(boolean b) {
        for (TextField input : inputs) {
            TranslateTransition tt = new TranslateTransition(Duration.millis(50), input);
            tt.setFromX(0f);
            tt.setByX(10f);
            tt.setCycleCount(6);
            tt.setAutoReverse(true);
            tt.play();
            input.setStyle(errorStyle);
        }
    }
}