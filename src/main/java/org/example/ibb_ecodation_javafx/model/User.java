package org.example.ibb_ecodation_javafx.model;

import lombok.*;
import org.example.ibb_ecodation_javafx.core.db.Entity;
import org.example.ibb_ecodation_javafx.model.enums.Role;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class User implements Entity {
    private int id;
    private String username;
    private String email;
    private String password;
    private Role role;
    private boolean is_verified;
    private boolean is_locked;
    private int version;
}
