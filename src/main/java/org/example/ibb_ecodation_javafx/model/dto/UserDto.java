package org.example.ibb_ecodation_javafx.model.dto;

import lombok.*;
import org.example.ibb_ecodation_javafx.model.enums.Role;

@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
public class UserDto {
    private int id;
    private String username;
    private String password;
    private String email;
    private String role;
    private boolean is_verified;
    private boolean is_locked;
    private int version;
}
