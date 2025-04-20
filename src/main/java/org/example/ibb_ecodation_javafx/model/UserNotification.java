package org.example.ibb_ecodation_javafx.model;

import lombok.*;
import org.example.ibb_ecodation_javafx.annotation.DbField;
import org.example.ibb_ecodation_javafx.core.db.Entity;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@ToString
public class UserNotification implements Entity {
    @DbField(name = "id")
    private int id;
    @DbField(name = "user_id")
    private int userId;
    @DbField(name = "header")
    private String header;
    @DbField(name = "description")
    private String description;
    @DbField(name = "type")
    private String type;
    @DbField(name = "version")
    private int version = 1;
}
