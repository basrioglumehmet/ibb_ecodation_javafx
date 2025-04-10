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
import org.example.ibb_ecodation_javafx.ui.table.DynamicTable;

import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import java.awt.print.PrinterJob;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.TimeZone;
import java.util.stream.Collectors;

/**
 * PDF belgelerini dışa aktarma ve yazdırma için yardımcı sınıf
 */
public class PdfExportUtil {

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy HH:mm");
    private static final float PAGE_WIDTH = PageSize.A4.getWidth() - 72; // A4 width (595) minus default margins (36 each side)
    private static final float MIN_WIDTH = 20f; // Minimum width for any column
    private static final float ID_WIDTH_FACTOR = 0.5f; // Reduce ID column width by 50%

    static {
        DATE_FORMAT.setTimeZone(TimeZone.getTimeZone("Europe/Istanbul")); // TRT (UTC+03:00)
    }

    /**
     * PDF dosyasını varsayılan sistem yazıcısından yazdırır
     */
    public static void printPdfFromFile(File pdfFile) {
        Objects.requireNonNull(pdfFile, "PDF dosyası null olamaz");

        try (PDDocument document = PDDocument.load(pdfFile)) {
            PrinterJob printJob = PrinterJob.getPrinterJob();
            PrintService printService = PrintServiceLookup.lookupDefaultPrintService();

            if (printService == null) {
                System.out.println("Yazıcı bulunamadı. Lütfen bir yazıcının bağlı olduğundan emin olun.");
                return;
            }

            printJob.setPrintService(printService);
            printJob.setPageable(new PDFPageable(document));
            printJob.print();

            System.out.println("PDF yazıcıya gönderildi: " + printService.getName());

        } catch (IOException e) {
            System.err.println("PDF dosyası yüklenirken hata: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("Yazdırma hatası: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static <T> void addTable(Document doc, DynamicTable<T> table, Class<T> clazz) {
        List<String> headers = table.getHeaders();
        List<List<String>> data = table.getData();

        if (headers.isEmpty()) {
            doc.add(new Paragraph("Tabloda başlık bulunamadı."));
            return;
        }

        float[] columnWidths = new float[headers.size()];
        Arrays.fill(columnWidths, 100f);
        Table pdfTable = new Table(columnWidths);

        headers.forEach(header -> pdfTable.addHeaderCell(
                new Cell().add(new Paragraph(getFieldNameFromAnnotation(clazz, header)).setBold())
        ));
        data.forEach(row -> row.forEach(cellValue -> {
            pdfTable.addCell(new Cell().add(new Paragraph(cellValue)));
            System.out.println(cellValue);
        }));

        doc.add(pdfTable);
    }

    /**
     * Genel listeden KDV faturasını dışa aktarır
     */
    public static <T> File exportVatInvoiceFromList(Window ownerWindow, List<T> dataList, List<String> headers) {
        Objects.requireNonNull(dataList, "Veri listesi null olamaz");
        Objects.requireNonNull(headers, "Başlıklar null olamaz");

        File outputFile = showSaveDialog(ownerWindow);
        if (outputFile == null) {
            System.out.println("Dosya kaydetme iptal edildi.");
            return null;
        }

        try (PdfWriter writer = new PdfWriter(outputFile);
             PdfDocument pdf = new PdfDocument(writer);
             Document doc = new Document(pdf, PageSize.A4)) {

            PdfFont font = loadCustomFont();
            doc.setFont(font).setFontSize(10);

            addHeader(doc);
            addGenericTable(doc, dataList, headers);
            addGenericTotals(doc, dataList);
            addFooter(doc);

            System.out.println("Fatura şu konuma aktarıldı: " + outputFile.getAbsolutePath());
            return outputFile;

        } catch (Exception e) {
            System.err.println("Fatura dışa aktarılırken hata: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    private static String mapFieldNameToField(Class<?> clazz, String header) {
        for (Field field : clazz.getDeclaredFields()) {
            PdfDefinition annotation = field.getAnnotation(PdfDefinition.class);
            if (annotation != null && annotation.fieldName().equals(header)) {
                if (field.getAnnotation(PdfIgnore.class) == null) { // PdfIgnore yoksa eşleştir
                    return field.getName();
                }
            }
        }
        return header; // Eşleşme yoksa veya PdfIgnore varsa header'ı döndür
    }

    private static <T> void addGenericTable(Document doc, List<T> dataList, List<String> headers) {
        if (headers.isEmpty()) {
            doc.add(new Paragraph("Tablo başlıkları bulunamadı."));
            return;
        }

        // @PdfIgnore ile işaretlenmiş başlıkları filtrele
        List<String> filteredHeaders = headers.stream()
                .filter(header -> {
                    String fieldName = mapFieldNameToField(dataList.isEmpty() ? Object.class : dataList.get(0).getClass(), header);
                    try {
                        Field field = dataList.get(0).getClass().getDeclaredField(fieldName);
                        return field.getAnnotation(PdfIgnore.class) == null;
                    } catch (NoSuchFieldException e) {
                        return true; // Alan bulunamazsa varsayılan olarak dahil et
                    }
                })
                .collect(Collectors.toList());

        if (filteredHeaders.isEmpty()) {
            doc.add(new Paragraph("Gösterilecek başlık bulunamadı."));
            return;
        }

        // Dinamik sütun genişliklerini hesapla
        float[] columnWidths = new float[filteredHeaders.size()];
        Class<?> clazz = dataList.isEmpty() ? Object.class : dataList.get(0).getClass();
        float totalCharLength = 0f;

        for (int i = 0; i < filteredHeaders.size(); i++) {
            String headerText = filteredHeaders.get(i);
            float charLength = headerText.length();
            if ("ID".equals(headerText)) {
                charLength *= ID_WIDTH_FACTOR; // ID sütununu küçült
            }
            columnWidths[i] = charLength;
            totalCharLength += charLength;
        }

        float scaleFactor = totalCharLength > 0 ? (PAGE_WIDTH - filteredHeaders.size() * MIN_WIDTH) / totalCharLength : 1f;
        for (int i = 0; i < columnWidths.length; i++) {
            columnWidths[i] = Math.max(MIN_WIDTH, columnWidths[i] * scaleFactor + MIN_WIDTH);
        }

        Table pdfTable = new Table(columnWidths);

        filteredHeaders.forEach(header -> pdfTable.addHeaderCell(
                new Cell().add(new Paragraph(header).setBold())
        ));

        for (T data : dataList) {
            for (String header : filteredHeaders) {
                try {
                    String fieldName = mapFieldNameToField(clazz, header);
                    Field field = clazz.getDeclaredField(fieldName);
                    field.setAccessible(true);
                    Object value = field.get(data);
                    String displayValue;
                    if (value instanceof Date) {
                        displayValue = DATE_FORMAT.format((Date) value); // Direct formatting of Date object
                    } else {
                        displayValue = value != null ? value.toString() : "";
                    }
                    pdfTable.addCell(new Cell().add(new Paragraph(displayValue)));
                } catch (Exception e) {
                    pdfTable.addCell(new Cell().add(new Paragraph("N/A")));
                    System.err.println("Hata - Header: " + header + ", Mesaj: " + e.getMessage());
                }
            }
        }

        doc.add(pdfTable);
    }

    private static String getFieldNameFromAnnotation(Class<?> clazz, String header) {
        try {
            Field field = clazz.getDeclaredField(header);
            PdfDefinition annotation = field.getAnnotation(PdfDefinition.class);
            return annotation != null ? annotation.fieldName() : header;
        } catch (NoSuchFieldException e) {
            return header;
        }
    }

    private static <T> void addGenericTotals(Document doc, List<T> dataList) {
        BigDecimal subtotal = BigDecimal.ZERO; // Temel Tutar toplamı
        BigDecimal vatTotal = BigDecimal.ZERO; // KDV Tutarı toplamı
        BigDecimal grandTotal = BigDecimal.ZERO; // Genel Toplam kontrolü

        if (dataList.isEmpty()) {
            doc.add(new Paragraph("Toplamlar için veri yok"));
            return;
        }

        Class<?> clazz = dataList.get(0).getClass();
        Field baseAmountField = null;
        Field vatRateField = null;
        Field vatAmountField = null;
        Field totalAmountField = null;

        // Gerekli alanları bul, @PdfIgnore ile işaretlenmişleri atla
        for (Field field : clazz.getDeclaredFields()) {
            PdfDefinition annotation = field.getAnnotation(PdfDefinition.class);
            if (annotation != null && field.getAnnotation(PdfIgnore.class) == null) {
                field.setAccessible(true);
                switch (annotation.fieldName()) {
                    case "Tutar":
                        baseAmountField = field;
                        break;
                    case "%":
                        vatRateField = field;
                        break;
                    case "Toplam":
                        vatAmountField = field;
                        break;
                    case "Genel Toplam":
                        totalAmountField = field;
                        break;
                }
            }
        }

        if (baseAmountField == null || vatRateField == null || vatAmountField == null || totalAmountField == null) {
            doc.add(new Paragraph("Gerekli alanlar (Temel Tutar, %, Toplam veya Genel Toplam) bulunamadı"));
            return;
        }

        for (T data : dataList) {
            try {
                BigDecimal baseAmount = new BigDecimal(baseAmountField.get(data).toString());
                BigDecimal vatRate = new BigDecimal(vatRateField.get(data).toString())
                        .divide(BigDecimal.valueOf(100), 4, BigDecimal.ROUND_HALF_UP);
                BigDecimal vatAmount = baseAmount.multiply(vatRate);
                BigDecimal totalAmount = baseAmount.add(vatAmount);

                BigDecimal actualVatAmount = new BigDecimal(vatAmountField.get(data).toString());
                BigDecimal actualTotalAmount = new BigDecimal(totalAmountField.get(data).toString());

                if (vatAmount.compareTo(actualVatAmount) != 0) {
                    System.err.println("Hesaplanan KDV (" + vatAmount + ") ile verideki KDV (" + actualVatAmount + ") eşleşmiyor!");
                }
                if (totalAmount.compareTo(actualTotalAmount) != 0) {
                    System.err.println("Hesaplanan Genel Toplam (" + totalAmount + ") ile verideki Genel Toplam (" + actualTotalAmount + ") eşleşmiyor!");
                }

                subtotal = subtotal.add(baseAmount);
                vatTotal = vatTotal.add(vatAmount);
                grandTotal = grandTotal.add(totalAmount);

            } catch (Exception e) {
                System.err.println("Toplamlar işlenirken hata: " + e.getMessage());
            }
        }

        addTotalParagraphs(doc, subtotal.doubleValue(), vatTotal.doubleValue(), grandTotal.doubleValue());
    }

    private static File showSaveDialog(Window ownerWindow) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Fatura PDF'sini Kaydet");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF Dosyaları", "*.pdf"));
        fileChooser.setInitialFileName("fatura.pdf");
        return fileChooser.showSaveDialog(ownerWindow);
    }

    private static PdfFont loadCustomFont() throws IOException {
        File fontFile = new File(PdfExportUtil.class.getResource("/org/example/ibb_ecodation_javafx/assets/fonts/Poppins-Regular.ttf").getFile());
        return PdfFontFactory.createFont(fontFile.getAbsolutePath());
    }

    public static Image createImageFromClasspath(String imagePath, float width, float height) {
        try {
            ImageData imageData = ImageDataFactory.create(Objects.requireNonNull(PdfExportUtil.class.getResource(imagePath)));
            Image image = new Image(imageData);
            image.setWidth(width);
            image.setHeight(height);
            return image;
        } catch (NullPointerException e) {
            System.out.println("Resim dosyası bulunamadı: " + imagePath);
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static void addHeader(Document doc) {
        String ibbLogo = "/org/example/ibb_ecodation_javafx/assets/Ibb_amblem.png";
        Table table = new Table(3);
        table.setBorder(Border.NO_BORDER);
        table.setMarginBottom(10f);
        Cell imageCell = new Cell().add(createImageFromClasspath(ibbLogo, 60, 60));
        imageCell.setBorder(Border.NO_BORDER);
        table.addCell(imageCell);
        doc.add(table);
    }

    private static void addFooter(Document doc) {
        doc.add(new Paragraph("\nBu fatura elektronik ortamda oluşturulmuştur.")
                .setFontSize(9)
                .setItalic()
                .setMarginTop(20));
    }

    private static void addTotalParagraphs(Document doc, double subtotal, double vatTotal, double grandTotal) {
        doc.add(new Paragraph(String.format("\nAra Toplam: %.2f TL", subtotal)));
        doc.add(new Paragraph(String.format("KDV Toplamı: %.2f TL", vatTotal)));
        doc.add(new Paragraph(String.format("GENEL TOPLAM: %.2f TL", grandTotal)).setBold());
    }
}