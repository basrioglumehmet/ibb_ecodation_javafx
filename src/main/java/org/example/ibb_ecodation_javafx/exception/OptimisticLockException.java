package org.example.ibb_ecodation_javafx.exception;


public class OptimisticLockException extends RuntimeException {
    public OptimisticLockException(String message) {
        super(message);
    }
}
