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
import org.example.ibb_ecodation_javafx.model.Vat;
import org.example.ibb_ecodation_javafx.ui.combobox.ShadcnComboBox;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DynamicTable<T> extends VBox {

    private final VBox tableContent;
    private final StackPane tableWrapper;
    private final ScrollPane scrollPane;
    private String headerText = "Header";
    private String descriptionText = "Description";
    private ShadcnComboBox comboBox;
    private List<String> headers = new ArrayList<>();
    private List<List<String>> data = new ArrayList<>();
    private List<Pair<CheckBox, List<String>>> selectedRows = new ArrayList<>();

    public DynamicTable() {
        this.setSpacing(20);
        this.setPadding(new Insets(20));
        this.setStyle("-fx-background-color: #202024; -fx-background-radius: 20;");

        VBox.setVgrow(this, Priority.ALWAYS);
        HBox.setHgrow(this, Priority.ALWAYS);

        tableContent = new VBox();
        tableContent.setStyle("-fx-background-color: #121214; -fx-background-radius: 20; -fx-border-radius: 20; ");

        scrollPane = new ScrollPane(tableContent);
        scrollPane.setFitToHeight(true);
        scrollPane.setFitToWidth(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setStyle("-fx-background: #121214; -fx-background-color: #121214; -fx-background-radius: 20;");

        tableWrapper = new StackPane(scrollPane);
        tableWrapper.setStyle("-fx-background-color:#121214; -fx-padding:20px;");
        applyRoundedClipping(tableWrapper, 20);

        VBox.setVgrow(tableWrapper, Priority.ALWAYS);
        StackPane.setAlignment(scrollPane, Pos.CENTER);

        tableContent.setMinWidth(Region.USE_PREF_SIZE);
        tableContent.setMaxWidth(Double.MAX_VALUE);
        tableContent.setMaxHeight(Double.MAX_VALUE);

        StackPane.setMargin(scrollPane, new Insets(0));
        applyRoundedClipping(scrollPane, 20);
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
        Label titleLabel = new Label(headerText);
        titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: white;");

        Label subtitleLabel = new Label(descriptionText);
        subtitleLabel.setStyle("-fx-text-fill: #fff; -fx-font-size: 14px;");
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
        this.headers.addAll(List.of(headers));
        refreshTable();
    }

    public void addData(String... rowData) {
        data.add(new ArrayList<>(List.of(rowData)));
        refreshTable();
    }

    public void clearData() {
        data.clear();
        selectedRows.clear();
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

    private void refreshHeader() {
        this.getChildren().remove(0);
        this.getChildren().add(0, createHeader());
    }

    private void refreshTable() {
        tableContent.getChildren().clear();
        selectedRows.clear();

        if (headers.isEmpty()) {
            tableContent.getChildren().add(new Label("No headers defined"));
            return;
        }

        // Add header row with an empty space for checkbox column
        HBox headerRow = new HBox();
        headerRow.setStyle("-fx-background-color: #1a1a1d; -fx-padding: 10px; -fx-border-color: #303034; -fx-border-width: 0 0 1 0; -fx-background-radius: 10 10 0 0;");
        Label emptyLabel = new Label("");
        emptyLabel.setMinWidth(40);
        emptyLabel.setAlignment(Pos.CENTER);
        HBox.setHgrow(emptyLabel, Priority.ALWAYS);
        headerRow.getChildren().add(emptyLabel);

        for (String header : headers) {
            Label headerLabel = new Label(header);
            headerLabel.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 5px;");
            headerLabel.setMinWidth(calculateCellWidth(header));
            headerLabel.setAlignment(Pos.CENTER);
            HBox.setHgrow(headerLabel, Priority.ALWAYS);
            headerRow.getChildren().add(headerLabel);
        }
        tableContent.getChildren().add(headerRow);

        // Add data rows with custom checkboxes
        if (data.isEmpty()) {
            Label noDataLabel = new Label("No data available");
            noDataLabel.setStyle("-fx-text-fill: white; -fx-padding: 10px;");
            tableContent.getChildren().add(noDataLabel);
        } else {
            for (int i = 0; i < data.size(); i++) {
                List<String> row = data.get(i);
                HBox dataRow = new HBox();

                dataRow.setStyle("-fx-padding: 10px;  " );

                CheckBox checkBox = new CheckBox();

                checkBox.selectedProperty().addListener((obs, oldVal, newVal) -> {

                    updateRowStyle(dataRow, newVal);
                });

                checkBox.setMinWidth(40);
                checkBox.setAlignment(Pos.CENTER);
                HBox.setHgrow(checkBox, Priority.ALWAYS);

                selectedRows.add(new Pair<>(checkBox, row));

                dataRow.setOnMouseClicked((MouseEvent event) -> {
                    checkBox.setSelected(!checkBox.isSelected());
                });

                dataRow.getChildren().add(checkBox);

                for (int j = 0; j < headers.size(); j++) {
                    String cellText = j < row.size() ? row.get(j) : "";
                    Label cellLabel = new Label(cellText);
                    cellLabel.setStyle("-fx-text-fill: white; -fx-padding: 5px;");
                    cellLabel.setMinWidth(calculateCellWidth(headers.get(j)));
                    cellLabel.setAlignment(Pos.CENTER);
                    HBox.setHgrow(cellLabel, Priority.ALWAYS);
                    dataRow.getChildren().add(cellLabel);
                }
                tableContent.getChildren().add(dataRow);
            }
        }
    }

    private void updateRowStyle(HBox row, boolean isSelected) {
        String baseStyle = "-fx-padding: 10px; -fx-border-color: #303034; -fx-border-width: 0 0 1 0;";
        String radiusStyle = (data.indexOf(selectedRows.get(tableContent.getChildren().indexOf(row) - 1).getValue()) == data.size() - 1 && data.size() > 1) ? "-fx-background-radius: 0 0 10 10;" : "";
        row.setStyle(baseStyle + (isSelected ? " -fx-background-color: #2a2a2e;" : " -fx-background-color: transparent;") + radiusStyle);
    }

    private double calculateCellWidth(String text) {
        double baseWidthPerChar = 20.0;
        double minWidth = 80;
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

    public void setTableData(List<Vat> filteredData) {
        // Clear the previous data in the table
        data.clear();

        // Map each Vat object to a list of strings representing the row data
        for (Vat vat : filteredData) {
            List<String> row = new ArrayList<>();

            // Add the properties of Vat to the row
            row.add(String.valueOf(vat.getId()));  // ID
            row.add(vat.getBaseAmount().toString());  // Base amount
            row.add(vat.getRate().toString());  // Rate
            row.add(vat.getAmount().toString());  // VAT amount
            row.add(vat.getTotalAmount().toString());  // Total amount
            row.add(vat.getReceiptNumber());  // Receipt number
            row.add(new SimpleDateFormat("yyyy-MM-dd").format(vat.getTransactionDate()));  // Transaction date
            row.add(vat.getDescription());  // Description

            // Add the row to the data list
            data.add(row);
        }

        // Refresh the table to reflect the new data
        refreshTable();
    }

}