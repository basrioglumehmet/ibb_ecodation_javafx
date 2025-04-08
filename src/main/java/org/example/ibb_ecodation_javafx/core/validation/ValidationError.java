package org.example.ibb_ecodation_javafx.core.validation;

import lombok.*;
import org.example.ibb_ecodation_javafx.ui.input.ShadcnInput;

@Getter
@Setter
@AllArgsConstructor
@ToString
@EqualsAndHashCode
public class ValidationError {
    private ShadcnInput input;
    private String errorDetail;
}
