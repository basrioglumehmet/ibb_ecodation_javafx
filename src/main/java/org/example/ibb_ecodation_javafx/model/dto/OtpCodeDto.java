package org.example.ibb_ecodation_javafx.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@AllArgsConstructor
@Getter
@Setter
@ToString
public class OtpCodeDto {
    private boolean isSuccess;
    private int ownerId;
}
