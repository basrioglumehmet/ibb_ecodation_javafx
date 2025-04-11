package org.example.ibb_ecodation_javafx.utils;

import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import javafx.stage.FileChooser;
import javafx.stage.Window;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.printing.PDFPageable;
import org.example.ibb_ecodation_javafx.annotation.PdfDefinition;
import org.example.ibb_ecodation_javafx.annotation.PdfIgnore;
import org.example.ibb_ecodation_javafx.core.service.LanguageService;
import org.example.ibb_ecodation_javafx.ui.combobox.ShadcnLanguageComboBox;
import org.springframework.stereotype.Component;

import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import java.awt.print.PrinterJob;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Generic PDF export and printing utility class
 */
@Component
public class PdfExportUtil {

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy HH:mm");
    private static final float PAGE_WIDTH = PageSize.A4.getWidth() - 72; // A4 width minus margins
    private static final float MIN_WIDTH = 20f; // Minimum column width
    private static final float ID_WIDTH_FACTOR = 0.5f; // Reduce ID column width

    private final LanguageService languageService;

    static {
        DATE_FORMAT.setTimeZone(TimeZone.getTimeZone("Europe/Istanbul")); // TRT (UTC+03:00)
    }

    public PdfExportUtil(LanguageService languageService) {
        this.languageService = languageService;
    }

    /**
     * Prints a PDF file using the default system printer
     */
    public void printPdfFromFile(File pdfFile, String languageCode) {
        Objects.requireNonNull(pdfFile, languageService.translate("error.pdf.file.null"));

        languageService.loadAll(languageCode);

        try (PDDocument document = PDDocument.load(pdfFile)) {
            PrinterJob printJob = PrinterJob.getPrinterJob();
            PrintService printService = PrintServiceLookup.lookupDefaultPrintService();

            if (printService == null) {
                System.out.println(languageService.translate("error.no.printer"));
                return;
            }

            printJob.setPrintService(printService);
            printJob.setPageable(new PDFPageable(document));
            printJob.print();

            System.out.println(languageService.translate("info.pdf.sent.to.printer") + ": " + printService.getName());

        } catch (IOException e) {
            System.err.println(languageService.translate("error.pdf.load") + ": " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println(languageService.translate("error.printing") + ": " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Exports a list of any objects to a PDF with dynamic headers and optional totals
     */
    public <T> File exportToPdf(Window ownerWindow, List<T> dataList, List<String> headerKeys, String bottomMessageKey, String languageCode) {
        if (dataList == null) {
            System.err.println("Data list is null; cannot proceed with PDF export.");
            return null;
        }
        if (headerKeys == null) {
            System.err.println("Header keys are null; cannot proceed with PDF export.");
            return null;
        }

        languageService.loadAll(languageCode);

        File outputFile = showSaveDialog(ownerWindow, languageCode);
        if (outputFile == null) {
            System.out.println(languageService.translate("info.file.save.cancelled"));
            return null;
        }

        try (PdfWriter writer = new PdfWriter(outputFile);
             PdfDocument pdf = new PdfDocument(writer);
             Document doc = new Document(pdf, PageSize.A4)) {

            PdfFont font = loadCustomFont();
            doc.setFont(font).setFontSize(10);

            addHeader(doc);
            addDynamicTable(doc, dataList, headerKeys, languageCode);
            addDynamicTotals(doc, dataList, languageCode);

            String footerMessage = languageService.translate(bottomMessageKey); // bottomMessageKey typo fixed
            addFooter(doc, footerMessage);

            System.out.println(languageService.translate("info.invoice.exported") + ": " + outputFile.getAbsolutePath());
            return outputFile;

        } catch (Exception e) {
            System.err.println(languageService.translate("error.invoice.export") + ": " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Maps an i18n key to the corresponding field name using @PdfDefinition annotations
     */
    private <T> String mapI18nKeyToFieldName(Class<T> clazz, String i18nKey) {
        for (Field field : clazz.getDeclaredFields()) {
            PdfDefinition annotation = field.getAnnotation(PdfDefinition.class);
            if (annotation != null && annotation.fieldName().equals(i18nKey)) {
                return field.getName();
            }
        }
        return i18nKey; // Fallback to the key itself if no mapping found
    }

    /**
     * Adds a dynamic table with headers translated from i18n keys and data from mapped fields
     */
    private <T> void addDynamicTable(Document doc, List<T> dataList, List<String> headerKeys, String languageCode) {
        if (headerKeys.isEmpty()) {
            doc.add(new Paragraph(languageService.translate("error.no.table.headers")));
            return;
        }

        Class<?> clazz = dataList.isEmpty() ? Object.class : dataList.get(0).getClass();

        // Filter out headers marked with @PdfIgnore
        List<String> filteredHeaders = headerKeys.stream()
                .filter(i18nKey -> {
                    String fieldName = mapI18nKeyToFieldName(clazz, i18nKey);
                    try {
                        Field field = clazz.getDeclaredField(fieldName);
                        return field.getAnnotation(PdfIgnore.class) == null;
                    } catch (NoSuchFieldException e) {
                        return true; // Include by default if field not found
                    }
                })
                .collect(Collectors.toList());

        if (filteredHeaders.isEmpty()) {
            doc.add(new Paragraph(languageService.translate("error.no.visible.headers")));
            return;
        }

        // Calculate dynamic column widths based on translated headers
        float[] columnWidths = new float[filteredHeaders.size()];
        float totalCharLength = 0f;

        for (int i = 0; i < filteredHeaders.size(); i++) {
            String i18nKey = filteredHeaders.get(i);
            String translatedHeader = languageService.translate(i18nKey);
            float charLength = translatedHeader.length();
            if (i18nKey.endsWith(".id")) { // Reduce width for ID-like fields
                charLength *= ID_WIDTH_FACTOR;
            }
            columnWidths[i] = charLength;
            totalCharLength += charLength;
        }

        float scaleFactor = totalCharLength > 0 ? (PAGE_WIDTH - filteredHeaders.size() * MIN_WIDTH) / totalCharLength : 1f;
        for (int i = 0; i < columnWidths.length; i++) {
            columnWidths[i] = Math.max(MIN_WIDTH, columnWidths[i] * scaleFactor + MIN_WIDTH);
        }

        Table pdfTable = new Table(columnWidths);

        // Add translated headers
        filteredHeaders.forEach(i18nKey -> pdfTable.addHeaderCell(
                new Cell().add(new Paragraph(languageService.translate(i18nKey)).setBold())
        ));

        // Add data rows
        for (T data : dataList) {
            for (String i18nKey : filteredHeaders) {
                try {
                    String fieldName = mapI18nKeyToFieldName(clazz, i18nKey);
                    Field field = clazz.getDeclaredField(fieldName);
                    field.setAccessible(true);
                    Object value = field.get(data);
                    String displayValue = formatFieldValue(value);
                    pdfTable.addCell(new Cell().add(new Paragraph(displayValue)));
                } catch (Exception e) {
                    pdfTable.addCell(new Cell().add(new Paragraph(languageService.translate("value.na"))));
                    System.err.println(languageService.translate("error.header") + ": " + i18nKey + ", " +
                            languageService.translate("error.message") + ": " + e.getMessage());
                }
            }
        }

        doc.add(pdfTable);
    }

    /**
     * Formats field values based on their type
     */
    private String formatFieldValue(Object value) {
        if (value == null) return "";
        if (value instanceof LocalDateTime localDateTime) {
            return DATE_FORMAT.format(Date.from(localDateTime.atZone(TimeZone.getTimeZone("Europe/Istanbul").toZoneId()).toInstant()));
        }
        if (value instanceof Date date) {
            return DATE_FORMAT.format(date);
        }
        return value.toString();
    }

    /**
     * Adds totals for numeric fields if applicable (e.g., "amount", "total")
     */
    private <T> void addDynamicTotals(Document doc, List<T> dataList, String languageCode) {
        if (dataList.isEmpty()) {
            return;
        }

        Class<?> clazz = dataList.get(0).getClass();
        Map<String, BigDecimal> totals = new LinkedHashMap<>();

        // Identify numeric fields
        for (Field field : clazz.getDeclaredFields()) {
            PdfDefinition annotation = field.getAnnotation(PdfDefinition.class);
            if (annotation != null && field.getAnnotation(PdfIgnore.class) == null) {
                String i18nKey = annotation.fieldName();
                if (field.getType().equals(BigDecimal.class) || field.getType().equals(double.class) || field.getType().equals(Double.class)) {
                    totals.put(i18nKey, BigDecimal.ZERO);
                }
            }
        }

        if (totals.isEmpty()) {
            return;
        }

        // Calculate totals
        for (T data : dataList) {
            for (Map.Entry<String, BigDecimal> entry : totals.entrySet()) {
                String i18nKey = entry.getKey();
                try {
                    String fieldName = mapI18nKeyToFieldName(clazz, i18nKey);
                    Field field = clazz.getDeclaredField(fieldName);
                    field.setAccessible(true);
                    Object value = field.get(data);
                    if (value instanceof BigDecimal bd) {
                        totals.put(i18nKey, entry.getValue().add(bd));
                    } else if (value instanceof Double d) {
                        totals.put(i18nKey, entry.getValue().add(BigDecimal.valueOf(d)));
                    }
                } catch (Exception e) {
                    System.err.println("Error calculating total for " + i18nKey + ": " + e.getMessage());
                }
            }
        }

        // Add total paragraphs with smarter label handling
        String totalSuffix = languageService.translate("label.total");
        for (Map.Entry<String, BigDecimal> entry : totals.entrySet()) {
            String fieldLabel = languageService.translate(entry.getKey());
            String label;
            // Avoid repeating "Total" if the field name already contains it
            if (fieldLabel.toLowerCase().contains("total")) {
                label = fieldLabel; // Use field name alone (e.g., "Grand Total")
            } else {
                label = fieldLabel + " " + totalSuffix; // Append "Total" (e.g., "Amount Total")
            }
            doc.add(new Paragraph(String.format("%s: %.2f", label, entry.getValue().doubleValue())));
        }
    }

    private File showSaveDialog(Window ownerWindow, String languageCode) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(languageService.translate("dialog.save.invoice.title"));
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter(
                languageService.translate("filter.pdf.files"), "*.pdf"));
        fileChooser.setInitialFileName(languageService.translate("default.invoice.filename"));
        return fileChooser.showSaveDialog(ownerWindow);
    }

    private PdfFont loadCustomFont() throws IOException {
        File fontFile = new File(PdfExportUtil.class.getResource("/org/example/ibb_ecodation_javafx/assets/fonts/Poppins-Regular.ttf").getFile());
        return PdfFontFactory.createFont(fontFile.getAbsolutePath());
    }

    public Image createImageFromClasspath(String imagePath, float width, float height, String languageCode) {
        try {
            ImageData imageData = ImageDataFactory.create(Objects.requireNonNull(PdfExportUtil.class.getResource(imagePath)));
            Image image = new Image(imageData);
            image.setWidth(width);
            image.setHeight(height);
            return image;
        } catch (NullPointerException e) {
            System.out.println(languageService.translate("error.image.not.found") + ": " + imagePath);
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private void addHeader(Document doc) {
        String ibbLogo = "/org/example/ibb_ecodation_javafx/assets/Ibb_amblem.png";
        Table table = new Table(3);
        table.setBorder(Border.NO_BORDER);
        table.setMarginBottom(10f);
        Cell imageCell = new Cell().add(createImageFromClasspath(ibbLogo, 60, 60, ShadcnLanguageComboBox.getCurrentLanguageCode()));
        imageCell.setBorder(Border.NO_BORDER);
        table.addCell(imageCell);
        doc.add(table);
    }

    private void addFooter(Document doc, String message) {
        doc.add(new Paragraph(message)
                .setFontSize(9)
                .setItalic()
                .setMarginTop(20));
    }
}