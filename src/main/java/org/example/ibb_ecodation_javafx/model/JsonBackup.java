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
public class JsonBackup implements Entity {

    @JdbcNamedField(dbFieldName = "id")
    private int id;

    @JdbcNamedField(dbFieldName = "header")
    private String header;

    @JdbcNamedField(dbFieldName = "json_data")
    private String jsonData;

    @JdbcNamedField(dbFieldName = "created_at")
    private LocalDateTime createdAt;

    @JdbcNamedField(dbFieldName = "version")
    private int version = 1;
}
