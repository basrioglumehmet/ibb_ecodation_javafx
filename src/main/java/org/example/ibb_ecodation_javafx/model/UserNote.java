package org.example.ibb_ecodation_javafx.model;

import lombok.*;
import org.example.ibb_ecodation_javafx.annotation.JdbcNamedField;
import org.example.ibb_ecodation_javafx.annotation.PdfIgnore;
import org.example.ibb_ecodation_javafx.core.db.Entity;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@ToString
public class UserNote implements Entity {

    @JdbcNamedField(dbFieldName = "id")
    private int id;

    @JdbcNamedField(dbFieldName = "user_id")
    private int userId;

    @JdbcNamedField(dbFieldName = "report_at")
    private LocalDateTime reportAt;

    @JdbcNamedField(dbFieldName = "header")
    private String header;

    @JdbcNamedField(dbFieldName = "description")
    private String description;

    @JdbcNamedField(dbFieldName = "version")
    private int version = 1;

}
