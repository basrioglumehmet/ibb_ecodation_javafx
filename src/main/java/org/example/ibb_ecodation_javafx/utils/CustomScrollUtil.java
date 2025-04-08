package org.example.ibb_ecodation_javafx.utils;

import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

public class CustomScrollUtil {

    private double scrollY = 0;
    private double mouseY;
    private final Pane viewport;
    private final VBox content;

    public CustomScrollUtil(Pane viewport, VBox content) {
        this.viewport = viewport;
        this.content = content;
        initializeScroll();
    }

    private void initializeScroll() {
        // Ensure content size is calculated
        content.needsLayoutProperty().addListener((obs, old, newVal) -> {
            if (!newVal) {
                updateScrollBounds();
            }
        });

        viewport.setOnScroll((ScrollEvent event) -> {
            double deltaY = event.getDeltaY() * 2; // Multiply for faster scrolling
            scrollY = clamp(scrollY - deltaY, 0, getMaxScroll());
            updateScrollPosition();
            event.consume();
        });

        viewport.setOnMousePressed((MouseEvent event) -> {
            mouseY = event.getY();
        });

        viewport.setOnMouseDragged((MouseEvent event) -> {
            double deltaY = mouseY - event.getY();
            scrollY = clamp(scrollY + deltaY, 0, getMaxScroll());
            mouseY = event.getY();
            updateScrollPosition();
            event.consume();
        });

        updateScrollBounds();
    }

    private double getMaxScroll() {
        return Math.max(0, content.getBoundsInLocal().getHeight() - viewport.getHeight());
    }

    private double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }

    private void updateScrollBounds() {
        scrollY = clamp(scrollY, 0, getMaxScroll());
        updateScrollPosition();
    }

    private void updateScrollPosition() {
        content.setTranslateY(-scrollY);
    }

    public void setScrollPosition(double position) {
        scrollY = clamp(position, 0, getMaxScroll());
        updateScrollPosition();
    }

    public double getScrollPosition() {
        return scrollY;
    }
}