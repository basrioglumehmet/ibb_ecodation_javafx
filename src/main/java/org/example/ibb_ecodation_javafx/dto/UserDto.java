package org.example.ibb_ecodation_javafx.dto;

import lombok.*;
import org.example.ibb_ecodation_javafx.enums.Role;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class UserDto {
    private Integer id;
    private String username;
    private String password;
    private String email;
    private Role role;
}
