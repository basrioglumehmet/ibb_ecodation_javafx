package org.example.ibb_ecodation_javafx.model;

import lombok.*;
import org.example.ibb_ecodation_javafx.annotation.JdbcNamedField;
import org.example.ibb_ecodation_javafx.core.db.Entity;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class AppLog implements Entity {

    @JdbcNamedField(dbFieldName = "id")
    private int id;

    @JdbcNamedField(dbFieldName = "description")
    private String description;

    @JdbcNamedField(dbFieldName = "computer_name")
    private String computerName;

    @JdbcNamedField(dbFieldName = "ip_addresses")
    private String ipAddresses;

    @JdbcNamedField(dbFieldName = "at_time")
    private LocalDateTime atTime;

    @JdbcNamedField(dbFieldName = "version")
    private int version = 1;
}