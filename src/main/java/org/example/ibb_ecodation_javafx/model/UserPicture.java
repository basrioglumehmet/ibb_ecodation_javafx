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
public class UserPicture implements Entity {
    @JdbcNamedField(dbFieldName = "user_id")
    private int userId;
    @JdbcNamedField(dbFieldName = "image_data")
    private byte[] imageData;
    private int version;
}
