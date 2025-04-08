package org.example.ibb_ecodation_javafx.utils;

import lombok.experimental.UtilityClass;
import org.example.ibb_ecodation_javafx.core.validation.FieldValidator;
import org.example.ibb_ecodation_javafx.core.validation.ValidationRule;
import org.example.ibb_ecodation_javafx.ui.input.ShadcnInput;

import java.util.Map;

@UtilityClass
public class ValidationUtil {

    /**
     * Inputları ve hata mesajlarını bir arada tutarak, FieldValidator oluşturur.
     * @param inputErrorMap - Inputlar ve onlara ait hata mesajları.
     * @return - FieldValidator objesi.
     */
    public static FieldValidator createValidator(Map<ShadcnInput, String> inputErrorMap) {
        FieldValidator validator = new FieldValidator();

        inputErrorMap.forEach((input, errorMsg) ->
                validator.addRule(createRule(input, errorMsg))
        );

        return validator.onError(error -> {
            // Validation error, input'a error mesajını verir.
            error.getInput().setError(error.getErrorDetail());
        });
    }

    /**
     * Belirtilen input ve hata mesajı ile bir validation kuralı oluşturur.
     * @param input - Kontrol edilen input
     * @param errorMsg - Hata mesajı
     * @return ValidationRule
     */
    private static ValidationRule<String> createRule(ShadcnInput input, String errorMsg) {
        return new ValidationRule<>() {
            @Override
            public String getValue() {
                return input.getText();
            }

            @Override
            public boolean validate(String value) {
                return value != null && !value.isBlank();
            }

            @Override
            public String getErrorMessage() {
                return errorMsg;
            }

            @Override
            public ShadcnInput getInput() {
                return input;
            }
        };
    }

    /**
     * Error mesajlarını temizler.
     * @param inputs - Hataların temizleneceği input alanları.
     */
    public static void clearErrors(ShadcnInput... inputs) {
        for (ShadcnInput input : inputs) {
            input.clearError();
        }
    }
}
