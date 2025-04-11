package org.example.ibb_ecodation_javafx.ui.listItem;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.*;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import java.util.ArrayList;
import java.util.List;
import javafx.scene.paint.Paint;
import org.example.ibb_ecodation_javafx.core.service.LanguageService;
import org.example.ibb_ecodation_javafx.ui.button.ShadcnButton;
import javafx.animation.ScaleTransition;
import javafx.util.Duration;

import static org.example.ibb_ecodation_javafx.utils.FontAwesomeUtil.getGlyphIcon;

public class ShadcnNoteList extends ScrollPane {
    private GridPane gridPane;
    private List<Note> notes;
    private static final int COLUMNS = 2;
    private final StringProperty glyphIconName = new SimpleStringProperty();
    private final LanguageService languageService;
    private final String languageCode;

    private static class Note {
        String date;
        String title;
        String content;

        Note(String date, String title, String content) {
            this.date = date;
            this.title = title;
            this.content = content;
        }
    }

    public ShadcnNoteList(LanguageService languageService, String languageCode) {
        this.languageService = languageService;
        this.languageCode = languageCode;
        languageService.loadAll(languageCode);

        gridPane = new GridPane();
        gridPane.setPadding(new Insets(10));
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        gridPane.setStyle("-fx-background-color: transparent;");
        this.setStyle("-fx-padding:20px;");
        gridPane.setMaxWidth(Double.MAX_VALUE);

        for (int i = 0; i < COLUMNS; i++) {
            ColumnConstraints column = new ColumnConstraints();
            column.setPercentWidth(50);
            column.setHgrow(Priority.ALWAYS);
            gridPane.getColumnConstraints().add(column);
        }

        notes = new ArrayList<>();
        this.setContent(gridPane);
        this.setFitToWidth(true);
        this.setVbarPolicy(ScrollBarPolicy.AS_NEEDED);
        this.setHbarPolicy(ScrollBarPolicy.NEVER);

        // Translate initial notes
        notes.add(new Note(
                languageService.translate("note.date.1"),
                languageService.translate("note.title.song"),
                languageService.translate("note.content.timer")
        ));
        notes.add(new Note(
                languageService.translate("note.date.2"),
                languageService.translate("note.title.awaking"),
                languageService.translate("note.content.timer")
        ));
        notes.add(new Note(
                languageService.translate("note.date.3"),
                languageService.translate("note.title.heart"),
                languageService.translate("note.content.timer")
        ));

        glyphIconName.set(languageService.translate("icon.clock")); // Translate glyph icon name
        updateGrid(false);
    }

    public void addNote(String date, String title, String content) {
        Note newNote = new Note(date, title, content); // Assume inputs are pre-translated; if keys, translate here
        notes.add(newNote);
        addNewCardWithAnimation(newNote);
    }

    public void clearNotes() {
        notes.clear();
        updateGrid(false);
    }

    private void updateGrid(boolean animateAll) {
        gridPane.getChildren().clear();

        for (int i = 0; i < notes.size(); i++) {
            Note note = notes.get(i);
            VBox card = createNoteCard(note);

            int row = i / COLUMNS;
            int col = i % COLUMNS;

            gridPane.add(card, col, row);
            GridPane.setHgrow(card, Priority.ALWAYS);
            GridPane.setFillWidth(card, true);

            if (animateAll) {
                animateCard(card);
            }
        }

        addPlusButton(false);
    }

    private void addNewCardWithAnimation(Note newNote) {
        VBox card = createNoteCard(newNote);
        int totalItems = notes.size() - 1;
        int row = totalItems / COLUMNS;
        int col = totalItems % COLUMNS;

        card.setScaleX(0);
        card.setScaleY(0);
        gridPane.add(card, col, row);
        GridPane.setHgrow(card, Priority.ALWAYS);
        GridPane.setFillWidth(card, true);

        animateCard(card);

        updatePlusButtonPosition();
    }

    private VBox createNoteCard(Note note) {
        VBox card = new VBox(5);
        card.setStyle("-fx-background-color: #202024; -fx-background-radius: 8px; -fx-padding: 20px;");
        card.setMaxWidth(Double.MAX_VALUE);
        card.setSpacing(20);
        card.setMinHeight(182);

        Label dateLabel = new Label(note.date);
        dateLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #fff;");

        Label titleLabel = new Label(note.title);
        titleLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 16px; -fx-text-fill: #fff;");
        titleLabel.setWrapText(true);

        FontAwesomeIconView iconView = getGlyphIcon(this.glyphIconName);
        iconView.setGlyphSize(16);
        iconView.setFill(Paint.valueOf("white"));

        StackPane iconWrapper = new StackPane(iconView);

        Label contentLabel = new Label(note.content);
        contentLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #fff;");
        contentLabel.setWrapText(true);

        HBox bottomSection = new HBox(10);
        bottomSection.setAlignment(Pos.CENTER_LEFT);
        bottomSection.getChildren().addAll(iconWrapper, contentLabel);

        HBox actionSection = new HBox(10);
        actionSection.setAlignment(Pos.CENTER_LEFT);
        ShadcnButton removeButton = new ShadcnButton(
                languageService.translate("button.remove"), // Translated "Remove"
                ShadcnButton.ButtonType.DESTRUCTIVE,
                "TRASH",
                true,
                false,
                "center"
        );

        removeButton.setOnAction(e -> {
            notes.remove(note);
            updateGrid(false);
        });

        actionSection.getChildren().add(removeButton);
        card.getChildren().addAll(dateLabel, titleLabel, bottomSection, actionSection);

        return card;
    }

    private void animateCard(Pane card) {
        ScaleTransition scaleTransition = new ScaleTransition(Duration.millis(300), card);
        scaleTransition.setFromX(0);
        scaleTransition.setFromY(0);
        scaleTransition.setToX(1);
        scaleTransition.setToY(1);
        scaleTransition.setCycleCount(1);
        scaleTransition.setAutoReverse(false);
        scaleTransition.play();
    }

    private void addPlusButton(boolean animate) {
        Button plusCard = new Button(languageService.translate("label.newnote"));
        plusCard.setPadding(new Insets(10));
        plusCard.setStyle("-fx-background-color: #f27a1a; -fx-background-radius: 8px; -fx-padding: 20px; -fx-text-fill: #1a1a1e; -fx-font-size: 36px;");
        plusCard.setMaxWidth(Double.MAX_VALUE);
        plusCard.setPrefHeight(200);
        plusCard.setAlignment(Pos.CENTER);

        plusCard.setOnAction(e -> {
            addNote(
                    languageService.translate("note.date.new"),
                    languageService.translate("note.title.new"),
                    languageService.translate("note.content.new")
            );
        });

        int totalItems = notes.size();
        int row = totalItems / COLUMNS;
        int col = totalItems % COLUMNS;

        gridPane.add(plusCard, col, row);
        GridPane.setHgrow(plusCard, Priority.ALWAYS);
        GridPane.setFillWidth(plusCard, true);
    }

    private void updatePlusButtonPosition() {
        gridPane.getChildren().removeIf(node -> node instanceof Button && languageService.translate("label.newnote").equals(((Button)node).getText()));
        addPlusButton(false);
    }
}