package org.example.ibb_ecodation_javafx.core.result;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class BootResult {
    private boolean isSuccess;
    private String message;
}
