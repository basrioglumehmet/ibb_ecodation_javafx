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
import org.example.ibb_ecodation_javafx.statemanagement.Store;
import org.example.ibb_ecodation_javafx.statemanagement.state.DarkModeState;

import java.math.BigDecimal;
import java.util.Map;
import java.util.stream.Collectors;

import static org.example.ibb_ecodation_javafx.utils.ThemeUtil.*;

public class ShadcnBarChart extends BarChart<String, Number> {
    private final ObservableList<XYChart.Data<String, Number>> chartData;
    private Label totalLabel;
    private XYChart.Series<String, Number> series;
    private Store store = Store.getInstance();

    public ShadcnBarChart() {
        this(true);
    }

    public ShadcnBarChart(boolean withSampleData) {
        super(new CategoryAxis(), new NumberAxis());

        this.chartData = FXCollections.observableArrayList();
        this.series = new XYChart.Series<>();

        if (withSampleData) {
            initializeDefaultData();
        }

        series.setData(chartData);
        getData().add(series);
        configureChart();

        setMaxWidth(Double.MAX_VALUE);
        setPrefWidth(Region.USE_COMPUTED_SIZE);
    }

    private void initializeDefaultData() {
        String[] categories = {"Ocak", "Şubat", "Mart", "Nisan", "Mayıs", "Haziran",
                "Temmuz", "Ağustos", "Eylül", "Ekim", "Kasım", "Aralık"};
        double[] values = {12500.50, 9800.75, 15300.20, 11200.00, 13750.30, 14500.60,
                16800.90, 15400.25, 13200.80, 14700.45, 15900.70, 17200.15};

        for (int i = 0; i < categories.length; i++) {
            chartData.add(new XYChart.Data<>(categories[i], values[i]));
        }
    }

    private void configureChart() {
        setLegendVisible(false);
//        ((CategoryAxis) getXAxis()).setLabel("");
//        ((NumberAxis) getYAxis()).setLabel("");


        getYAxis().setStyle(" -fx-font-family: 'Poppins';");
        getXAxis().setStyle(" -fx-font-family: 'Poppins';");

        setHorizontalGridLinesVisible(false);
        setVerticalGridLinesVisible(false);
        setHorizontalZeroLineVisible(false);


        setBarGap(5);
        setCategoryGap(20);

        setStyle("-fx-background-color: transparent;");
        lookup(".chart-plot-background").setStyle("-fx-background-color: transparent;");
        lookup(".chart-legend").setStyle("-fx-background-color: transparent;");
        lookup(".chart").setStyle("-fx-background-color: transparent;");
        // Dark mode subscription

        Platform.runLater(() -> {
            store.getState().subscribe(stateRegistry -> {
                boolean darkModeValue = stateRegistry.getState(DarkModeState.class).isEnabled();
                changeTextColor(darkModeValue,getYAxis());
                changeTextColor(darkModeValue,getXAxis());
                Node title = lookup(".chart-title");
                if (title != null) {
                    title.setStyle("-fx-font-size: 18px; -fx-font-family: 'Poppins';");
                    changeTextColor(darkModeValue,title);
                }

                Node xAxisLabel = lookup(".x-axis .axis-label");
                if (xAxisLabel != null) {
                    xAxisLabel.setStyle(" -fx-font-size: 14px; -fx-font-family: 'Poppins';");
                    changeTextColor(darkModeValue,xAxisLabel);
                }

                Node yAxisLabel = lookup(".y-axis .axis-label");
                if (yAxisLabel != null) {
                    yAxisLabel.setStyle(" -fx-font-size: 14px; -fx-font-family: 'Poppins';");
                    changeTextColor(darkModeValue,yAxisLabel);
                }
            });



            chartData.forEach(data -> {
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
                "#8dd80a", "#3aa15a", "#4795e5"
        };

        Platform.runLater(() -> {
            for (int i = 0; i < chartData.size(); i++) {
                XYChart.Data<String, Number> data = chartData.get(i);
                Node node = data.getNode();
                if (node != null) {
                    String color = colors[i % colors.length];
                    node.setStyle(
                            "-fx-background-color: " + color + ";" +
                                    "-fx-background-radius: 10 10 0 0;" +
                                    "-fx-padding: 0;"
                    );
                }
            }
        });
    }

    public void setData(String title, String xAxisLabel, String yAxisLabel, Map<String, Number> data) {
        chartData.clear();
        data.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .forEach(entry -> chartData.add(new XYChart.Data<>(entry.getKey(), entry.getValue())));

        series.setData(chartData);

        setTitle(title);
        ((CategoryAxis) getXAxis()).setLabel(xAxisLabel);
        ((NumberAxis) getYAxis()).setLabel(yAxisLabel);

        applyCustomColors();
        updateTotal();


        setBarGap(5);
        setCategoryGap(20);
        Platform.runLater(this::layout);
    }

    public void setMonthlyData(Map<String, BigDecimal> newData) {
        chartData.clear();
        newData.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .forEach(entry -> chartData.add(new XYChart.Data<>(entry.getKey(), entry.getValue())));

        series.setData(chartData);
        applyCustomColors();
        updateTotal();


        setBarGap(5);
        setCategoryGap(20);
        Platform.runLater(this::layout);
    }

    public ObservableList<XYChart.Data<String, Number>> getChartData() {
        return chartData;
    }

    public void updateData(String category, double value) {
        chartData.stream()
                .filter(data -> data.getXValue().equals(category))
                .findFirst()
                .ifPresent(data -> data.setYValue(value));
        updateTotal();

        setBarGap(5);
        setCategoryGap(20);
        Platform.runLater(this::layout);
    }

    private void updateTotal() {
        if (totalLabel == null) return;

        double total = chartData.stream()
                .mapToDouble(data -> data.getYValue().doubleValue())
                .sum();
        totalLabel.setText(String.format("Total: %.2f", total));
    }

    public Label getTotalLabel() {
        return totalLabel;
    }

    public void setTotalLabel(Label label) {
        this.totalLabel = label;
        updateTotal();
    }
}