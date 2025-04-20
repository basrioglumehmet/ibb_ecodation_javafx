package org.example.ibb_ecodation_javafx.model;

import lombok.*;
import org.example.ibb_ecodation_javafx.annotation.DbField;
import org.example.ibb_ecodation_javafx.core.db.Entity;

import java.sql.Timestamp;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class AppLog implements Entity {

    @DbField(name = "id")
    private int id;

    @DbField(name = "description")
    private String description;

    @DbField(name = "computer_name")
    private String computerName;

    @DbField(name = "ip_addresses")
    private String ipAddresses;

    @DbField(name = "at_time")
    private Timestamp atTime;

    @DbField(name = "version")
    private int version = 1;
}