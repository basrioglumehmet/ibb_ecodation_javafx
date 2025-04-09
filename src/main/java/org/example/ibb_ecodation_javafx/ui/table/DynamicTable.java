package org.example.ibb_ecodation_javafx.ui.table;

import io.reactivex.rxjava3.subjects.PublishSubject;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.shape.Rectangle;
import javafx.util.Pair;
import org.example.ibb_ecodation_javafx.ui.combobox.ShadcnComboBox;

import java.util.Map;

public class DynamicTable<T> extends VBox {

    private final TableView<ObservableList<String>> tableView;
    private final StackPane tableWrapper;
    private final ScrollPane scrollPane; // Horizontal scrolling için ScrollPane ekliyoruz
    private String headerText = "Header"; // Varsayılan header değeri
    private String descriptionText = "Description"; // Varsayılan description değeri
    private ShadcnComboBox comboBox;

    public DynamicTable() {
        // VBox özelliklerini ayarla
        this.setSpacing(20);
        this.setPadding(new Insets(20));
        this.setStyle("-fx-background-color: #202024; -fx-background-radius: 20;");

        // DynamicTable'ın parent içinde esnek olmasını sağla
        VBox.setVgrow(this, Priority.ALWAYS);
        HBox.setHgrow(this, Priority.ALWAYS);

        // TableView oluştur
        tableView = new TableView<>();
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY); // Sütunların genişliğini manuel olarak ayarlayabilmek için
        tableView.setStyle("-fx-background-radius: 20; -fx-border-radius: 20; -fx-background-color: #121214; -fx-control-inner-background: #121214; -fx-text-fill: white;");
        tableView.setPlaceholder(new Label("No data available"));

        // ScrollPane oluştur ve TableView'ı içine yerleştir
        scrollPane = new ScrollPane(tableView);
        scrollPane.setFitToHeight(true); // Yükseklik otomatik ayarlansın
        scrollPane.setFitToWidth(false); // Genişlik için kaydırma çubuğu aktif olsun
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED); // Yatay kaydırma çubuğu gerektiğinde görünsün
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED); // Dikey kaydırma çubuğu gerektiğinde görünsün
        scrollPane.setStyle("-fx-background: #121214; -fx-background-color: #121214;");

        // TableWrapper oluştur
        tableWrapper = new StackPane(scrollPane);
        tableWrapper.setStyle("-fx-background-color:#121214; -fx-padding:20px;");
        applyRoundedClipping(tableWrapper, 20);

        // TableWrapper'ın VBox içinde esnek olmasını sağla
        VBox.setVgrow(tableWrapper, Priority.ALWAYS);
        StackPane.setAlignment(scrollPane, Pos.CENTER);

        // TableView'un genişliğini ve yüksekliğini ayarla
        tableView.setMinWidth(Region.USE_PREF_SIZE); // İçeriğe göre genişlik
        tableView.setMaxWidth(Double.MAX_VALUE);
        tableView.setMaxHeight(Double.MAX_VALUE);
        StackPane.setMargin(scrollPane, new Insets(0)); // Kenar boşluklarını sıfırla

        // Bileşenleri ekle
        this.getChildren().addAll(createHeader(), tableWrapper);
    }

    // Yuvarlatılmış köşeler için clipping
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
        Label titleLabel = new Label(headerText); // headerText kullanımı
        titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: white;");

        Label subtitleLabel = new Label(descriptionText); // descriptionText kullanımı
        subtitleLabel.setStyle("-fx-text-fill: #fff; -fx-font-size: 14px;");
        titleSection.getChildren().addAll(titleLabel, subtitleLabel);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox buttonGroup = new HBox(10);
        buttonGroup.setAlignment(Pos.CENTER_RIGHT);

//        Button exportButton = new Button("Export");
//        exportButton.setStyle("-fx-background-color: #fff; -fx-text-fill: #000; -fx-background-radius: 20;");
//        exportButton.setPadding(new Insets(8, 15, 8, 15));
//
//        Button downloadButton = new Button("Download");
//        downloadButton.setStyle("-fx-background-color: #f27a1a; -fx-text-fill: white; -fx-background-radius: 20;");
//        downloadButton.setPadding(new Insets(8, 15, 8, 15));

        comboBox = new ShadcnComboBox(s -> s);



        buttonGroup.getChildren().addAll(comboBox);
        header.getChildren().addAll(titleSection, spacer, buttonGroup);

        return header;
    }
    public PublishSubject<Pair<String,T>> watchComboBox(){
        return comboBox.watchSelection();
    }
    public void setComboBoxItems(Map<String, String> items){
        comboBox.setItems(items);
    }

    public void setComboBoxTitle(String title){
        comboBox.setTitle(title);
    }

    public void addHeaders(String... headers) {
        tableView.getColumns().clear();

        for (int i = 0; i < headers.length; i++) {
            final int colIndex = i;
            TableColumn<ObservableList<String>, String> column = new TableColumn<>(headers[i]);

            column.setCellValueFactory(param ->
                    new ReadOnlyStringWrapper(param.getValue().get(colIndex))
            );

            // Dinamik genişlik hesaplama
            double baseWidthPerChar = 20.0; // Her karakter için temel piksel genişliği
            double minWidth = 80; // Minimum genişlik (örneğin "ID" gibi kısa başlıklar için)
            double headerLength = headers[i].length();
            double calculatedWidth = headerLength * baseWidthPerChar;

            // Minimum genişlik ile hesaplanan genişlik arasında maksimumu al
            column.setPrefWidth(Math.max(minWidth, calculatedWidth));
            column.setMinWidth(minWidth); // Çok kısa başlıklar için minimum genişlik garanti edilir

            column.setStyle("-fx-alignment: CENTER; -fx-text-fill: white;");

            tableView.getColumns().add(column);
        }
    }

    public void addData(String... rowData) {
        ObservableList<String> row = FXCollections.observableArrayList(rowData);
        tableView.getItems().add(row);
    }

    public void clearData() {
        tableView.getItems().clear();
    }

    public TableView<ObservableList<String>> getTableView() {
        return tableView;
    }

    // Header için getter ve setter
    public String getHeaderText() {
        return headerText;
    }

    public void setHeaderText(String headerText) {
        this.headerText = headerText;
        refreshHeader(); // Header'ı güncelle
    }

    // Description için getter ve setter
    public String getDescriptionText() {
        return descriptionText;
    }

    public void setDescriptionText(String descriptionText) {
        this.descriptionText = descriptionText;
        refreshHeader(); // Description'ı güncelle
    }

    // Header'ı yeniden oluşturup güncelleyen yardımcı metod
    private void refreshHeader() {
        this.getChildren().remove(0); // Eski header'ı kaldır
        this.getChildren().add(0, createHeader()); // Yeni header'ı ekle
    }

    @Override
    public void requestLayout() {
        super.requestLayout();
    }
}