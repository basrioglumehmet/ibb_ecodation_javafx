package org.example.ibb_ecodation_javafx.model;

import lombok.*;
import org.example.ibb_ecodation_javafx.annotation.JdbcNamedField;
import org.example.ibb_ecodation_javafx.core.db.Entity;
import org.example.ibb_ecodation_javafx.model.enums.Role;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class User implements Entity {
    @JdbcNamedField(dbFieldName = "id")
    private int id;
    @JdbcNamedField(dbFieldName = "username")
    private String username;
    @JdbcNamedField(dbFieldName = "email")
    private String email;
    @JdbcNamedField(dbFieldName = "password")
    private String password;
    @JdbcNamedField(dbFieldName = "role")
    private Role role = Role.USER;
    @JdbcNamedField(dbFieldName = "is_verified")
    private boolean isVerified;
    @JdbcNamedField(dbFieldName = "is_locked")
    private boolean isLocked;
    @JdbcNamedField(dbFieldName = "version")
    private int version = 1;
}
