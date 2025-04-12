package org.example.ibb_ecodation_javafx.ui.table;

import io.reactivex.rxjava3.subjects.PublishSubject;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.animation.ScaleTransition;
import javafx.util.Duration;
import javafx.util.Pair;
import org.example.ibb_ecodation_javafx.statemanagement.Store;
import org.example.ibb_ecodation_javafx.statemanagement.state.DarkModeState;
import org.example.ibb_ecodation_javafx.ui.combobox.ShadcnComboBox;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;


public class DynamicTable<T> extends VBox {

    private final VBox tableContent;
    private final StackPane tableWrapper;
    private final ScrollPane scrollPane;
    private String headerText = "Header";
    private String descriptionText = "Description";
    private ShadcnComboBox comboBox;
    private List<String> headers = new ArrayList<>();
    private List<Double> headerWidths = new ArrayList<>();
    private List<List<String>> data = new ArrayList<>();
    private List<Pair<CheckBox, List<String>>> selectedRows = new ArrayList<>();
    private boolean singleSelection = false;
    private Label titleLabel;
    private Label subtitleLabel;
    private HBox headerRow;
    private Store store = Store.getInstance();

    public DynamicTable() {
        this.setSpacing(15);
        this.setPadding(new Insets(15));
        this.setStyle(String.format("-fx-background-color: %s; -fx-background-radius: 8; -fx-border-radius: 8; -fx-border-width: 1; -fx-border-color: %s;" +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 4, 0, 0, 2);",
                store.getCurrentState(DarkModeState.class).isEnabled() ? "#f2f2f3" : "#202024",
                store.getCurrentState(DarkModeState.class).isEnabled() ? "#e4e4e7" : "#2c2c30"));

        tableContent = new VBox();
        tableContent.setStyle("-fx-background-color: transparent;");

        scrollPane = new ScrollPane(tableContent);
        scrollPane.setFitToHeight(true);
        scrollPane.setFitToWidth(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setStyle("-fx-background-color: transparent;");

        scrollPane.skinProperty().addListener((obs, oldSkin, newSkin) -> {
            if (newSkin != null) {
                Region viewport = (Region) scrollPane.lookup(".viewport");
                if (viewport != null) {
                    viewport.setStyle("-fx-background-color: transparent;");
                }
            }
        });

        tableWrapper = new StackPane(scrollPane);
        tableWrapper.setStyle("-fx-background-color: transparent; -fx-padding: 10;");

        VBox.setVgrow(tableWrapper, Priority.ALWAYS);
        StackPane.setAlignment(scrollPane, Pos.CENTER);

        tableContent.setMinWidth(Region.USE_PREF_SIZE);
        tableContent.setMaxWidth(Double.MAX_VALUE);
        tableContent.setMaxHeight(Double.MAX_VALUE);

        this.getChildren().addAll(createHeader(), tableWrapper);

        store.getState().subscribe(stateRegistry -> {
            boolean isDarkMode = stateRegistry.getState(DarkModeState.class).isEnabled();
            this.setStyle(String.format("-fx-background-color: %s; -fx-background-radius: 8; -fx-border-radius: 8; -fx-border-width: 1; -fx-border-color: %s;" +
                            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 4, 0, 0, 2);",
                    !isDarkMode ? "#f2f2f3" : "#202024",
                    !isDarkMode ? "#e4e4e7" : "#2c2c30"));
            tableContent.setStyle("-fx-background-color: transparent;");
            scrollPane.setStyle("-fx-background-color: transparent;");
            tableWrapper.setStyle("-fx-background-color: transparent; -fx-padding: 10;");
            refreshTable();
        });
    }

    private HBox createHeader() {
        HBox header = new HBox();
        header.setAlignment(Pos.CENTER_LEFT);
        header.setSpacing(10);
        header.setMinHeight(Region.USE_PREF_SIZE);
        header.setMaxHeight(Region.USE_PREF_SIZE);

        VBox titleSection = new VBox(5);
        titleLabel = new Label(headerText);
        titleLabel.setStyle(String.format("-fx-font-family: 'Poppins'; -fx-font-size: 24; -fx-font-weight: bold; -fx-text-fill: %s;",
                !store.getCurrentState(DarkModeState.class).isEnabled() ? "#000" : "#fff"));

        subtitleLabel = new Label(descriptionText);
        subtitleLabel.setStyle(String.format("-fx-font-family: 'Poppins'; -fx-font-size: 14; -fx-text-fill: %s;",
                !store.getCurrentState(DarkModeState.class).isEnabled() ? "#000" : "#fff"));
        titleSection.getChildren().addAll(titleLabel, subtitleLabel);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox buttonGroup = new HBox(10);
        buttonGroup.setAlignment(Pos.CENTER_RIGHT);

        comboBox = new ShadcnComboBox(s -> s);
        comboBox.setStyle("-fx-background-color: #5865f2; -fx-background-radius: 6; -fx-text-fill: white; -fx-font-family: 'Poppins'; -fx-font-size: 12; -fx-padding: 6 12;");
        buttonGroup.getChildren().addAll(comboBox);

        header.getChildren().addAll(titleSection, spacer, buttonGroup);

        return header;
    }

    public PublishSubject<Pair<String, T>> watchComboBox() {
        return comboBox.watchSelection();
    }

    public void setComboBoxItems(Map<String, String> items) {
        comboBox.setItems(items);
    }

    public void setComboBoxTitle(String title) {
        comboBox.setTitle(title);
    }

    public void addHeaders(String... headers) {
        this.headers.clear();
        this.headerWidths.clear();
        this.headers.addAll(List.of(headers));
        calculateHeaderWidths();
        refreshTable();
    }

    public void addData(String... rowData) {
        data.add(new ArrayList<>(List.of(rowData)));
        calculateHeaderWidths();
        refreshTable();
    }

    public void clearData() {
        data.clear();
        selectedRows.clear();
        calculateHeaderWidths();
        refreshTable();
    }

    public List<List<String>> getData() {
        return new ArrayList<>(data);
    }

    public List<List<String>> getSelectedData() {
        return selectedRows.stream()
                .filter(pair -> pair.getKey().isSelected())
                .map(Pair::getValue)
                .collect(Collectors.toList());
    }

    public String getHeaderText() {
        return headerText;
    }

    public void setHeaderText(String headerText) {
        this.headerText = headerText;
        refreshHeader();
    }

    public String getDescriptionText() {
        return descriptionText;
    }

    public void setDescriptionText(String descriptionText) {
        this.descriptionText = descriptionText;
        refreshHeader();
    }

    public void setSingleSelection(boolean single) {
        this.singleSelection = single;
        refreshTable();
    }

    private void refreshHeader() {
        this.getChildren().remove(0);
        this.getChildren().add(0, createHeader());
    }

    private void calculateHeaderWidths() {
        headerWidths.clear();
        if (headers.isEmpty()) return;

        for (int i = 0; i < headers.size(); i++) {
            String header = headers.get(i);
            double headerWidth = calculateCellWidth(header);
            double maxDataWidth = headerWidth;

            for (List<String> row : data) {
                if (i < row.size()) {
                    double dataWidth = calculateCellWidth(row.get(i));
                    maxDataWidth = Math.max(maxDataWidth, dataWidth);
                }
            }
            headerWidths.add(maxDataWidth);
        }
    }

    private void refreshTable() {
        tableContent.getChildren().clear();
        selectedRows.clear();

        if (headers.isEmpty()) {
            Label noHeadersLabel = new Label("No headers defined");
            noHeadersLabel.setStyle("-fx-font-family: 'Poppins'; -fx-font-size: 14; -fx-text-fill: #5865f2; -fx-padding: 10;");
            tableContent.getChildren().add(noHeadersLabel);
            return;
        }

        headerRow = new HBox();
        headerRow.setStyle(String.format("-fx-background-color: %s; -fx-padding: 10; -fx-background-radius: 8 8 0 0;",
                !store.getCurrentState(DarkModeState.class).isEnabled() ? "#e8e8e8" : "#2c2c30"));
        headerRow.setSpacing(8);

        Label emptyLabel = new Label("");
        emptyLabel.setMinWidth(30);
        emptyLabel.setMaxWidth(30);
        emptyLabel.setAlignment(Pos.CENTER);
        HBox.setHgrow(emptyLabel, Priority.NEVER);
        headerRow.getChildren().add(emptyLabel);

        for (int i = 0; i < headers.size(); i++) {
            Label headerLabel = new Label(headers.get(i));
            headerLabel.setStyle(String.format("-fx-font-family: 'Poppins'; -fx-font-size: 14; -fx-font-weight: bold; -fx-padding: 5; -fx-text-fill: %s;",
                    !store.getCurrentState(DarkModeState.class).isEnabled() ? "#000" : "#fff"));
            headerLabel.setMinWidth(headerWidths.get(i));
            headerLabel.setMaxWidth(headerWidths.get(i));
            headerLabel.setAlignment(Pos.CENTER);
            HBox.setHgrow(headerLabel, Priority.NEVER);
            headerRow.getChildren().add(headerLabel);
        }
        tableContent.getChildren().add(headerRow);

        if (data.isEmpty()) {
            Label noDataLabel = new Label("No data available");
            noDataLabel.setStyle("-fx-font-family: 'Poppins'; -fx-font-size: 14; -fx-text-fill: #5865f2; -fx-padding: 10;");
            tableContent.getChildren().add(noDataLabel);
        } else {
            for (int i = 0; i < data.size(); i++) {
                List<String> row = data.get(i);
                final HBox dataRow = new HBox();
                boolean isEvenRow = (i % 2 == 0);
                String rowBackground = !store.getCurrentState(DarkModeState.class).isEnabled() ?
                        (isEvenRow ? "#ffffff" : "#f5f5f5") : (isEvenRow ? "#2c2c30" : "#252529");
                String radiusStyle = (i == 0 && i == data.size() - 1) ? "-fx-background-radius: 0 0 8 8;" :
                        (i == 0) ? "-fx-background-radius: 0;" :
                                (i == data.size() - 1) ? "-fx-background-radius: 0 0 8 8;" : "-fx-background-radius: 4;";
                dataRow.setStyle(String.format("-fx-padding: 8; -fx-background-color: %s; %s", rowBackground, radiusStyle));
                dataRow.setSpacing(8);

                CheckBox checkBox = new CheckBox();
                final int rowIndex = i;
                checkBox.selectedProperty().addListener((obs, oldValue, newValue) -> {
                    if (singleSelection && newValue) {
                        selectedRows.forEach(pair -> {
                            if (pair.getKey() != checkBox) {
                                pair.getKey().setSelected(false);
                            }
                        });
                    }
                    updateRowStyle(dataRow, newValue, rowIndex);
                });
                checkBox.setOnMouseClicked(e -> {
                    ScaleTransition st = new ScaleTransition(Duration.millis(100), checkBox);
                    st.setToX(checkBox.isSelected() ? 1.1 : 1);
                    st.setToY(checkBox.isSelected() ? 1.1 : 1);
                    st.play();
                });

                checkBox.setMinWidth(30);
                checkBox.setMaxWidth(30);
                checkBox.setAlignment(Pos.CENTER);
                HBox.setHgrow(checkBox, Priority.NEVER);

                selectedRows.add(new Pair<>(checkBox, row));

                dataRow.setOnMouseClicked((MouseEvent event) -> {
                    if (singleSelection) {
                        checkBox.setSelected(!checkBox.isSelected());
                    } else {
                        checkBox.setSelected(!checkBox.isSelected());
                    }
                });
                dataRow.setOnMouseEntered(e -> {
                    if (!checkBox.isSelected()) {
                        String hoverRadiusStyle = (rowIndex == 0 && data.size() == 1) ? "-fx-background-radius: 0 0 8 8;" :
                                (rowIndex == 0) ? "-fx-background-radius: 0;" :
                                        (rowIndex == data.size() - 1) ? "-fx-background-radius: 0 0 8 8;" : "-fx-background-radius: 4;";
                        dataRow.setStyle(String.format("-fx-padding: 8; -fx-background-color: %s; %s",
                                !store.getCurrentState(DarkModeState.class).isEnabled() ? "#f0f0f0" : "#2c2c30", hoverRadiusStyle));
                    }
                });
                dataRow.setOnMouseExited(e -> {
                    if (!checkBox.isSelected()) {
                        updateRowStyle(dataRow, false, rowIndex);
                    }
                });

                dataRow.getChildren().add(checkBox);

                for (int j = 0; j < headers.size(); j++) {
                    String cellText = j < row.size() ? row.get(j) : "";
                    Label cellLabel = new Label(cellText);
                    cellLabel.setStyle(String.format("-fx-font-family: 'Poppins'; -fx-font-size: 13; -fx-padding: 5; -fx-text-fill: %s;",
                            !store.getCurrentState(DarkModeState.class).isEnabled() ? "#000" : "#fff"));
                    cellLabel.setMinWidth(headerWidths.get(j));
                    cellLabel.setMaxWidth(headerWidths.get(j));
                    cellLabel.setAlignment(Pos.CENTER);
                    HBox.setHgrow(cellLabel, Priority.NEVER);
                    dataRow.getChildren().add(cellLabel);
                }
                tableContent.getChildren().add(dataRow);
            }
        }
    }

    private void updateRowStyle(HBox row, boolean isSelected, int rowIndex) {
        boolean isEvenRow = (rowIndex % 2 == 0);
        String rowBackground = !store.getCurrentState(DarkModeState.class).isEnabled() ?
                (isEvenRow ? "#ffffff" : "#f5f5f5") : (isEvenRow ? "#2c2c30" : "#252529");
        String radiusStyle = (rowIndex == 0 && data.size() == 1) ? "-fx-background-radius: 0 0 8 8;" :
                (rowIndex == 0) ? "-fx-background-radius: 0;" :
                        (rowIndex == data.size() - 1) ? "-fx-background-radius: 0 0 8 8;" : "-fx-background-radius: 4;";
        String baseStyle = "-fx-padding: 8;";
        row.setStyle(baseStyle + (isSelected ?
                String.format("-fx-background-color: %s; %s", !store.getCurrentState(DarkModeState.class).isEnabled() ? "#d9d9d9" : "#3a3a3e", radiusStyle) :
                String.format("-fx-background-color: %s; %s", rowBackground, radiusStyle)));
    }

    private double calculateCellWidth(String text) {
        double baseWidthPerChar = 8.0;
        double minWidth = 50;
        double calculatedWidth = text.length() * baseWidthPerChar;
        return Math.max(minWidth, calculatedWidth);
    }

    public List<String> getHeaders() {
        return headers;
    }

    public void setTableData(List<T> items, Function<T, List<String>> mapper) {
        data.clear();
        for (T item : items) {
            List<String> row = mapper.apply(item);
            data.add(new ArrayList<>(row));
        }
        calculateHeaderWidths();
        refreshTable();
    }
}