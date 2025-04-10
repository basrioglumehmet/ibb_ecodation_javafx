package org.example.ibb_ecodation_javafx.model;

import lombok.*;
import org.example.ibb_ecodation_javafx.annotation.JdbcNamedField;
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
    @JdbcNamedField(dbFieldName = "id")
    private int id;

    @JdbcNamedField(dbFieldName = "baseAmount")
    private BigDecimal baseAmount;

    @JdbcNamedField(dbFieldName = "rate")
    private BigDecimal rate;

    @JdbcNamedField(dbFieldName = "amount")
    private BigDecimal amount;

    @JdbcNamedField(dbFieldName = "totalAmount")
    private BigDecimal totalAmount;

    @JdbcNamedField(dbFieldName = "receiptNumber")
    private String receiptNumber;

    @JdbcNamedField(dbFieldName = "transactionDate")
    private Date transactionDate;

    @JdbcNamedField(dbFieldName = "description")
    private String description;

    @JdbcNamedField(dbFieldName = "exportFormat")
    private String exportFormat;

    @JdbcNamedField(dbFieldName = "is_deleted")
    private boolean isDeleted = false;
}