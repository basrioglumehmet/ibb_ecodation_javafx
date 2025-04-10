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
import org.example.ibb_ecodation_javafx.ui.table.DynamicTable;

import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import java.awt.print.PrinterJob;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.TimeZone;

/**
 * PDF belgelerini dışa aktarma ve yazdırma için yardımcı sınıf
 */
public class PdfExportUtil {

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    private static final float PAGE_WIDTH = PageSize.A4.getWidth() - 72; // A4 width (595) minus default margins (36 each side)
    private static final float MIN_WIDTH = 20f; // Minimum width for any column
    private static final float ID_WIDTH_FACTOR = 0.5f; // Reduce ID column width by 50%

    static {
        DATE_FORMAT.setTimeZone(TimeZone.getTimeZone("Europe/Istanbul")); // TRT (UTC+03:00)
    }

    /**
     * PDF dosyasını varsayılan sistem yazıcısından yazdırır
     * @param pdfFile Yazdırılacak PDF dosyası
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

    private static <T> void addGenericTable(Document doc, List<T> dataList, List<String> headers) {
        if (headers.isEmpty()) {
            doc.add(new Paragraph("Tablo başlıkları bulunamadı."));
            return;
        }

        // Dinamik sütun genişliklerini hesapla
        float[] columnWidths = new float[headers.size()];
        Class<?> clazz = dataList.isEmpty() ? Object.class : dataList.get(0).getClass();
        float totalCharLength = 0f;

        for (int i = 0; i < headers.size(); i++) {
            String headerText = getFieldNameFromAnnotation(clazz, headers.get(i));
            float charLength = headerText.length();
            if ("ID".equals(headerText)) {
                charLength *= ID_WIDTH_FACTOR; // ID sütununu küçült
            }
            columnWidths[i] = charLength;
            totalCharLength += charLength;
        }

        float scaleFactor = totalCharLength > 0 ? (PAGE_WIDTH - headers.size() * MIN_WIDTH) / totalCharLength : 1f;
        for (int i = 0; i < columnWidths.length; i++) {
            columnWidths[i] = Math.max(MIN_WIDTH, columnWidths[i] * scaleFactor + MIN_WIDTH);
        }

        Table pdfTable = new Table(columnWidths);

        headers.forEach(header -> pdfTable.addHeaderCell(
                new Cell().add(new Paragraph(getFieldNameFromAnnotation(clazz, header)).setBold())
        ));

        for (T data : dataList) {
            for (String header : headers) {
                try {
                    Field field = clazz.getDeclaredField(header);
                    field.setAccessible(true);
                    Object value = field.get(data);
                    String displayValue;
                    if (value instanceof Date) {
                        displayValue = DATE_FORMAT.format((Date) value);
                    } else {
                        displayValue = value != null ? value.toString() : "";
                    }
                    System.out.println(displayValue);
                    pdfTable.addCell(new Cell().add(new Paragraph(displayValue)));
                } catch (Exception e) {
                    pdfTable.addCell(new Cell().add(new Paragraph("N/A")));
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

        // Gerekli alanları bul
        for (Field field : clazz.getDeclaredFields()) {
            PdfDefinition annotation = field.getAnnotation(PdfDefinition.class);
            if (annotation != null) {
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
                // Alan değerlerini al
                BigDecimal baseAmount = new BigDecimal(baseAmountField.get(data).toString());
                BigDecimal vatRate = new BigDecimal(vatRateField.get(data).toString())
                        .divide(BigDecimal.valueOf(100), 4, BigDecimal.ROUND_HALF_UP);
                BigDecimal vatAmount = baseAmount.multiply(vatRate);
                BigDecimal totalAmount = baseAmount.add(vatAmount);

                // Verideki değerlerle kontrol et
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

        // Toplamları PDF'ye ekle
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
    // Resim yükleme ve boyut ayarlama fonksiyonu
    public static Image createImageFromClasspath(String imagePath, float width, float height) {
        try {
            // Classpath'teki resim yolunu kullanarak ImageData oluşturuyoruz
            ImageData imageData = ImageDataFactory.create(Objects.requireNonNull(PdfExportUtil.class.getResource(imagePath)));

            // Image nesnesi oluşturuyoruz
            Image image = new Image(imageData);

            // Boyut ayarlamaları
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

        // PdfPTable ile bir tablo oluşturuyoruz
        Table table = new Table(3); // 3 sütunlu bir tablo
        table.setBorder(Border.NO_BORDER);
        table.setMarginBottom(10f);
        Cell imageCell = new Cell().add(createImageFromClasspath(ibbLogo, 60, 60));
        imageCell.setBorder(Border.NO_BORDER);
        table.addCell(imageCell);

        // Tabloyu belgeye ekleyelim
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