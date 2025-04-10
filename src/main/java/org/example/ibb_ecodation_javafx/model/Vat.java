package org.example.ibb_ecodation_javafx.model;

import lombok.*;
import org.example.ibb_ecodation_javafx.annotation.JdbcNamedField;
import org.example.ibb_ecodation_javafx.annotation.PdfDefinition;
import org.example.ibb_ecodation_javafx.core.db.Entity;

import java.math.BigDecimal;
import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@ToString
public class Vat implements Entity {
    @PdfDefinition(fieldName = "ID")
    @JdbcNamedField(dbFieldName = "id")
    private int id;

    @PdfDefinition(fieldName = "Tutar")
    @JdbcNamedField(dbFieldName = "baseAmount")
    private BigDecimal baseAmount;

    @PdfDefinition(fieldName = "%")
    @JdbcNamedField(dbFieldName = "rate")
    private BigDecimal rate;

    @PdfDefinition(fieldName = "Toplam")
    @JdbcNamedField(dbFieldName = "amount")
    private BigDecimal amount;

    @PdfDefinition(fieldName = "Genel Toplam")
    @JdbcNamedField(dbFieldName = "totalAmount")
    private BigDecimal totalAmount;

    @PdfDefinition(fieldName = "Fiş Numarası")
    @JdbcNamedField(dbFieldName = "receiptNumber")
    private String receiptNumber;

    @PdfDefinition(fieldName = "İşlem Tarihi")
    @JdbcNamedField(dbFieldName = "transactionDate")
    private Date transactionDate;

    @PdfDefinition(fieldName = "Açıklama")
    @JdbcNamedField(dbFieldName = "description")
    private String description;

    @PdfDefinition(fieldName = "Dışa Aktarma Formatı")
    @JdbcNamedField(dbFieldName = "exportFormat")
    private String exportFormat;

    @PdfDefinition(fieldName = "Silindi mi")
    @JdbcNamedField(dbFieldName = "is_deleted")
    private boolean isDeleted = false;
}