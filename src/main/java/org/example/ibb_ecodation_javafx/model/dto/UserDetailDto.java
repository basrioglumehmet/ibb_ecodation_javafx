package org.example.ibb_ecodation_javafx.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
public class UserDetailDto {
    private int userId;
    private byte[] profilePicture;
    private String username;
    private String email;
    private String role;
    private boolean is_verified;
    private boolean is_locked;
}
