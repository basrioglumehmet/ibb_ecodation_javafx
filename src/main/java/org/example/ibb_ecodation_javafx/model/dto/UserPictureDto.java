package org.example.ibb_ecodation_javafx.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserPictureDto {
    private int userId;
    private byte[] imageData;
}
