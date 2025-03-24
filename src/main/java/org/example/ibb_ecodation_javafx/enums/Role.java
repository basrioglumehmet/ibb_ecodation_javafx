package org.example.ibb_ecodation_javafx.enums;

/**
 * 📌 Kullanıcı Rollerini Tanımlayan Enum
 */
public enum Role {
    STUDENT("Öğrenci"),
    TEACHER("Öğretmen"),
    ADMIN("Yönetici");

    // Field
    private final String description;

    // Parametreli
    Role(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    /**
     * 📌 String’den Enum’a güvenli dönüşüm yapar.
     */
    public static Role fromString(String role) {
        try {
            return Role.valueOf(role.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("❌ Geçersiz rol: " + role);
        }
    }
}