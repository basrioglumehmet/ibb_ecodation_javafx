package org.example.ibb_ecodation_javafx.model.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum Role {
    ADMIN("ADMIN"),
    USER("USER");

    private final String description;

    Role(String description) {
        this.description = description;
    }

    @JsonValue
    public String getDescription() {
        return description;
    }

    @JsonCreator
    public static Role fromString(String role) {
        if (role == null) {
            return USER;
        }
        try {
            return Role.valueOf(role.toUpperCase());
        } catch (IllegalArgumentException e) {
            return USER;
        }
    }

    @Override
    public String toString() {
        return description;
    }
}