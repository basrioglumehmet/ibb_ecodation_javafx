package org.example.ibb_ecodation_javafx.model;

import lombok.*;
import org.example.ibb_ecodation_javafx.annotation.DbField;
import org.example.ibb_ecodation_javafx.annotation.PdfDefinition;
import org.example.ibb_ecodation_javafx.annotation.PdfIgnore;
import org.example.ibb_ecodation_javafx.core.db.Entity;

import java.math.BigDecimal;
import java.sql.Timestamp;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@ToString
public class Vat implements Entity {

    @PdfDefinition(fieldName = "vat.id")
    @DbField(name = "id")
    private int id;

    @PdfDefinition(fieldName = "vat.userId")
    @DbField(name = "user_id")
    private int userId;

    @PdfDefinition(fieldName = "vat.amount")
    @DbField(name = "base_amount")
    private BigDecimal baseAmount;

    @PdfDefinition(fieldName = "%")
    @DbField(name = "rate")
    private BigDecimal rate;

    @PdfDefinition(fieldName = "vat.total")
    @DbField(name = "amount")
    private BigDecimal amount;

    @PdfDefinition(fieldName = "vat.generalTotal")
    @DbField(name = "total_amount")
    private BigDecimal totalAmount;

    @PdfDefinition(fieldName = "vat.receiptNumber")
    @DbField(name = "receipt_number")
    private String receiptNumber;

    @PdfDefinition(fieldName = "vat.transactionDate")
    @DbField(name = "transaction_date")
    private Timestamp transactionDate;

    @PdfDefinition(fieldName = "description")
    @DbField(name = "description")
    private String description;

    @PdfDefinition(fieldName = "vat.exportFormat")
    @DbField(name = "exportFormat")
    @PdfIgnore
    private String exportFormat = "VARSAYILAN";

    @PdfDefinition(fieldName = "vat.isDeleted")
    @DbField(name = "is_deleted")
    @PdfIgnore
    private boolean isDeleted = false;

    @PdfDefinition(fieldName = "vat.version")
    @DbField(name = "version")
    @PdfIgnore
    private int version = 1;
}