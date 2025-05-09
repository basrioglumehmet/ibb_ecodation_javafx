package org.example.ibb_ecodation_javafx.core.validation;

import org.springframework.stereotype.Component;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

@Component
public class FieldValidator {
    private final List<ValidationRule<?>> rules = new ArrayList<>();
    private Consumer<ValidationError> errorCallback = error -> System.out.println(error.getErrorDetail());

    public FieldValidator addRule(ValidationRule<?> rule) {
        rules.add(rule);
        return this;
    }

    public FieldValidator onError(Consumer<ValidationError> errorCallback) {
        this.errorCallback = errorCallback;
        return this;
    }

    public List<ValidationError> runValidatorEngine() {
        List<ValidationError> errors = new ArrayList<>();
        for (ValidationRule<?> rule : rules) {
            Object value = rule.getValue();
            boolean isValid = validateRule(rule, value);
            if (!isValid) {
                ValidationError error = new ValidationError(rule.getComponent(), rule.getErrorMessage());
                errors.add(error);
                if (errorCallback != null) {
                    errorCallback.accept(error);
                }
            }
        }
        return errors;
    }

    @SuppressWarnings("unchecked")
    private <T> boolean validateRule(ValidationRule<T> rule, Object value) {
        return rule.validate((T) value);
    }
}