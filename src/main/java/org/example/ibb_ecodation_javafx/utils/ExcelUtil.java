package org.example.ibb_ecodation_javafx.utils;

import javafx.stage.FileChooser;
import javafx.stage.Window;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.example.ibb_ecodation_javafx.annotation.PdfDefinition;
import org.example.ibb_ecodation_javafx.core.service.LanguageService;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

@Component
public class ExcelUtil {

    private final LanguageService languageService;

    public ExcelUtil(LanguageService languageService) {
        this.languageService = languageService;
    }

    public <T> void exportToExcel(List<T> dataList, Class<T> clazz, String languageCode, Window ownerWindow) throws IOException {
        if (dataList == null) {
            System.err.println("Data list is null; cannot proceed with Excel export.");
            return;
        }

        languageService.loadAll(languageCode);

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(languageService.translate("dialog.save.excel.title"));
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter(
                        languageService.translate("filter.excel.files"),
                        "*.xlsx"
                )
        );
        fileChooser.setInitialFileName(languageService.translate("default.excel.filename"));

        File file = fileChooser.showSaveDialog(ownerWindow);

        if (file == null) {
            System.out.println(languageService.translate("info.file.save.cancelled"));
            return;
        }

        try (XSSFWorkbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet(languageService.translate("sheet.name"));

            List<Field> pdfFields = new ArrayList<>();
            List<String> headers = new ArrayList<>();

            // Get fields with @PdfDefinition and translate headers
            for (Field field : clazz.getDeclaredFields()) {
                PdfDefinition pdfDef = field.getAnnotation(PdfDefinition.class);
                if (pdfDef != null) {
                    field.setAccessible(true);
                    pdfFields.add(field);
                    headers.add(languageService.translate(pdfDef.fieldName()));
                }
            }

            // Header row
            Row headerRow = sheet.createRow(0);
            CellStyle headerStyle = createHeaderStyle(workbook);

            for (int i = 0; i < headers.size(); i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers.get(i));
                cell.setCellStyle(headerStyle);
            }

            // Data rows
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
                        System.err.println("Error accessing field " + field.getName() + ": " + e.getMessage());
                    }
                }
            }

            // Auto-size columns
            for (int i = 0; i < headers.size(); i++) {
                sheet.autoSizeColumn(i);
            }

            try (FileOutputStream fileOut = new FileOutputStream(file)) {
                workbook.write(fileOut);
                System.out.println(languageService.translate("info.excel.exported") + ": " + file.getAbsolutePath());
            }
        } catch (IOException e) {
            System.err.println(languageService.translate("error.excel.export") + ": " + e.getMessage());
            throw e;
        }
    }

    private CellStyle createHeaderStyle(Workbook workbook) {
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

    private CellStyle createDataStyle(Workbook workbook) {
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