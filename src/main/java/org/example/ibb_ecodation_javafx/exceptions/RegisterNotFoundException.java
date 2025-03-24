package org.example.ibb_ecodation_javafx.exceptions;

public class RegisterNotFoundException extends RuntimeException {

    public RegisterNotFoundException() {
        super("Hata! Kullanıcı Kayıtlı Değil");
    }

    public RegisterNotFoundException(String message) {
        super(message);
    }
}