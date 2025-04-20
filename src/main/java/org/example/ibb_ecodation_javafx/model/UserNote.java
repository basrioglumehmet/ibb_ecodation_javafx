package org.example.ibb_ecodation_javafx.model;

import lombok.*;
import org.example.ibb_ecodation_javafx.annotation.DbField;
import org.example.ibb_ecodation_javafx.core.db.Entity;

import java.sql.Timestamp;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@ToString
public class UserNote implements Entity {

    @DbField(name = "id")
    private int id;

    @DbField(name = "user_id")
    private int userId;

    @DbField(name = "report_at")
    private Timestamp reportAt;

    @DbField(name = "header")
    private String header;

    @DbField(name = "description")
    private String description;

    @DbField(name = "version")
    private int version = 1;

}
