package org.example.ibb_ecodation_javafx.utils;

import com.itextpdf.io.exceptions.IOException;
import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import javafx.stage.FileChooser;
import javafx.stage.Window;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.printing.PDFPageable;
import org.example.ibb_ecodation_javafx.ui.table.DynamicTable;

import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import java.awt.*;
import java.awt.print.Printable;
import java.awt.print.PrinterJob;
import java.io.File;
import java.net.URISyntaxException;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class PdfExportUtil {

    public static File exportVatInvoiceFromDynamicTable(Window ownerWindow, DynamicTable<?> table) {
        File outputFile = showSaveDialog(ownerWindow);
        if (outputFile == null) {
            System.out.println("File save cancelled.");
            return null;  // Eğer dosya kaydetme işlemi iptal edilirse null döndür
        }

        try (PdfWriter writer = new PdfWriter(outputFile);
             PdfDocument pdf = new PdfDocument(writer);
             Document doc = new Document(pdf, PageSize.A4)) {

            PdfFont font = loadCustomFont();
            doc.setFont(font).setFontSize(10);

            addHeader(doc);
            addCustomerInfo(doc);
            addTable(doc, table);
            addTotals(doc, table);
            addFooter(doc);

            System.out.println("Invoice exported to: " + outputFile.getAbsolutePath());

            return outputFile;  // Dosya başarılı bir şekilde oluşturulmuşsa döndürüyoruz

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;  // Hata durumunda null döndürüyoruz
    }


    private static File showSaveDialog(Window ownerWindow) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Invoice PDF");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF Files", "*.pdf"));
        fileChooser.setInitialFileName("invoice.pdf");
        return fileChooser.showSaveDialog(ownerWindow);
    }

    private static PdfFont loadCustomFont() throws URISyntaxException {
        File fontFile = new File(PdfExportUtil.class.getResource("/org/example/ibb_ecodation_javafx/assets/fonts/Poppins-Regular.ttf").toURI());
        try {
            return PdfFontFactory.createFont(fontFile.getAbsolutePath(), PdfEncodings.IDENTITY_H);
        } catch (java.io.IOException e) {
            throw new RuntimeException("Font yüklenemedi.", e);
        }
    }


    private static void addHeader(Document doc) {
        doc.add(new Paragraph("ECODATION LTD. ŞTİ.").setFontSize(14).setBold());
        doc.add(new Paragraph("İstanbul Teknopark\n+90 555 555 55 55\ninfo@ecodation.com").setFontSize(10));
        doc.add(new Paragraph("FATURA / VAT INVOICE").setFontSize(16).setBold().setMarginTop(20));
        doc.add(new Paragraph("Fatura No: 2024-001\nTarih: 10/04/2025").setFontSize(10).setMarginBottom(10));
    }

    private static void addCustomerInfo(Document doc) {
        doc.add(new Paragraph("Müşteri:\nAli Yılmaz\nKaraköy Mahallesi No:5\nİstanbul, Türkiye")
                .setFontSize(10).setMarginBottom(20));
    }

    private static void addTable(Document doc, DynamicTable<?> table) {
        List<String> headers = table.getHeaders();
        List<List<String>> data = table.getData();

        if (headers.isEmpty()) {
            doc.add(new Paragraph("Tablo başlıkları bulunamadı."));
            return;
        }

        float[] columnWidths = new float[headers.size()];
        Arrays.fill(columnWidths, 100f);
        Table pdfTable = new Table(columnWidths);

        for (String header : headers) {
            pdfTable.addHeaderCell(new Cell().add(new Paragraph(header).setBold()));
        }

        for (List<String> row : data) {
            for (int i = 0; i < headers.size(); i++) {
                String cellValue = (i < row.size()) ? row.get(i) : "";
                pdfTable.addCell(new Cell().add(new Paragraph(cellValue)));
            }
        }

        doc.add(pdfTable);
    }private static void addTotals(Document doc, DynamicTable<?> table) {
        // Headers listesini alıyoruz.
        List<String> headers = table.getHeaders();
        List<List<String>> data = table.getData(); // Verileri alıyoruz.

        // "Toplam" ve "%" kolonlarının index'lerini buluyoruz.
        int amountIndex = headers.indexOf("Toplam");
        int vatRateIndex = headers.indexOf("%");

        if (amountIndex == -1 || vatRateIndex == -1) {
            // Eğer "Toplam" veya "%" kolonu yoksa, kullanıcıya bir hata mesajı gösterelim.
            doc.add(new Paragraph("Toplam veya KDV Oranı kolonu bulunamadı!"));
            return;
        }

        double subtotal = 0.0;
        double vatTotal = 0.0;

        // Veriler üzerinden her satırdaki "Toplam" ve "%" kolonlarını alıp hesaplamalara ekliyoruz.
        for (List<String> row : data) {
            String rawAmount = row.get(amountIndex);  // "Toplam" kolonunun değeri.
            String rawVatRate = row.get(vatRateIndex); // "%" kolonunun değeri.

            try {
                // Sayı formatlarını parse ediyoruz.
                NumberFormat format = NumberFormat.getInstance(new Locale("tr", "TR"));
                Number amountNumber = format.parse(rawAmount.trim());
                double amount = amountNumber.doubleValue();

                // KDV oranını alıyoruz (örneğin %18, %20 gibi).
                double vatRate = Double.parseDouble(rawVatRate.trim()) / 100.0;

                // Toplam tutarı ve KDV'yi hesaplıyoruz.
                double vatAmount = amount * vatRate;
                double totalAmount = amount + vatAmount;

                subtotal += amount;
                vatTotal += vatAmount;

            } catch (ParseException e) {
                System.out.println("Sayı formatı çözümlenemedi: " + rawAmount);
            } catch (NumberFormatException e) {
                System.out.println("KDV oranı çözümlenemedi: " + rawVatRate);
            }
        }

        // Genel toplamları PDF'ye ekliyoruz.
        doc.add(new Paragraph(String.format("\nAra Toplam: %.2f TL", subtotal)));
        doc.add(new Paragraph(String.format("KDV Toplamı: %.2f TL", vatTotal)));
        doc.add(new Paragraph(String.format("GENEL TOPLAM: %.2f TL", subtotal + vatTotal)).setBold());
    }
    private static void addFooter(Document doc) {
        doc.add(new Paragraph("\nBu fatura elektronik ortamda oluşturulmuştur.")
                .setFontSize(9)
                .setItalic()
                .setMarginTop(20));
    }

    public static void printPdfFromFile(File pdfFile) {
        try {
            PDDocument document = PDDocument.load(pdfFile);

            PrinterJob printJob = PrinterJob.getPrinterJob();

            PrintService printService = PrintServiceLookup.lookupDefaultPrintService();
            if (printService == null) {
                System.out.println("No printer found. Please ensure a printer is connected.");
                document.close();
                return;
            }

            printJob.setPrintService(printService);

            printJob.setPageable(new PDFPageable(document));

            printJob.print();

            document.close();

            System.out.println("PDF sent to printer: " + printService.getName());

        } catch (IOException e) {
            System.err.println("Error loading PDF file: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("Printing error: " + e.getMessage());
            e.printStackTrace();
        }
    }

}
