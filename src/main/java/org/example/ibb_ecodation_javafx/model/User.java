package org.example.ibb_ecodation_javafx.model;

import lombok.*;
import org.example.ibb_ecodation_javafx.annotation.PdfDefinition;
import org.example.ibb_ecodation_javafx.annotation.PdfIgnore;
import org.example.ibb_ecodation_javafx.core.db.Entity;
import org.example.ibb_ecodation_javafx.model.enums.Role;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class User implements Entity {
    @PdfDefinition(fieldName = "user.id")
    private int id;

    @PdfDefinition(fieldName = "user.username")
    private String username;

    @PdfDefinition(fieldName = "user.email")
    private String email;

    @PdfDefinition(fieldName = "user.password")
    @PdfIgnore
    private String password;

    @PdfDefinition(fieldName = "user.role")
    private Role role = Role.USER;

    @PdfDefinition(fieldName = "user.is_verified")
    @PdfIgnore
    private boolean isVerified = false;

    @PdfDefinition(fieldName = "user.is_locked")
    @PdfIgnore
    private boolean isLocked = false;

    @PdfDefinition(fieldName = "user.version")
    @PdfIgnore
    private int version = 1;
}