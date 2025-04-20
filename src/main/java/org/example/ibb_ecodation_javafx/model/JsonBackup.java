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
public class JsonBackup implements Entity {

    @DbField(name = "id")
    private int id;

    @DbField(name = "header")
    private String header;

    @DbField(name = "json_data")
    private String jsonData;

    @DbField(name = "created_at")
    private Timestamp createdAt;

    @DbField(name = "version")
    private int version = 1;
}
