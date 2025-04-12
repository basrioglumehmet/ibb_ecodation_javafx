package org.example.ibb_ecodation_javafx.ui.table;

import io.reactivex.rxjava3.subjects.PublishSubject;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.shape.Rectangle;
import javafx.util.Pair;
import org.example.ibb_ecodation_javafx.statemanagement.Store;
import org.example.ibb_ecodation_javafx.statemanagement.state.DarkModeState;
import org.example.ibb_ecodation_javafx.ui.combobox.ShadcnComboBox;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.example.ibb_ecodation_javafx.utils.ThemeUtil.*;

public class DynamicTable<T> extends VBox {

    private final VBox tableContent;
    private final StackPane tableWrapper;
    private final ScrollPane scrollPane;
    private String headerText = "Header";
    private String descriptionText = "Description";
    private ShadcnComboBox comboBox;
    private List<String> headers = new ArrayList<>();
    private List<Double> headerWidths = new ArrayList<>(); // Store header widths
    private List<List<String>> data = new ArrayList<>();
    private List<Pair<CheckBox, List<String>>> selectedRows = new ArrayList<>();
    private boolean singleSelection = false;
    private Label titleLabel;
    private Label subtitleLabel;
    private HBox headerRow;
    private Store store = Store.getInstance();

    public DynamicTable() {
        this.setSpacing(20);
        this.setPadding(new Insets(20));
        this.setStyle("-fx-background-color: #121214; -fx-background-radius: 20;");

        tableContent = new VBox();
        tableContent.setStyle("-fx-background-color: #202024; -fx-background-radius: 20;");

        scrollPane = new ScrollPane(tableContent);
        scrollPane.setFitToHeight(true);
        scrollPane.setFitToWidth(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setStyle("-fx-background-color: #202024; -fx-background-radius: 20;");

        // Ensure ScrollPane's viewport also has the correct background
        scrollPane.skinProperty().addListener((obs, oldSkin, newSkin) -> {
            if (newSkin != null) {
                Region viewport = (Region) scrollPane.lookup(".viewport");
                if (viewport != null) {
                    viewport.setStyle("-fx-background-color: #202024;");
                }
            }
        });

        tableWrapper = new StackPane(scrollPane);
        tableWrapper.setStyle("-fx-background-color: #202024; -fx-padding: 20px; -fx-background-radius: 20;");
        applyRoundedClipping(tableWrapper, 20);

        VBox.setVgrow(tableWrapper, Priority.ALWAYS);
        StackPane.setAlignment(scrollPane, Pos.CENTER);

        tableContent.setMinWidth(Region.USE_PREF_SIZE);
        tableContent.setMaxWidth(Double.MAX_VALUE);
        tableContent.setMaxHeight(Double.MAX_VALUE);

        this.getChildren().addAll(createHeader(), tableWrapper);
    }

    private void applyRoundedClipping(Region region, double radius) {
        Rectangle clip = new Rectangle();
        clip.setArcWidth(radius * 2);
        clip.setArcHeight(radius * 2);
        region.setClip(clip);
        region.layoutBoundsProperty().addListener((obs, oldVal, newVal) -> {
            clip.setWidth(newVal.getWidth());
            clip.setHeight(newVal.getHeight());
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
        titleLabel.setStyle("-fx-font-family: 'Poppins'; -fx-font-size: 24px; -fx-font-weight: bold;");

        subtitleLabel = new Label(descriptionText);
        subtitleLabel.setStyle("-fx-font-family: 'Poppins';  -fx-font-size: 14px;");
        titleSection.getChildren().addAll(titleLabel, subtitleLabel);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox buttonGroup = new HBox(10);
        buttonGroup.setAlignment(Pos.CENTER_RIGHT);

        comboBox = new ShadcnComboBox(s -> s);
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
        // Calculate widths based on headers and data
        calculateHeaderWidths();
        refreshTable();
    }

    public void addData(String... rowData) {
        data.add(new ArrayList<>(List.of(rowData)));
        calculateHeaderWidths(); // Recalculate widths to account for new data
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

        // For each column, find the maximum width between header and data
        for (int i = 0; i < headers.size(); i++) {
            String header = headers.get(i);
            double headerWidth = calculateCellWidth(header);
            double maxDataWidth = headerWidth;

            // Check all data entries in this column
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

        tableWrapper.setStyle("-fx-background-color: #202024; -fx-padding: 20px; -fx-background-radius: 20;");
        scrollPane.setStyle("-fx-background-color: #202024; -fx-background-radius: 20;");
        tableContent.setStyle("-fx-background-color: #202024; -fx-background-radius: 20;");

        if (headers.isEmpty()) {
            Label noHeadersLabel = new Label("No headers defined");
            noHeadersLabel.setStyle("-fx-font-family: 'Poppins'; -fx-text-fill: #f27a1a; -fx-padding: 10px;");
            tableContent.getChildren().add(noHeadersLabel);
            return;
        }




        // Add header row with an empty space for checkbox column
        headerRow = new HBox();
        headerRow.setStyle(" -fx-padding: 10px;  -fx-background-radius: 10 10 0 0;");
        headerRow.setSpacing(5); // Add spacing between header cells

        Label emptyLabel = new Label("");
        emptyLabel.setMinWidth(40);
        emptyLabel.setMaxWidth(40);
        emptyLabel.setAlignment(Pos.CENTER);
        HBox.setHgrow(emptyLabel, Priority.NEVER);
        headerRow.getChildren().add(emptyLabel);

        for (int i = 0; i < headers.size(); i++) {
            Label headerLabel = new Label(headers.get(i));
            headerLabel.setStyle("-fx-font-family: 'Poppins'; -fx-font-weight: bold; -fx-padding: 5px;");
            changeTextColor(store.getCurrentState(DarkModeState.class).isEnabled(), headerLabel);
            headerLabel.setMinWidth(headerWidths.get(i));
            headerLabel.setMaxWidth(headerWidths.get(i));
            headerLabel.setAlignment(Pos.CENTER);
            HBox.setHgrow(headerLabel, Priority.NEVER);
            headerRow.getChildren().add(headerLabel);
        }
        tableContent.getChildren().add(headerRow);

        // Add data rows with custom checkboxes
        if (data.isEmpty()) {
            Label noDataLabel = new Label("No data available");
            noDataLabel.setStyle("-fx-font-family: 'Poppins'; #f27a1a; -fx-padding: 10px;");
            tableContent.getChildren().add(noDataLabel);
        } else {
            for (int i = 0; i < data.size(); i++) {
                List<String> row = data.get(i);
                HBox dataRow = new HBox();
                dataRow.setStyle("-fx-padding: 10px;");
                dataRow.setSpacing(5); // Add spacing between data cells

                CheckBox checkBox = new CheckBox();
                checkBox.selectedProperty().addListener((obs, oldVal, newVal) -> {
                    if (singleSelection && newVal) {
                        selectedRows.forEach(pair -> {
                            if (pair.getKey() != checkBox) {
                                pair.getKey().setSelected(false);
                            }
                        });
                    }
                    updateRowStyle(dataRow, newVal);
                });

                checkBox.setMinWidth(40);
                checkBox.setMaxWidth(40);
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

                dataRow.getChildren().add(checkBox);

                for (int j = 0; j < headers.size(); j++) {
                    String cellText = j < row.size() ? row.get(j) : "";
                    Label cellLabel = new Label(cellText);
                    cellLabel.setStyle("-fx-font-family: 'Poppins';  -fx-padding: 5px;");
                    changeTextColor(store.getCurrentState(DarkModeState.class).isEnabled(), cellLabel);
                    cellLabel.setMinWidth(headerWidths.get(j));
                    cellLabel.setMaxWidth(headerWidths.get(j));
                    cellLabel.setAlignment(Pos.CENTER);
                    HBox.setHgrow(cellLabel, Priority.NEVER);
                    dataRow.getChildren().add(cellLabel);
                }
                tableContent.getChildren().add(dataRow);
            }
        }

        store.getState().subscribe(stateRegistry -> {
            boolean isDarkMode = stateRegistry.getState(DarkModeState.class).isEnabled();
            // Only update text colors, avoid changing table background
            changeTextColor(isDarkMode, titleLabel);
            changeTextColor(isDarkMode, subtitleLabel);
            headerRow.getChildren().forEach(node -> {
                if (node instanceof Label) {
                    changeTextColor(isDarkMode, (Label) node);
                }
            });
            tableContent.getChildren().forEach(node -> {
                if (node instanceof HBox && node != headerRow) {
                    ((HBox) node).getChildren().forEach(child -> {
                        if (child instanceof Label) {
                            changeTextColor(isDarkMode, (Label) child);
                        }
                    });
                }
            });
        });
    }

    private void updateRowStyle(HBox row, boolean isSelected) {
        String baseStyle = "-fx-padding: 10px; ";
        String radiusStyle = (data.indexOf(selectedRows.get(tableContent.getChildren().indexOf(row) - 1).getValue()) == data.size() - 1 && data.size() > 1) ? "-fx-background-radius: 0 0 10 10;" : "";
        row.setStyle(baseStyle + (isSelected ? " -fx-background-color: #202024;" : " -fx-background-color: transparent;") + radiusStyle);
    }

    private double calculateCellWidth(String text) {
        double baseWidthPerChar = 10.0; // Reduced for better fit
        double minWidth = 60; // Reduced minimum width for smaller columns like "Version"
        double calculatedWidth = text.length() * baseWidthPerChar;
        return Math.max(minWidth, calculatedWidth);
    }

    @Override
    public void requestLayout() {
        super.requestLayout();
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