package org.example.ibb_ecodation_javafx.repository.query;

import lombok.experimental.UtilityClass;

@UtilityClass
public class VatQuery {

    public static String CREATE_VAT = "INSERT INTO vat " +
            "(user_id, base_amount, rate, amount, total_amount, receipt_number, transaction_date, description, exportFormat, is_deleted, version) " +
            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

    public static String READ_VAT_BY_ID = "SELECT * FROM vat WHERE id=?";
    public static String SOFT_DELETE_BY_ID = "UPDATE vat SET is_deleted = 1, version = version + 1 WHERE id = ? AND is_deleted = 0";

    public static String READ_ALL_VATS = "SELECT * FROM vat";

    public static String READ_ALL_VATS_BY_USER_ID = "SELECT * FROM vat WHERE user_id=?";

    public static String UPDATE_VAT_BY_ID = "UPDATE vat SET " +
            "user_id=?, base_amount=?, rate=?, amount=?, total_amount=?, receipt_number=?, transaction_date=?, description=?, exportFormat=?, is_deleted=?, version=version+1 " +
            "WHERE id=? AND version=?";

    public static String DELETE_VAT_BY_ID = "DELETE FROM vat WHERE id=?";
}
