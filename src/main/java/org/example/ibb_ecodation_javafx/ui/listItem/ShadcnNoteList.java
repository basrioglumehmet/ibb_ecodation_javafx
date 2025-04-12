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
import org.example.ibb_ecodation_javafx.utils.FontAwesomeUtil;
import io.reactivex.rxjava3.disposables.Disposable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import static org.example.ibb_ecodation_javafx.utils.FontAwesomeUtil.getGlyphIcon;

public class ShadcnNoteList extends ScrollPane {
    private final GridPane gridPane;
    private final List<UserNote> notes;
    private static final int COLUMNS = 2;
    private final StringProperty glyphIconName = new SimpleStringProperty();
    private final LanguageService languageService;
    private final String languageCode;
    private Button plusCard;
    private Label pageTitle;
    private javafx.event.EventHandler<javafx.event.ActionEvent> plusCardAction;
    private Consumer<UserNote> updateNoteAction;
    private Consumer<UserNote> removeNoteAction;
    private Store store = Store.getInstance();
    private Disposable darkModeSubscription;

    public ShadcnNoteList(LanguageService languageService, String languageCode) {
        this.languageService = languageService;
        this.languageCode = languageCode;
        languageService.loadAll(languageCode);

        pageTitle = new Label(languageService.translate("label.note"));
        updateTitleStyle();

        VBox container = new VBox(12);
        container.setAlignment(Pos.TOP_LEFT);
        container.setPadding(new Insets(0, 0, 0, 0));

        gridPane = new GridPane();
        gridPane.setPadding(new Insets(15));
        gridPane.setHgap(8);
        gridPane.setVgap(8);
        gridPane.setStyle("-fx-background-color: transparent;");
        gridPane.setMaxWidth(Double.MAX_VALUE);

        for (int i = 0; i < COLUMNS; i++) {
            ColumnConstraints column = new ColumnConstraints();
            column.setPercentWidth(50);
            column.setHgrow(Priority.ALWAYS);
            gridPane.getColumnConstraints().add(column);
        }

        container.getChildren().addAll(pageTitle, gridPane);

        notes = new ArrayList<>();
        this.setContent(container);
        this.setFitToWidth(true);
        this.setVbarPolicy(ScrollBarPolicy.AS_NEEDED);
        this.setHbarPolicy(ScrollBarPolicy.NEVER);
        this.setStyle("-fx-background-color: transparent; -fx-padding: 15;");

        glyphIconName.set("CLOCK");
        updateGrid(false);


        darkModeSubscription = store.getState().subscribe(stateRegistry -> {
            updateTitleStyle();
            updateGrid(false);
        });
    }

    private void updateTitleStyle() {
        boolean isDarkMode = store.getCurrentState(DarkModeState.class).isEnabled();
        pageTitle.setStyle("-fx-font-size: 24; -fx-font-family: 'Poppins';" +
                String.format("-fx-text-fill: %s;", isDarkMode ? "#fff" : "#000"));
    }

    public void addNote(UserNote userNote) {
        notes.add(userNote);
        addNewCardWithAnimation(userNote);
    }

    public void clearNotes() {
        notes.clear();
        updateGrid(false);
    }

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

    private VBox createNoteCard(UserNote note) {
        boolean isDarkMode = store.getCurrentState(DarkModeState.class).isEnabled();

        VBox card = new VBox(8);
        card.setStyle("-fx-background-radius: 8;" +
                String.format("-fx-background-color: %s; -fx-padding: 10;" +
                                "-fx-border-radius: 8; -fx-border-width: 1; -fx-border-color: %s;",
                        isDarkMode ? "#202024" : "#f5f5f5",
                        isDarkMode ? "#2c2c30" : "#e4e4e7"));
        card.setMaxWidth(Double.MAX_VALUE);
        card.setMinHeight(160);
        card.setOnMouseEntered(e -> card.setStyle("-fx-background-radius: 8;" +
                String.format("-fx-background-color: %s; -fx-padding: 10; " +
                                "-fx-border-radius: 8; -fx-border-width: 1; -fx-border-color: %s;",
                        isDarkMode ? "#2c2c30" : "#e5e5e5",
                        isDarkMode ? "#2c2c30" : "#e4e4e7")));
        card.setOnMouseExited(e -> card.setStyle("-fx-background-radius: 8;" +
                String.format("-fx-background-color: %s; -fx-padding: 10;" +
                                "-fx-border-radius: 8; -fx-border-width: 1; -fx-border-color: %s;",
                        isDarkMode ? "#202024" : "#f5f5f5",
                        isDarkMode ? "#2c2c30" : "#e4e4e7")));

        Label dateLabel = new Label(note.getReportAt().toString());
        dateLabel.setStyle("-fx-font-size: 13; -fx-font-family: 'Poppins';" +
                String.format("-fx-text-fill: %s;", isDarkMode ? "#fff" : "#000"));

        Label titleLabel = new Label(note.getHeader());
        titleLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 16; -fx-font-family: 'Poppins';" +
                String.format("-fx-text-fill: %s;", isDarkMode ? "#fff" : "#000"));
        titleLabel.setWrapText(true);

        FontAwesomeIconView iconView = getGlyphIcon(this.glyphIconName);
        iconView.setGlyphSize(16);
        iconView.setFill(Paint.valueOf(isDarkMode ? "#fff" : "#000"));
        StackPane iconWrapper = new StackPane(iconView);
        iconWrapper.setPadding(new Insets(2));

        Label contentLabel = new Label(note.getDescription());
        contentLabel.setStyle("-fx-font-size: 13; -fx-font-family: 'Poppins';" +
                String.format("-fx-text-fill: %s;", isDarkMode ? "#fff" : "#000"));
        contentLabel.setWrapText(true);

        HBox bottomSection = new HBox(8);
        bottomSection.setAlignment(Pos.CENTER_LEFT);
        bottomSection.getChildren().addAll(iconWrapper, contentLabel);

        HBox actionSection = new HBox(8);
        actionSection.setAlignment(Pos.CENTER_LEFT);

        ShadcnButton updateButton = createActionButton(
                languageService.translate("button.update"),
                ShadcnButton.ButtonType.PRIMARY,
                "USER",
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
        boolean isDarkMode = !store.getCurrentState(DarkModeState.class).isEnabled();

        plusCard = new Button();
        plusCard.setStyle("-fx-background-radius: 8;" +
                "-fx-background-color: #8dd80a; -fx-text-fill: white;" +
                "-fx-font-size: 14; -fx-font-family: 'Poppins'; -fx-padding: 10;");
        plusCard.setMaxWidth(Double.MAX_VALUE);
        plusCard.setPrefHeight(160);

        plusCard.setOnMouseEntered(e -> plusCard.setStyle("-fx-background-radius: 8;" +
                "-fx-background-color: #6cad03; -fx-text-fill: white;" +
                "-fx-font-size: 14; -fx-font-family: 'Poppins'; -fx-padding: 10;"));
        plusCard.setOnMouseExited(e -> plusCard.setStyle("-fx-background-radius: 8;" +
                "-fx-background-color: #8dd80a; -fx-text-fill: white;" +
                "-fx-font-size: 14; -fx-font-family: 'Poppins'; -fx-padding: 10;"));

        var plusIconProperty = new SimpleStringProperty("PLUS");
        FontAwesomeIconView plusIcon = FontAwesomeUtil.getGlyphIcon(plusIconProperty);
        plusIcon.setGlyphSize(16);
        plusIcon.setFill(Paint.valueOf("black"));

        Label plusLabel = new Label(languageService.translate("label.newnote"));
        plusLabel.setStyle("-fx-font-size: 14; -fx-font-family: 'Poppins'; -fx-text-fill: black;");

        HBox plusContent = new HBox(5, plusIcon, plusLabel);
        plusContent.setAlignment(Pos.CENTER);
        plusCard.setGraphic(plusContent);

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
        gridPane.getChildren().removeIf(node -> node instanceof Button);
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

    public void dispose() {
        if (darkModeSubscription != null && !darkModeSubscription.isDisposed()) {
            darkModeSubscription.dispose();
        }
    }
}