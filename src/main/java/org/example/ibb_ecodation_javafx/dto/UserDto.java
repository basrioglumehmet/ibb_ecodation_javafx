package org.example.ibb_ecodation_javafx.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserDto {
    private Integer id;
    private String username;
    private String password;
    private String email;
}
