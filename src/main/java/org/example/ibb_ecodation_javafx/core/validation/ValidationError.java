package org.example.ibb_ecodation_javafx.core.validation;

import lombok.*;
import org.example.ibb_ecodation_javafx.ui.ValidatableComponent;

@Getter
@Setter
@AllArgsConstructor
@ToString
@EqualsAndHashCode
public class ValidationError {
    private ValidatableComponent component;
    private String errorDetail;
}