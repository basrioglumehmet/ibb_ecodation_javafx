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
public class UserPicture implements Entity {
    @DbField(name = "user_id")
    private int userId;
    @DbField(name = "image_data")
    private byte[] imageData;
    @DbField(name = "version")
    private int version = 1;
}
