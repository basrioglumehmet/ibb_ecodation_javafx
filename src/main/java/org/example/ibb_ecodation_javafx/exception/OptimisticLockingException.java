package org.example.ibb_ecodation_javafx.exception;


public class OptimisticLockingException extends RuntimeException {
    public OptimisticLockingException(String message) {
        super(message);
    }
}
