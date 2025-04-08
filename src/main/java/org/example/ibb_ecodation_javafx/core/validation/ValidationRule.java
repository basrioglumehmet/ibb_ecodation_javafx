package org.example.ibb_ecodation_javafx.core.validation;

import org.example.ibb_ecodation_javafx.ui.input.ShadcnInput;

public interface ValidationRule<T> {
    T getValue();
    boolean validate(T value);
    String getErrorMessage();
    ShadcnInput getInput();
}
