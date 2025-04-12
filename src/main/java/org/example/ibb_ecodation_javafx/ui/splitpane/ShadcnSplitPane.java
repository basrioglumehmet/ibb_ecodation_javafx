package org.example.ibb_ecodation_javafx.ui.splitpane;

import javafx.scene.Node;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class ShadcnSplitPane extends HBox {

    private final VBox leftPane;
    private final VBox rightPane;

    public ShadcnSplitPane() {
        leftPane = new VBox();
        rightPane = new VBox();

        this.setStyle("-fx-padding: 20;");
        this.setSpacing(20);

        HBox.setHgrow(leftPane, Priority.ALWAYS);
        HBox.setHgrow(rightPane, Priority.ALWAYS);
        VBox.setVgrow(leftPane,Priority.ALWAYS);
        VBox.setVgrow(rightPane,Priority.ALWAYS);

        // Add panes to HBox
        getChildren().addAll(leftPane, rightPane);
    }
    public void toggleLeftContent() {
        boolean currentlyVisible = leftPane.isVisible();
        leftPane.setVisible(!currentlyVisible);
        leftPane.setManaged(!currentlyVisible);
    }

    public void setLeftContent(Node... children) {
        leftPane.getChildren().setAll(children);
    }

    public void setRightContent(Node... children) {
        rightPane.getChildren().setAll(children);
    }
}
