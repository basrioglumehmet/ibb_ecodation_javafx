package org.example.ibb_ecodation_javafx.model;

import lombok.*;
import org.example.ibb_ecodation_javafx.annotation.JdbcNamedField;
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
    @JdbcNamedField(dbFieldName = "id")
    private int id;

    @PdfDefinition(fieldName = "user.username")
    @JdbcNamedField(dbFieldName = "username")
    private String username;

    @PdfDefinition(fieldName = "user.email")
    @JdbcNamedField(dbFieldName = "email")
    private String email;

    @PdfDefinition(fieldName = "user.password")
    @PdfIgnore
    @JdbcNamedField(dbFieldName = "password")
    private String password;

    @PdfDefinition(fieldName = "user.role")
    @JdbcNamedField(dbFieldName = "role")
    private Role role = Role.USER;

    @PdfDefinition(fieldName = "user.is_verified")
    @PdfIgnore
    @JdbcNamedField(dbFieldName = "is_verified")
    private boolean isVerified;

    @PdfDefinition(fieldName = "user.is_locked")
    @PdfIgnore
    @JdbcNamedField(dbFieldName = "is_locked")
    private boolean isLocked;

    @PdfDefinition(fieldName = "user.version")
    @PdfIgnore
    @JdbcNamedField(dbFieldName = "version")
    private int version = 1;
}