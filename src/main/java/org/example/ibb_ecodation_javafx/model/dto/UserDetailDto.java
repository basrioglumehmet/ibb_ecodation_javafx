package org.example.ibb_ecodation_javafx.model.dto;

import lombok.*;

@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
@Builder
@ToString
public class UserDetailDto {
    private int userId;
    private byte[] profilePicture;
    private String username;
    private String email;
    private String password;
    private String role;
    private boolean isVerified;
    private boolean isLocked;
    private int version;
}
