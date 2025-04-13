package org.example.ibb_ecodation_javafx.repository.query;

import lombok.experimental.UtilityClass;

@UtilityClass
public class AppLogQuery {

    public static String CREATE_APP_LOG = "INSERT INTO app_logs " +
            "(description, computer_name, ip_addresses, at_time, version) " +
            "VALUES (?, ?, ?, ?, ?)";

    public static String READ_APP_LOG_BY_ID = "SELECT * FROM app_logs WHERE id = ?";

    public static String READ_ALL_APP_LOGS = "SELECT * FROM app_logs";

    public static String UPDATE_APP_LOG_BY_ID = "UPDATE app_logs SET " +
            "description = ?, computer_name = ?, ip_addresses = ?, at_time = ?, version = version + 1 " +
            "WHERE id = ? AND version = ?";

    public static String DELETE_APP_LOG_BY_ID = "DELETE FROM app_logs WHERE id = ?";
}