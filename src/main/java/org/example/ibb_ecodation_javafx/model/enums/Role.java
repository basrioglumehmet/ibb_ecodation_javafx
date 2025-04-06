package org.example.ibb_ecodation_javafx.model.enums;


public enum Role {
    ADMIN("ADMIN"),
    USER("USER");

    private final String description;

    Role(String description) {
        this.description = description;
    }

    public static Role fromString(String role) {
        try {
            return Role.valueOf(role.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("❌ Geçersiz rol: " + role);
        }
    }

    @Override
    public String toString() {
        return description;
    }
}
