package org.example.ibb_ecodation_javafx.ui.listItem;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.*;
import javafx.scene.paint.Paint;
import org.example.ibb_ecodation_javafx.core.service.LanguageService;
import org.example.ibb_ecodation_javafx.model.UserNote;
import org.example.ibb_ecodation_javafx.statemanagement.Store;
import org.example.ibb_ecodation_javafx.statemanagement.state.DarkModeState;
import org.example.ibb_ecodation_javafx.ui.button.ShadcnButton;
import javafx.animation.ScaleTransition;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import static org.example.ibb_ecodation_javafx.utils.FontAwesomeUtil.getGlyphIcon;

/**
 * Not kartlarını görüntüleyen ve yöneten ScrollPane bileşeni.
 */
public class ShadcnNoteList extends ScrollPane {
    private final GridPane gridPane;
    private final List<UserNote> notes;
    private static final int COLUMNS = 2;
    private final StringProperty glyphIconName = new SimpleStringProperty();
    private final LanguageService languageService;
    private final String languageCode;
    private Button plusCard;
    private javafx.event.EventHandler<javafx.event.ActionEvent> plusCardAction;
    private Consumer<UserNote> updateNoteAction;
    private Consumer<UserNote> removeNoteAction;
    private Store store = Store.getInstance();

    public ShadcnNoteList(LanguageService languageService, String languageCode) {
        this.languageService = languageService;
        this.languageCode = languageCode;
        languageService.loadAll(languageCode);

        gridPane = new GridPane();
        gridPane.setPadding(new Insets(10));
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        gridPane.setStyle("-fx-background-color: transparent;");
        this.setStyle("-fx-padding: 20px;");
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

        glyphIconName.set("CLOCK");
        updateGrid(false);
    }

    /**
     * Yeni bir not ekler.
     *
     * @param userNote Eklenecek UserNote nesnesi.
     */
    public void addNote(UserNote userNote) {
        notes.add(userNote);
        addNewCardWithAnimation(userNote);
    }

    /**
     * Tüm notları temizler.
     */
    public void clearNotes() {
        notes.clear();
        updateGrid(false);
    }

    /**
     * Not kartlarını günceller ve ızgarayı yeniden çizer.
     *
     * @param animateAll Tüm kartların animasyonlu olup olmayacağı.
     */
    private void updateGrid(boolean animateAll) {
        gridPane.getChildren().clear();
        for (int i = 0; i < notes.size(); i++) {
            UserNote note = notes.get(i);
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

    /**
     * Yeni bir not kartı ekler ve animasyon uygular.
     *
     * @param newNote Eklenecek not.
     */
    private void addNewCardWithAnimation(UserNote newNote) {
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

    /**
     * Not kartı oluşturur.
     *
     * @param note Kartın verileri.
     * @return Oluşturulan kart.
     */
    private VBox createNoteCard(UserNote note) {
        VBox card = new VBox(5);
        card.setStyle(" -fx-background-radius: 8px; -fx-padding: 20px;"+
                String.format("-fx-background-color: %s;",
                        !store.getCurrentState(DarkModeState.class).isEnabled() ?
                                "#202024": // dark
                                "#fbfbfb"
                ));
        card.setMaxWidth(Double.MAX_VALUE);
        card.setSpacing(20);
        card.setMinHeight(182);

        Label dateLabel = new Label(note.getReportAt().toString());
        dateLabel.setStyle("-fx-font-size: 12px;"+
                String.format("-fx-text-fill: %s;",
                        !store.getCurrentState(DarkModeState.class).isEnabled() ?
                                "#fff": // dark
                                "#000"
                ));

        Label titleLabel = new Label(note.getHeader());
        titleLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 16px;"+
                String.format("-fx-text-fill: %s;",
                        !store.getCurrentState(DarkModeState.class).isEnabled() ?
                                "#fff": // dark
                                "#000"
                ));
        titleLabel.setWrapText(true);

        FontAwesomeIconView iconView = getGlyphIcon(this.glyphIconName);
        iconView.setGlyphSize(16);
        iconView.setFill(Paint.valueOf( !store.getCurrentState(DarkModeState.class).isEnabled() ?
                        "#fff": // dark
                        "#000"
        ));
        StackPane iconWrapper = new StackPane(iconView);

        Label contentLabel = new Label(note.getDescription());
        contentLabel.setStyle("-fx-font-size: 12px;"+
                String.format("-fx-text-fill: %s;",
                        !store.getCurrentState(DarkModeState.class).isEnabled() ?
                                "#fff": // dark
                                "#000"
                ));
        contentLabel.setWrapText(true);

        HBox bottomSection = new HBox(10);
        bottomSection.setAlignment(Pos.CENTER_LEFT);
        bottomSection.getChildren().addAll(iconWrapper, contentLabel);

        VBox actionSection = new VBox(10);
        actionSection.setAlignment(Pos.CENTER_LEFT);

        ShadcnButton updateButton = createActionButton(
                languageService.translate("button.update"),
                ShadcnButton.ButtonType.PRIMARY,
                "PENCIL",
                e -> {
                    if (updateNoteAction != null) {
                        updateNoteAction.accept(note);
                    } else {
                        System.out.println("Güncelleme aksiyonu tanımlı değil.");
                    }
                }
        );

        ShadcnButton removeButton = createActionButton(
                languageService.translate("button.remove"),
                ShadcnButton.ButtonType.DESTRUCTIVE,
                "TRASH",
                e -> {
                    if (removeNoteAction != null) {
                        removeNoteAction.accept(note);
                    } else {
                        System.out.println("Silme aksiyonu tanımlı değil.");
                    }
                }
        );

        actionSection.getChildren().addAll(updateButton, removeButton);
        card.getChildren().addAll(dateLabel, titleLabel, bottomSection, actionSection);

        return card;
    }

    private ShadcnButton createActionButton(String text, ShadcnButton.ButtonType type, String icon,
                                            javafx.event.EventHandler<javafx.event.ActionEvent> action) {
        ShadcnButton button = new ShadcnButton(text, type, icon, true, false, "center");
        button.setOnAction(action);
        return button;
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
        plusCard = new Button(languageService.translate("label.newnote"));
        plusCard.setPadding(new Insets(10));
        plusCard.setStyle("-fx-background-color: #f27a1a; -fx-background-radius: 8px; -fx-padding: 20px; -fx-text-fill: #1a1a1e; -fx-font-size: 36px;");
        plusCard.setMaxWidth(Double.MAX_VALUE);
        plusCard.setPrefHeight(200);
        plusCard.setAlignment(Pos.CENTER);

        if (plusCardAction != null) {
            plusCard.setOnAction(plusCardAction);
        }

        int totalItems = notes.size();
        int row = totalItems / COLUMNS;
        int col = totalItems % COLUMNS;
        gridPane.add(plusCard, col, row);
        GridPane.setHgrow(plusCard, Priority.ALWAYS);
        GridPane.setFillWidth(plusCard, true);
    }

    private void updatePlusButtonPosition() {
        gridPane.getChildren().removeIf(node -> node instanceof Button
                && languageService.translate("label.newnote").equals(((Button) node).getText()));
        addPlusButton(false);
    }

    public Button getPlusCard() {
        return plusCard;
    }

    public void setPlusCardAction(javafx.event.EventHandler<javafx.event.ActionEvent> action) {
        this.plusCardAction = action;
        if (plusCard != null) {
            plusCard.setOnAction(action);
        }
    }

    public void setUpdateNoteAction(Consumer<UserNote> action) {
        this.updateNoteAction = action;
    }

    public void setRemoveNoteAction(Consumer<UserNote> action) {
        this.removeNoteAction = action;
    }
}