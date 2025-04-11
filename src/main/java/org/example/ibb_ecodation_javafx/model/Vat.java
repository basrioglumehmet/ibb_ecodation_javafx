package org.example.ibb_ecodation_javafx.model;

import lombok.*;
import org.example.ibb_ecodation_javafx.annotation.JdbcNamedField;
import org.example.ibb_ecodation_javafx.annotation.PdfDefinition;
import org.example.ibb_ecodation_javafx.annotation.PdfIgnore;
import org.example.ibb_ecodation_javafx.core.db.Entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@ToString
public class Vat implements Entity {

    @PdfDefinition(fieldName = "vat.id")
    @JdbcNamedField(dbFieldName = "id")
    private int id;

    @PdfDefinition(fieldName = "vat.userId")
    @JdbcNamedField(dbFieldName = "user_id")
    private int userId;

    @PdfDefinition(fieldName = "vat.amount")
    @JdbcNamedField(dbFieldName = "base_amount")
    private BigDecimal baseAmount;

    @PdfDefinition(fieldName = "%")
    @JdbcNamedField(dbFieldName = "rate")
    private BigDecimal rate;

    @PdfDefinition(fieldName = "vat.total")
    @JdbcNamedField(dbFieldName = "amount")
    private BigDecimal amount;

    @PdfDefinition(fieldName = "vat.generalTotal")
    @JdbcNamedField(dbFieldName = "total_amount")
    private BigDecimal totalAmount;

    @PdfDefinition(fieldName = "vat.receiptNumber")
    @JdbcNamedField(dbFieldName = "receipt_number")
    private String receiptNumber;

    @PdfDefinition(fieldName = "vat.transactionDate")
    @JdbcNamedField(dbFieldName = "transaction_date")
    private LocalDateTime transactionDate;

    @PdfDefinition(fieldName = "description")
    @JdbcNamedField(dbFieldName = "description")
    private String description;

    @PdfDefinition(fieldName = "vat.exportFormat")
    @JdbcNamedField(dbFieldName = "exportFormat")
    @PdfIgnore
    private String exportFormat = "VARSAYILAN";

    @PdfDefinition(fieldName = "vat.isDeleted")
    @JdbcNamedField(dbFieldName = "is_deleted")
    @PdfIgnore
    private boolean isDeleted = false;

    @PdfDefinition(fieldName = "vat.version")
    @JdbcNamedField(dbFieldName = "version")
    @PdfIgnore
    private int version = 1;
}