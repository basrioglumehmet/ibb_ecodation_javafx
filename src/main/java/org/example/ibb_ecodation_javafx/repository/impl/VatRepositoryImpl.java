package org.example.ibb_ecodation_javafx.repository.impl;

import org.example.ibb_ecodation_javafx.core.repository.impl.GenericRepositoryImpl;
import org.example.ibb_ecodation_javafx.model.UserNotification;
import org.example.ibb_ecodation_javafx.model.Vat;
import org.example.ibb_ecodation_javafx.repository.VatRepository;
import org.example.ibb_ecodation_javafx.repository.query.VatQuery;
import org.example.ibb_ecodation_javafx.service.VatService;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;

@Repository
public class VatRepositoryImpl extends GenericRepositoryImpl<Vat,Integer> implements VatRepository {

    public VatRepositoryImpl(DataSource dataSource, Environment environment) {
        super(dataSource, environment);
    }

    @Override
    protected String getTableName() {
        return "vat";
    }

    @Override
    protected RowMapper<Vat> getRowMapper() {
        return new VatRowMapper();
    }

    @Override
    protected String getInsertSql() {
        return VatQuery.CREATE_VAT;
    }

    @Override
    protected String getUpdateSql() {
        return VatQuery.UPDATE_VAT_BY_ID;
    }

    /**
     * Dikkat! User id göre işlem yapar.
     * @return
     */
    @Override
    protected String getSelectByIdSql() {
        return VatQuery.READ_VAT_BY_ID;
    }

    @Override
    protected String getSelectAllByIdSql() {
        return VatQuery.READ_ALL_VATS_BY_USER_ID;
    }


    @Override
    protected String getSelectAllSql() {
        return VatQuery.READ_ALL_VATS;
    }

    @Override
    protected String getDeleteSql() {
        return VatQuery.DELETE_VAT_BY_ID;
    }

    @Override
    protected Object[] getInsertParams(Vat entity) {
        return new Object[]{
                //(user_id, base_amount, rate, amount, total_amount, receipt_number, transaction_date, description, exportFormat, is_deleted, version)
                entity.getUserId(),
                entity.getBaseAmount(),
                entity.getRate(),
                entity.getAmount(),
                entity.getTotalAmount(),
                entity.getReceiptNumber(),
                entity.getTransactionDate(),
                entity.getDescription(),
                entity.getExportFormat(),
                entity.isDeleted(),
                entity.getVersion()
        };
    }

    @Override
    protected Object[] getUpdateParams(Vat entity) {
        return new Object[]{
                entity.getUserId(),
                entity.getBaseAmount(),
                entity.getRate(),
                entity.getAmount(),
                entity.getTotalAmount(),
                entity.getReceiptNumber(),
                entity.getTransactionDate(),
                entity.getDescription(),
                entity.getExportFormat(),
                entity.isDeleted(),
                entity.getId()
        };
    }

    @Override
    protected Integer getVersion(Vat entity) {
        return entity.getVersion();
    }

    //Inner class
    private static class VatRowMapper implements RowMapper<Vat> {
        @Override
        public Vat mapRow(ResultSet rs, int rowNum) throws SQLException {
            //(user_id, base_amount, rate, amount, total_amount, receipt_number,
            // transaction_date, description, exportFormat, is_deleted, version)
            Vat vat = new Vat();
            vat.setId(rs.getInt("id"));
            vat.setUserId(rs.getInt("user_id"));
            vat.setBaseAmount(rs.getBigDecimal("base_amount"));
            vat.setRate(rs.getBigDecimal("rate"));
            vat.setAmount(rs.getBigDecimal("amount"));
            vat.setTotalAmount(rs.getBigDecimal("total_amount"));
            vat.setReceiptNumber(rs.getString("receipt_number"));
            vat.setTransactionDate(rs.getTimestamp("transaction_date"));
            vat.setDescription(rs.getString("description"));
            vat.setExportFormat(rs.getString("exportFormat"));
            vat.setVersion(rs.getInt("version"));
            return vat;
        }
    }
}
