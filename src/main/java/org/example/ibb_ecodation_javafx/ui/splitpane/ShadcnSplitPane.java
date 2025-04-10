package org.example.ibb_ecodation_javafx.ui.splitpane;

import javafx.scene.Node;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class ShadcnSplitPane extends HBox {

    private final VBox leftPane;
    private final VBox rightPane;

    public ShadcnSplitPane() {
        // Initialize panes
        leftPane = new VBox();
        rightPane = new VBox();
        this.setStyle("-fx-padding:20;");
        leftPane.setPrefWidth(900);
        rightPane.setMaxWidth(900);
        this.setSpacing(20);

        // Set grow properties (Burası önemli)
        HBox.setHgrow(leftPane, Priority.ALWAYS);
        HBox.setHgrow(rightPane, Priority.ALWAYS);

        getChildren().addAll(leftPane, rightPane);
    }

    public void setLeftContent(Node... children) {
        leftPane.getChildren().setAll(children);
    }

    public void setRightContent(Node... children) {
        rightPane.getChildren().setAll(children);
    }
}
