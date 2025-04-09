package org.example.ibb_ecodation_javafx.ui.chart;

import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.layout.Region;
import javafx.util.Duration;

import java.util.Map;

public class ShadcnBarChart extends BarChart<String, Number> {
    private final ObservableList<XYChart.Data<String, Number>> monthlyData;
    private Label totalLabel;
    private XYChart.Series<String, Number> series;

    public ShadcnBarChart() {
        this(true);
    }

    public ShadcnBarChart(boolean withSampleData) {
        super(new CategoryAxis(), new NumberAxis());

        this.monthlyData = FXCollections.observableArrayList();
        this.series = new XYChart.Series<>();

        if (withSampleData) {
            initializeDefaultData();
        }

        series.setData(monthlyData);
        getData().add(series);
        configureChart();

        setMaxWidth(Double.MAX_VALUE);
        setPrefWidth(Region.USE_COMPUTED_SIZE);
    }

    private void initializeDefaultData() {
        String[] months = {"Ocak", "Şubat", "Mart", "Nisan", "Mayıs", "Haziran",
                "Temmuz", "Ağustos", "Eylül", "Ekim", "Kasım", "Aralık"};
        double[] vatValues = {12500.50, 9800.75, 15300.20, 11200.00, 13750.30, 14500.60,
                16800.90, 15400.25, 13200.80, 14700.45, 15900.70, 17200.15};

        for (int i = 0; i < months.length; i++) {
            monthlyData.add(new XYChart.Data<>(months[i], vatValues[i]));
        }
    }

    private void configureChart() {
        setLegendVisible(false);
        ((CategoryAxis) getXAxis()).setLabel("Aylar");
        ((NumberAxis) getYAxis()).setLabel("KDV (TL)");

        getYAxis().setStyle("-fx-tick-label-fill:white;");
        getXAxis().setStyle("-fx-tick-label-fill:white;");

        setHorizontalGridLinesVisible(false);
        setVerticalGridLinesVisible(false);
        setHorizontalZeroLineVisible(false);

        setStyle("-fx-background-color: transparent;");
        lookup(".chart-plot-background").setStyle("-fx-background-color: transparent;");
        lookup(".chart-legend").setStyle("-fx-background-color: transparent;");
        lookup(".chart").setStyle("-fx-background-color: transparent;");

        Platform.runLater(() -> {
            Node title = lookup(".chart-title");
            if (title != null) {
                title.setStyle("-fx-text-fill: white; -fx-font-size: 18px;");
            }

            Node xAxisLabel = lookup(".x-axis .axis-label");
            if (xAxisLabel != null) {
                xAxisLabel.setStyle("-fx-text-fill: white; -fx-font-size: 14px;");
            }

            Node yAxisLabel = lookup(".y-axis .axis-label");
            if (yAxisLabel != null) {
                yAxisLabel.setStyle("-fx-text-fill: white; -fx-font-size: 14px;");
            }

            monthlyData.forEach(data -> {
                Node node = data.getNode();
                if (node != null) {
                    node.setOnMouseEntered(event -> {
                        FadeTransition ft = new FadeTransition(Duration.millis(200), node);
                        ft.setFromValue(1.0);
                        ft.setToValue(0.7);
                        ft.play();
                    });
                    node.setOnMouseExited(event -> {
                        FadeTransition ft = new FadeTransition(Duration.millis(200), node);
                        ft.setFromValue(0.7);
                        ft.setToValue(1.0);
                        ft.play();
                    });
                }
            });
        });

        applyCustomColors();
    }

    private void applyCustomColors() {
        String[] colors = {
                "#f27a1a", "#3aa15a", "#4795e5"
        };

        Platform.runLater(() -> {
            for (int i = 0; i < monthlyData.size(); i++) {
                XYChart.Data<String, Number> data = monthlyData.get(i);
                Node node = data.getNode();
                if (node != null) {
                    String color = colors[i % colors.length];

                    // Yuvarlatılmış üst köşeler
                    node.setStyle(
                            "-fx-background-color: " + color + ";" +
                                    "-fx-background-radius: 10 10 0 0;" +
                                    "-fx-padding: 0;"
                    );
                }
            }
        });
    }

    // Harici veri setleme metodu
    public void setMonthlyData(Map<String, Double> newData) {
        monthlyData.clear();
        newData.forEach((month, value) -> monthlyData.add(new XYChart.Data<>(month, value)));
        series.setData(monthlyData);
        applyCustomColors();
        updateTotal();
    }

    // Veri alma metodu
    public ObservableList<XYChart.Data<String, Number>> getMonthlyData() {
        return monthlyData;
    }

    public void updateData(String month, double value) {
        monthlyData.stream()
                .filter(data -> data.getXValue().equals(month))
                .findFirst()
                .ifPresent(data -> data.setYValue(value));
        updateTotal();
    }

    private void updateTotal() {
        if (totalLabel == null) return;

        double total = monthlyData.stream()
                .mapToDouble(data -> data.getYValue().doubleValue())
                .sum();
        totalLabel.setText(String.format("Toplam KDV: %.2f TL", total));
    }

    public Label getTotalLabel() {
        return totalLabel;
    }

    public void setTotalLabel(Label label) {
        this.totalLabel = label;
        updateTotal();
    }
}
