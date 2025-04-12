package org.example.ibb_ecodation_javafx.statemanagement.enums;

public enum CountryCode {
    TR("tr"),
    EN("en");

    private final String code;

    // Constructor to assign the custom string value
    CountryCode(String code) {
        this.code = code;
    }

    // Getter method for the custom code
    public String getCode() {
        return code;
    }

    @Override
    public String toString() {
        return code;
    }
}
