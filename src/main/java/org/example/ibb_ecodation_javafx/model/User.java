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
    @PdfDefinition(fieldName = "ID")
    @JdbcNamedField(dbFieldName = "id")
    private int id;
    @JdbcNamedField(dbFieldName = "username")
    @PdfDefinition(fieldName = "Kullanıcı Adı")
    private String username;
    @JdbcNamedField(dbFieldName = "email")
    @PdfDefinition(fieldName = "Email")
    private String email;
    @JdbcNamedField(dbFieldName = "password")
    @PdfIgnore
    @PdfDefinition(fieldName = "password")
    private String password;
    @JdbcNamedField(dbFieldName = "role")
    @PdfDefinition(fieldName = "Rol")
    private Role role = Role.USER;
    @JdbcNamedField(dbFieldName = "is_verified")
    @PdfDefinition(fieldName = "is_verified")
    @PdfIgnore
    private boolean isVerified;
    @JdbcNamedField(dbFieldName = "is_locked")
    @PdfIgnore
    @PdfDefinition(fieldName = "is_locked")
    private boolean isLocked;
    @JdbcNamedField(dbFieldName = "version")
    @PdfDefinition(fieldName = "version")
    @PdfIgnore
    private int version = 1;
}
