package org.example.ibb_ecodation_javafx.ui.listItem;

import io.reactivex.rxjava3.disposables.Disposable;
import javafx.animation.ScaleTransition;
import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.*;
import javafx.scene.paint.Paint;
import javafx.util.Duration;
import org.example.ibb_ecodation_javafx.model.JsonBackup;
import org.example.ibb_ecodation_javafx.statemanagement.Store;
import org.example.ibb_ecodation_javafx.statemanagement.state.DarkModeState;
import org.example.ibb_ecodation_javafx.ui.button.ShadcnButton;
import org.example.ibb_ecodation_javafx.ui.codehighlighter.JsonSyntaxHighlighter;
import org.example.ibb_ecodation_javafx.utils.FontAwesomeUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * JSON yedek verilerini listeleyen bir bileşen.
 * Her yedek için bir kart oluşturur ve JSON verisini syntax highlighting ile gösterir.
 */
public class ShadcnBackupList extends ScrollPane {
    private final GridPane gridPane;
    private final List<JsonBackup> backups;
    private static final int COLUMNS = 2;
    private Button plusCard;
    private Label pageTitle;
    private javafx.event.EventHandler<javafx.event.ActionEvent> plusCardAction;
    private Consumer<JsonBackup> downloadButtonAction;
    private Consumer<JsonBackup> removeBackupAction;
    private Store store = Store.getInstance();
    private Disposable darkModeSubscription;
    private final String pageTitleText;
    private final String newBackupLabel;
    private final String downloadButtonText;
    private final String removeButtonText;

    /**
     * ShadcnBackupList sınıfının yapıcı metodu.
     * Dil servisi bağımlılığı olmadan, dışarıdan sağlanan metinlerle çalışır.
     *
     * @param pageTitleText     Liste başlığı
     * @param newBackupLabel    Yeni yedek ekleme düğmesi etiketi
     * @param downloadButtonText  Güncelle düğmesi etiketi
     * @param removeButtonText  Sil düğmesi etiketi
     */
    public ShadcnBackupList(String pageTitleText, String newBackupLabel, String downloadButtonText, String removeButtonText) {
        this.pageTitleText = pageTitleText;
        this.newBackupLabel = newBackupLabel;
        this.downloadButtonText = downloadButtonText;
        this.removeButtonText = removeButtonText;

        pageTitle = new Label(pageTitleText);
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

        backups = new ArrayList<>();
        this.setContent(container);
        this.setFitToWidth(true);
        this.setVbarPolicy(ScrollBarPolicy.AS_NEEDED);
        this.setHbarPolicy(ScrollBarPolicy.NEVER);
        this.setStyle("-fx-background-color: transparent; -fx-padding: 15;");

        updateGrid(false);

        darkModeSubscription = store.getState().subscribe(stateRegistry -> {
            updateTitleStyle();
            updateGrid(false);
        });
    }

    /**
     * Başlık stilini günceller.
     * Karanlık mod durumuna göre başlık rengini ayarlar.
     */
    private void updateTitleStyle() {
        boolean isDarkMode = store.getCurrentState(DarkModeState.class).isEnabled();
        pageTitle.setStyle("-fx-font-size: 24; -fx-font-family: 'Poppins';" +
                String.format("-fx-text-fill: %s;", isDarkMode ? "#fff" : "#000"));
    }

    /**
     * Yeni bir JSON yedek verisi ekler.
     *
     * @param jsonBackup Eklenecek JSON yedek verisi
     */
    public void addBackup(JsonBackup jsonBackup) {
        backups.add(jsonBackup);
        addNewCardWithAnimation(jsonBackup);
    }

    /**
     * Tüm yedek verilerini temizler.
     */
    public void clearBackups() {
        backups.clear();
        updateGrid(false);
    }

    /**
     * GridPane'i günceller ve tüm kartları yeniden oluşturur.
     *
     * @param animateAll Tüm kartlar için animasyon oynatılıp oynatılmayacağı
     */
    private void updateGrid(boolean animateAll) {
        gridPane.getChildren().clear();
        for (int i = 0; i < backups.size(); i++) {
            JsonBackup backup = backups.get(i);
            VBox card = createBackupCard(backup);
            int row = i / COLUMNS;
            int col = i % COLUMNS;
            gridPane.add(card, col, row);
            GridPane.setHgrow(card, Priority.ALWAYS);
            GridPane.setFillWidth(card, true);
            if (animateAll) {
                animateCard(card);
            }
        }
    }

    /**
     * Yeni bir kart ekler ve animasyon oynatır.
     *
     * @param newBackup Yeni JSON yedek verisi
     */
    private void addNewCardWithAnimation(JsonBackup newBackup) {
        VBox card = createBackupCard(newBackup);
        int totalItems = backups.size() - 1;
        int row = totalItems / COLUMNS;
        int col = totalItems % COLUMNS;
        card.setScaleX(0);
        card.setScaleY(0);
        gridPane.add(card, col, row);
        GridPane.setHgrow(card, Priority.ALWAYS);
        GridPane.setFillWidth(card, true);
        animateCard(card);
    }

    /**
     * JSON yedek verisi için bir kart oluşturur.
     *
     * @param backup JSON yedek verisi
     * @return Oluşturulan kart (VBox)
     */
    private VBox createBackupCard(JsonBackup backup) {
        boolean isDarkMode = store.getCurrentState(DarkModeState.class).isEnabled();

        VBox card = new VBox(8);
        card.setStyle("-fx-background-radius: 8;" +
                String.format("-fx-background-color: %s; -fx-padding: 10;" +
                                "-fx-border-radius: 8; -fx-border-width: 1; -fx-border-color: %s;",
                        isDarkMode ? "#202024" : "#f5f5f5",
                        isDarkMode ? "#2c2c30" : "#e4e4e7"));
        card.setMaxWidth(Double.MAX_VALUE);
        card.setMinHeight(200);
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

        Label dateLabel = new Label(backup.getCreatedAt().toString());
        dateLabel.setStyle("-fx-font-size: 13; -fx-font-family: 'Poppins';" +
                String.format("-fx-text-fill: %s;", isDarkMode ? "#fff" : "#000"));

        Label titleLabel = new Label(backup.getHeader());
        titleLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 16; -fx-font-family: 'Poppins';" +
                String.format("-fx-text-fill: %s;", isDarkMode ? "#fff" : "#000"));
        titleLabel.setWrapText(true);

        JsonSyntaxHighlighter jsonHighlighter = new JsonSyntaxHighlighter();
        jsonHighlighter.setCode(backup.getJsonData());
        jsonHighlighter.setDarkMode(isDarkMode);

        HBox actionSection = new HBox(8);
        actionSection.setAlignment(Pos.CENTER_LEFT);

        ShadcnButton downloadButton = createActionButton(
                downloadButtonText,
                ShadcnButton.ButtonType.PRIMARY,
                "CLOUD",
                e -> {
                    if (downloadButtonAction != null) {
                        downloadButtonAction.accept(backup);
                    }
                }
        );

        ShadcnButton removeButton = createActionButton(
                removeButtonText,
                ShadcnButton.ButtonType.DESTRUCTIVE,
                "TRASH",
                e -> {
                    if (removeBackupAction != null) {
                        removeBackupAction.accept(backup);
                    }
                }
        );

        actionSection.getChildren().addAll(downloadButton, removeButton);
        card.getChildren().addAll(dateLabel, titleLabel, jsonHighlighter, actionSection);

        return card;
    }

    /**
     * Bir aksiyon düğmesi oluşturur.
     *
     * @param text   Düğme metni
     * @param type   Düğme tipi (PRIMARY veya DESTRUCTIVE)
     * @param icon   Düğme ikonu
     * @param action Düğmeye tıklandığında çalışacak aksiyon
     * @return Oluşturulan düğme
     */
    private ShadcnButton createActionButton(String text, ShadcnButton.ButtonType type, String icon,
                                            javafx.event.EventHandler<javafx.event.ActionEvent> action) {
        ShadcnButton button = new ShadcnButton(text, type, icon, true, false, "center");
        button.setOnAction(action);
        return button;
    }

    /**
     * Kart için animasyon oynatır.
     *
     * @param card Animasyon oynatılacak kart
     */
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


    /**
     * Artı (+) düğmesini döndürür.
     *
     * @return Artı düğmesi
     */
    public Button getPlusCard() {
        return plusCard;
    }

    /**
     * Artı (+) düğmesi için aksiyon ayarlar.
     *
     * @param action Düğmeye tıklandığında çalışacak aksiyon
     */
    public void setPlusCardAction(javafx.event.EventHandler<javafx.event.ActionEvent> action) {
        this.plusCardAction = action;
        if (plusCard != null) {
            plusCard.setOnAction(action);
        }
    }

    /**
     * Yedek güncelleme aksiyonunu ayarlar.
     *
     * @param action Güncelleme aksiyonu
     */
    public void setDownloadButtonAction(Consumer<JsonBackup> action) {
        this.downloadButtonAction = action;
    }

    /**
     * Yedek silme aksiyonunu ayarlar.
     *
     * @param action Silme aksiyonu
     */
    public void setRemoveBackupAction(Consumer<JsonBackup> action) {
        this.removeBackupAction = action;
    }

    /**
     * Abonelikleri temizler.
     * Karanlık mod aboneliğini sonlandırır.
     */
    public void dispose() {
        if (darkModeSubscription != null && !darkModeSubscription.isDisposed()) {
            darkModeSubscription.dispose();
        }
    }
}