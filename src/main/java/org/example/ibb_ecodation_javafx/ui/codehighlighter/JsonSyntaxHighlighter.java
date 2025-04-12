package org.example.ibb_ecodation_javafx.ui.codehighlighter;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.geometry.Insets;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import org.example.ibb_ecodation_javafx.statemanagement.Store;
import org.example.ibb_ecodation_javafx.statemanagement.state.DarkModeState;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JsonSyntaxHighlighter extends StackPane {

    private final TextFlow textFlow;
    private final StringProperty codeProperty;
    private Store store = Store.getInstance();
    private boolean isDarkMode = store.getCurrentState(DarkModeState.class).isEnabled();
    private final VBox lineNumbers;
    private final VBox contentArea;
    private String defaultTextColor;
    private final ScrollPane scrollPane;

    /**
     * JsonSyntaxHighlighter sınıfının yapıcı metodu.
     * Bu sınıf, JSON verilerini syntax highlighting (sözdizimi vurgulama) ile gösterir.
     */
    public JsonSyntaxHighlighter() {
        textFlow = new TextFlow();
        codeProperty = new SimpleStringProperty("");
        lineNumbers = new VBox();
        contentArea = new VBox();
        scrollPane = new ScrollPane();
        initComponent();
    }

    /**
     * Bileşeni başlatır ve arayüzü oluşturur.
     * Boyutları ayarlar, macOS tarzı kontrol düğmelerini ekler ve kaydırma çubuğunu yapılandırır.
     */
    private void initComponent() {
        setPrefWidth(300);
        setPrefHeight(150);
        setMaxWidth(500);
        setMaxHeight(150);
        setMinWidth(300);
        setMinHeight(150);

        Circle redCircle = new Circle(6, javafx.scene.paint.Color.web("#FF5F5F"));
        Circle yellowCircle = new Circle(6, javafx.scene.paint.Color.web("#FFBD2E"));
        Circle greenCircle = new Circle(6, javafx.scene.paint.Color.web("#28CA41"));
        HBox circles = new HBox(8, redCircle, yellowCircle, greenCircle);
        circles.setPadding(new Insets(10, 0, 0, 10));

        textFlow.setPrefWidth(260);
        textFlow.setPadding(new Insets(2, 5, 2, 5));

        lineNumbers.setPrefWidth(30);
        lineNumbers.setMinWidth(30);
        lineNumbers.setMaxWidth(30);
        lineNumbers.setStyle("-fx-font-family: 'Poppins'; -fx-font-size: 12px; -fx-alignment: center-right; -fx-padding: 2 5 2 5;");

        HBox editorArea = new HBox(lineNumbers, textFlow);
        editorArea.setPrefWidth(300);
        editorArea.setMinWidth(300);
        editorArea.setMaxWidth(300);

        scrollPane.setContent(editorArea);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(false);
        scrollPane.setPrefHeight(110);
        scrollPane.setMaxHeight(110);
        scrollPane.setMinHeight(110);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setStyle("-fx-background-color: transparent; -fx-background: transparent;");

        VBox layout = new VBox(5, circles, scrollPane);
        layout.setPadding(new Insets(8));
        getChildren().add(layout);

        updateTheme();

        codeProperty.addListener((obs, old, newVal) -> highlight(newVal));
        highlight("");
    }

    /**
     * Tema renklerini günceller.
     * Karanlık mod (dark mode) veya açık mod (light mode) durumuna göre arka plan ve metin renklerini ayarlar.
     */
    private void updateTheme() {
        String background = isDarkMode ? "#202024" : "#F5F5F5";
        String border = isDarkMode ? "#2b2b30" : "#D1D1D1";
        String codeBg = isDarkMode ? "#1a1a1e" : "#EDEDED";
        defaultTextColor = isDarkMode ? "#D4D4D4" : "#202024";
        String lineNumberColor = isDarkMode ? "#888888" : "#666666";

        setStyle(String.format(
                "-fx-background-color: %s; -fx-background-radius: 12; -fx-border-radius: 12; -fx-border-color: %s; -fx-border-width: 1;",
                background, border
        ));

        textFlow.setStyle(String.format(
                "-fx-background-color: %s; -fx-background-radius: 8px;",
                codeBg
        ));

        lineNumbers.setStyle(String.format(
                "-fx-font-family: 'Poppins'; -fx-font-size: 12px; -fx-alignment: center-right; -fx-padding: 2 5 2 5; " +
                        "-fx-background-color: %s; -fx-background-radius: 8px 0 0 8px;",
                codeBg
        ));

        scrollPane.setStyle(String.format(
                "-fx-background-color: %s; -fx-background: %s; -fx-background-radius: 8px;",
                codeBg, codeBg
        ));

        for (var child : lineNumbers.getChildren()) {
            if (child instanceof Text lineNumber) {
                lineNumber.setStyle(String.format(
                        "-fx-font-family: 'Poppins'; -fx-font-size: 12px; -fx-fill: %s;",
                        lineNumberColor
                ));
            }
        }
    }

    /**
     * JSON metnini satır satır işler ve syntax highlighting uygular.
     * Her satır için uygun renkleri belirler ve metni TextFlow'a ekler.
     *
     * @param text Vurgulanacak JSON metni
     */
    private void highlight(String text) {
        textFlow.getChildren().clear();
        lineNumbers.getChildren().clear();

        if (text == null || text.isEmpty()) {
            Text emptyLine = new Text("\n");
            emptyLine.setStyle(String.format(
                    "-fx-font-family: 'Poppins'; -fx-font-size: 14px; -fx-fill: %s;",
                    defaultTextColor
            ));
            textFlow.getChildren().add(emptyLine);
            addLineNumber(1);
            return;
        }

        String[] lines = text.split("\n");
        Pattern pattern = Pattern.compile("(\"[^\"]*\"|\\b(true|false|null)\\b|-?\\d+\\.?\\d*|[:,{}\\[\\]])");

        for (int i = 0; i < lines.length; i++) {
            String line = lines[i];
            Matcher matcher = pattern.matcher(line);
            List<Text> textNodes = new ArrayList<>();
            int lastEnd = 0;

            while (matcher.find()) {
                if (matcher.start() > lastEnd) {
                    Text unstyled = new Text(line.substring(lastEnd, matcher.start()));
                    unstyled.setStyle(String.format(
                            "-fx-font-family: 'Poppins'; -fx-font-size: 14px; -fx-fill: %s;",
                            defaultTextColor
                    ));
                    textNodes.add(unstyled);
                }

                String token = matcher.group();
                String styleClass = token.startsWith("\"") ? "string" :
                        token.matches("true|false|null") ? "literal" :
                                token.matches("-?\\d+\\.?\\d*") ? "number" : "punctuation";
                Text styled = new Text(token);
                styled.setStyle(getStyleForClass(styleClass));
                textNodes.add(styled);
                lastEnd = matcher.end();
            }

            if (lastEnd < line.length()) {
                Text unstyled = new Text(line.substring(lastEnd));
                unstyled.setStyle(String.format(
                        "-fx-font-family: 'Poppins'; -fx-font-size: 14px; -fx-fill: %s;",
                        defaultTextColor
                ));
                textNodes.add(unstyled);
            }

            Text newline = new Text("\n");
            newline.setStyle(String.format(
                    "-fx-font-family: 'Poppins'; -fx-font-size: 14px; -fx-fill: %s;",
                    defaultTextColor
            ));
            textNodes.add(newline);

            textFlow.getChildren().addAll(textNodes);

            addLineNumber(i + 1);
        }
    }

    /**
     * Satır numarasını ekler.
     * Her satır için bir numara oluşturur ve lineNumbers VBox'ına ekler.
     *
     * @param number Eklenmek istenen satır numarası
     */
    private void addLineNumber(int number) {
        String lineNumberColor = isDarkMode ? "#888888" : "#666666";
        Text lineNumber = new Text(String.valueOf(number));
        lineNumber.setStyle(String.format(
                "-fx-font-family: 'Poppins'; -fx-font-size: 12px; -fx-fill: %s;",
                lineNumberColor
        ));
        lineNumbers.getChildren().add(lineNumber);
    }

    /**
     * JSON token'larına göre stil belirler.
     * Her token türü (string, literal, number, punctuation) için uygun rengi seçer.
     *
     * @param styleClass Token türü (string, literal, number, punctuation)
     * @return Stil dizesi (CSS formatında)
     */
    private String getStyleForClass(String styleClass) {
        return switch (styleClass) {
            case "string" -> String.format(
                    "-fx-font-family: 'Poppins'; -fx-font-size: 14px; -fx-fill: %s;",
                    isDarkMode ? "#fff" : "#000"
            );
            case "literal" -> String.format(
                    "-fx-font-family: 'Poppins'; -fx-font-size: 14px; -fx-fill: %s;",
                    isDarkMode ? "#3aa15a" : "#2E7D32"
            );
            case "number" -> String.format(
                    "-fx-font-family: 'Poppins'; -fx-font-size: 14px; -fx-fill: %s;",
                    isDarkMode ? "#faa61a" : "#EF6C00"
            );
            case "punctuation" -> String.format(
                    "-fx-font-family: 'Poppins'; -fx-font-size: 14px; -fx-fill: %s;",
                    isDarkMode ? "#D4D4D4" : "#202024"
            );
            default -> String.format(
                    "-fx-font-family: 'Poppins'; -fx-font-size: 14px; -fx-fill: %s;",
                    defaultTextColor
            );
        };
    }

    /**
     * JSON metnini ayarlar.
     * Yeni bir JSON metni alır ve bunu codeProperty'e kaydeder.
     *
     * @param code Ayarlanacak JSON metni
     */
    public void setCode(String code) {
        codeProperty.set(code == null ? "" : code);
    }

    /**
     * Mevcut JSON metnini döndürür.
     *
     * @return Mevcut JSON metni
     */
    public String getCode() {
        return codeProperty.get();
    }

    /**
     * codeProperty özelliğini döndürür.
     * Bu özellik, JSON metnini tutar ve değişiklikleri dinlemek için kullanılabilir.
     *
     * @return codeProperty özelliği
     */
    public StringProperty codeProperty() {
        return codeProperty;
    }

    /**
     * Karanlık modu ayarlar.
     * Karanlık mod (dark mode) veya açık mod (light mode) arasında geçiş yapar.
     *
     * @param darkMode true ise karanlık mod, false ise açık mod
     */
    public void setDarkMode(boolean darkMode) {
        this.isDarkMode = darkMode;
        updateTheme();
        highlight(codeProperty.get());
    }
}