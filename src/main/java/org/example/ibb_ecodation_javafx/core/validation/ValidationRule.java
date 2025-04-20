package org.example.ibb_ecodation_javafx.core.validation;

import org.example.ibb_ecodation_javafx.ui.ValidatableComponent;

public interface ValidationRule<T> {
    T getValue();
    boolean validate(T value);
    String getErrorMessage();
    ValidatableComponent getComponent();
}