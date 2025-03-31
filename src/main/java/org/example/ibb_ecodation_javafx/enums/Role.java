package org.example.ibb_ecodation_javafx.enums;
public enum Role {
    USER("User"),
    MODERATOR("MODERATOR"),
    ADMIN("ADMIN");

    private final String description;

    Role(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return description;
    }

    public static Role fromString(String role) {
        for (Role r : Role.values()) {
            if (r.toString().equalsIgnoreCase(role)) {
                return r;
            }
        }
        throw new IllegalArgumentException("Invalid role: " + role);
    }
}
