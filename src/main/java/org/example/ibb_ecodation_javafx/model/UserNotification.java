package org.example.ibb_ecodation_javafx.model;

import lombok.*;
import org.example.ibb_ecodation_javafx.annotation.JdbcNamedField;
import org.example.ibb_ecodation_javafx.core.db.Entity;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@ToString
public class UserNotification implements Entity {
    @JdbcNamedField(dbFieldName = "id")
    private int id;
    @JdbcNamedField(dbFieldName = "user_id")
    private int userId;
    @JdbcNamedField(dbFieldName = "header")
    private String header;
    @JdbcNamedField(dbFieldName = "description")
    private String description;
    @JdbcNamedField(dbFieldName = "type")
    private String type;
    @JdbcNamedField(dbFieldName = "version")
    private int version = 1;
}
