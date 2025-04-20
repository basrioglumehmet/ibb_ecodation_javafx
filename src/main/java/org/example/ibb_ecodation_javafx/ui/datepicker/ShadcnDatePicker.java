package org.example.ibb_ecodation_javafx.ui.datepicker;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.subjects.PublishSubject;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.paint.Color;
import javafx.util.Pair;
import org.example.ibb_ecodation_javafx.statemanagement.Store;
import org.example.ibb_ecodation_javafx.statemanagement.state.DarkModeState;
import org.example.ibb_ecodation_javafx.ui.ValidatableComponent;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;

import static org.example.ibb_ecodation_javafx.utils.FontAwesomeUtil.getGlyphIcon;
import static org.example.ibb_ecodation_javafx.utils.GuiAnimationUtil.runOpacityAnimation;
import static org.example.ibb_ecodation_javafx.utils.ThemeUtil.*;

public class ShadcnDatePicker extends VBox implements ValidatableComponent {
    private ContextMenu menu;
    private Button button;
    private boolean isOpen = false;
    private final StringProperty prevIcon = new SimpleStringProperty("PREV");
    private final StringProperty nextIcon = new SimpleStringProperty("NEXT");
    @FXML
    private final StringProperty header = new SimpleStringProperty("Placeholder");
    private final StringProperty error = new SimpleStringProperty("");
    private static final String FONT_FAMILY = "Poppins";
    private static final String BASE_STYLE = "-fx-font-family: '" + FONT_FAMILY + "'; -fx-font-size: 14px; -fx-padding: 6 10 6 10; -fx-background-radius: 4px; -fx-border-radius: 4px; -fx-border-width: 1px; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 3, 0, 0, 1);";
    private static final String LIGHT_MODE = "-fx-background-color: #f2f2f3; -fx-border-color: #e4e4e7; -fx-text-fill: #1C2526;";
    private static final String DARK_MODE = "-fx-background-color: #2c2c30; -fx-border-color: #2b2b30; -fx-text-fill: #FFFFFF;";
    private static final String LIGHT_MODE_FOCUS = "-fx-border-color: #8dd80a; -fx-background-color: #FFFFFF;";
    private static final String DARK_MODE_FOCUS = "-fx-border-color:#8dd80a; -fx-background-color: #38383c;";
    private static final String ERROR_STYLE = "-fx-font-family: '" + FONT_FAMILY + "'; -fx-font-size: 11px; -fx-text-fill: #FF5555; -fx-font-weight: 400;";
    private LocalDate currentTime;
    private LocalDate selectedDate; // New field to track selected date
    private boolean isDarkMode;
    private final PublishSubject<Pair<String, Integer>> publishSubject = PublishSubject.create();
    private final Store store = Store.getInstance();
    private Disposable disposable;
    private Label placeholder;
    private final Label errorLabel = new Label();

    public ShadcnDatePicker() {
        initializeUI();
        initializeState();
    }

    @FXML
    public void initialize() {
        initializeUI();
        initializeState();
    }

    private void initializeUI() {
        setSpacing(10);

        placeholder = new Label();
        placeholder.textProperty().bind(header);
        placeholder.setStyle("-fx-text-fill: white;");

        button = new Button("dd/mm/yyyy");
        errorLabel.setStyle(ERROR_STYLE);
        errorLabel.textProperty().bind(error);
        errorLabel.setVisible(false);
        menu = new ContextMenu();
        menu.setOnHidden(windowEvent -> {
            menu.hide();
            isOpen = false;
            updateFocusStyles();
        });
        currentTime = LocalDate.now();
        updateMenuItems();

        button.setOnMouseClicked(event -> {
            if (event.getButton() == javafx.scene.input.MouseButton.PRIMARY && !isOpen) {
                double screenX = button.localToScreen(0, 0).getX();
                double screenY = button.localToScreen(0, 0).getY() + button.getHeight();
                menu.show(button, screenX, screenY + button.getHeight());
                isOpen = true;
                updateFocusStyles();
            } else {
                isOpen = false;
                menu.hide();
                updateFocusStyles();
            }
        });

        this.getChildren().addAll(placeholder, button,errorLabel);
    }

    private void initializeState() {
        disposable = store.getState().subscribe(stateRegistry -> {
            isDarkMode = store.getCurrentState(DarkModeState.class).isEnabled();
            updateStyles();
            updateFocusStyles();
        });
    }

    @FXML
    public void setHeader(String headerText) {
        header.set(headerText);
    }

    @FXML
    public String getHeader() {
        return header.get();
    }

    public StringProperty headerProperty() {
        return header;
    }

    @FXML
    public LocalDate getValue() {
        return selectedDate; // Return the selected date
    }

    private void updateFocusStyles() {
        String style = BASE_STYLE + (isDarkMode ? DARK_MODE : LIGHT_MODE);
        if (isOpen) {
            style += (isDarkMode ? DARK_MODE_FOCUS : LIGHT_MODE_FOCUS);
        }
        this.button.setStyle(style);
    }

    private void updateStyles() {
        this.button.setStyle(isDarkMode ? BASE_STYLE + DARK_MODE : BASE_STYLE + LIGHT_MODE);
    }

    private void updateMenuItems() {
        menu.getItems().clear();
        renderMonthSelection(currentTime);
        renderCalendar(currentTime);
    }

    private void renderMonthSelection(LocalDate of) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy - MMMM");
        YearMonth currentMonth = YearMonth.from(of);
        Label currentMonthLabel = new Label(currentMonth.format(formatter));
        changeTextColor(true, currentMonthLabel);

        FontAwesomeIconView iconView = getGlyphIcon(prevIcon.get());
        iconView.setGlyphSize(16);
        iconView.setFill(changeIconColor(true));

        FontAwesomeIconView nextIconView = getGlyphIcon(nextIcon.get());
        nextIconView.setGlyphSize(16);
        nextIconView.setFill(changeIconColor(true));

        Button iconWrapper = new Button();
        iconWrapper.setGraphic(iconView);
        iconWrapper.setOnMouseClicked(mouseEvent -> {
            currentTime = currentTime.minusMonths(1);
            updateMenuItems();
        });
        iconWrapper.setOnMouseEntered(mouseEvent -> iconView.setFill(Color.web("#8dd80a")));
        iconWrapper.setOnMouseExited(mouseEvent -> iconView.setFill(changeIconColor(true)));
        iconWrapper.setPadding(new Insets(2));
        iconWrapper.setStyle("-fx-background-color:transparent;");

        Button iconWrapper2 = new Button();
        iconWrapper2.setGraphic(nextIconView);
        iconWrapper2.setPadding(new Insets(2));
        iconWrapper2.setStyle("-fx-background-color:transparent;");
        iconWrapper2.setOnMouseClicked(mouseEvent -> {
            currentTime = currentTime.plusMonths(1);
            updateMenuItems();
        });
        iconWrapper2.setOnMouseEntered(mouseEvent -> nextIconView.setFill(Color.web("#8dd80a")));
        iconWrapper2.setOnMouseExited(mouseEvent -> nextIconView.setFill(changeIconColor(true)));

        HBox container = new HBox();
        container.setAlignment(Pos.CENTER);
        container.setPadding(new Insets(5));
        container.setSpacing(5);

        Region leftSpace = new Region();
        Region rightSpace = new Region();
        HBox.setHgrow(leftSpace, Priority.ALWAYS);
        HBox.setHgrow(rightSpace, Priority.ALWAYS);

        container.getChildren().addAll(iconWrapper, leftSpace, currentMonthLabel, rightSpace, iconWrapper2);

        CustomMenuItem customMenuItem = new CustomMenuItem(container);
        customMenuItem.setHideOnClick(false);
        customMenuItem.getStyleClass().clear();

        container.setPrefWidth(444);

        changeContextMenuBackground(true, menu);
        menu.getItems().add(customMenuItem);
    }

    private void renderCalendar(LocalDate date) {
        YearMonth yearMonth = YearMonth.from(date);
        int daysInMonth = yearMonth.lengthOfMonth();
        YearMonth prevMonth = yearMonth.minusMonths(1);
        int daysInPrevMonth = prevMonth.lengthOfMonth();

        GridPane calendarGrid = new GridPane();
        calendarGrid.setHgap(5);
        calendarGrid.setVgap(5);
        calendarGrid.setPadding(new Insets(10));
        calendarGrid.setAlignment(Pos.CENTER);

        double totalWidth = 444;
        double hGap = calendarGrid.getHgap();
        double totalGapsWidth = hGap * 6;
        double availableWidth = totalWidth - totalGapsWidth - (calendarGrid.getPadding().getLeft() + calendarGrid.getPadding().getRight());
        double dayButtonWidth = availableWidth / 7;

        LocalDate firstDayOfMonth = LocalDate.of(yearMonth.getYear(), yearMonth.getMonth(), 1);
        int firstDayOfWeek = firstDayOfMonth.getDayOfWeek().getValue();
        int row = 0;
        int col = firstDayOfWeek % 7;

        for (int i = col - 1; i >= 0; i--) {
            int day = daysInPrevMonth - i;
            Button dayButton = new Button(String.valueOf(day));
            dayButton.setPrefWidth(dayButtonWidth);
            dayButton.setDisable(true);
            calendarGrid.add(dayButton, col - i - 1, row);
            changeDatePickerDayDisabledButtonStyle(true, dayButton);
        }

        for (int day = 1; day <= daysInMonth; day++) {
            Button dayButton = new Button(String.valueOf(day));
            dayButton.setPrefWidth(dayButtonWidth);
            int finalDay = day;
            dayButton.setOnAction(e -> {
                selectedDate = LocalDate.of(date.getYear(), date.getMonthValue(), finalDay); // Update selectedDate
                button.setText(String.format("%02d/%02d/%d", finalDay, date.getMonthValue(), date.getYear()));
                menu.hide();
                isOpen = false;
                updateFocusStyles();
                publishSubject.onNext(new Pair<>(date.getMonth().name(), date.getDayOfMonth()));
            });
            changeDatePickerDayButtonStyle(true, dayButton, false);
            dayButton.setOnMouseEntered(mouseEvent -> changeDatePickerDayButtonStyle(true, dayButton, true));
            dayButton.setOnMouseExited(mouseEvent -> changeDatePickerDayButtonStyle(true, dayButton, false));

            calendarGrid.add(dayButton, col, row);

            col++;
            if (col == 7) {
                col = 0;
                row++;
            }
        }

        CustomMenuItem calendarItem = new CustomMenuItem(calendarGrid);
        calendarItem.setHideOnClick(true);
        calendarItem.getStyleClass().clear();

        menu.getItems().add(calendarItem);
    }

    public Observable<Pair<String, Integer>> getObservable() {
        return publishSubject.hide();
    }

    @Override
    public void setError(String errorMessage) {
        this.error.set(errorMessage);
        this.errorLabel.setStyle(ERROR_STYLE);
        this.errorLabel.setVisible(true);
        runOpacityAnimation(errorLabel);
    }

    @Override
    public void clearError() {
        this.error.set("");
        this.errorLabel.setVisible(false);
        errorLabel.setStyle("-fx-text-fill: white;");
    }
}