package org.example.ibb_ecodation_javafx.utils;

import javafx.stage.FileChooser;
import javafx.stage.Stage;
import lombok.experimental.UtilityClass;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.example.ibb_ecodation_javafx.annotation.PdfDefinition;
import org.example.ibb_ecodation_javafx.model.Vat;
import org.example.ibb_ecodation_javafx.ui.table.DynamicTable;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

@UtilityClass
public class ExcelUtil {

    public static <T> void exportToExcel(List<T> dataList, Class<T> clazz) throws IOException {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Excel File");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Excel Files", "*.xlsx")
        );
        fileChooser.setInitialFileName("data_export.xlsx");

        Stage stage = new Stage();
        File file = fileChooser.showSaveDialog(stage);

        if (file == null) {
            return;
        }

        try (XSSFWorkbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Export Data");

            List<Field> pdfFields = new ArrayList<>();
            List<String> headers = new ArrayList<>();

            // Belirtilen sınıftan @PdfDefinition anotasyonuna sahip alanları al
            for (Field field : clazz.getDeclaredFields()) {
                PdfDefinition pdfDef = field.getAnnotation(PdfDefinition.class);
                if (pdfDef != null) {
                    field.setAccessible(true);
                    pdfFields.add(field);
                    headers.add(pdfDef.fieldName());
                }
            }

            // Başlık satırı
            Row headerRow = sheet.createRow(0);
            CellStyle headerStyle = createHeaderStyle(workbook);

            for (int i = 0; i < headers.size(); i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers.get(i));
                cell.setCellStyle(headerStyle);
            }

            // Veri satırları
            CellStyle dataStyle = createDataStyle(workbook);

            for (int i = 0; i < dataList.size(); i++) {
                Row row = sheet.createRow(i + 1);
                T item = dataList.get(i);

                for (int j = 0; j < pdfFields.size(); j++) {
                    Field field = pdfFields.get(j);
                    try {
                        Object value = field.get(item);
                        String cellValue = (value != null) ? value.toString() : "";
                        row.createCell(j).setCellValue(cellValue);
                        row.getCell(j).setCellStyle(dataStyle);
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
            }

            // Otomatik sütun genişliği
            for (int i = 0; i < headers.size(); i++) {
                sheet.autoSizeColumn(i);
            }

            try (FileOutputStream fileOut = new FileOutputStream(file)) {
                workbook.write(fileOut);
            }
        }
    }

    private static List<String> getPdfDefinitionHeaders() {
        List<String> headers = new ArrayList<>();
        Field[] fields = Vat.class.getDeclaredFields();

        // Vat sınıfındaki tüm alanları tara
        for (Field field : fields) {
            PdfDefinition pdfDefinition = field.getAnnotation(PdfDefinition.class);
            if (pdfDefinition != null) {
                headers.add(pdfDefinition.fieldName()); // fieldName değerini başlık olarak ekle
            }
        }
        return headers;
    }

    private static CellStyle createHeaderStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        font.setColor(IndexedColors.WHITE.getIndex());
        style.setFont(font);
        style.setFillForegroundColor(IndexedColors.GREY_50_PERCENT.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        return style;
    }

    private static CellStyle createDataStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setColor(IndexedColors.BLACK.getIndex());
        style.setFont(font);
        style.setAlignment(HorizontalAlignment.LEFT);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        return style;
    }
}
