package org.example.ibb_ecodation_javafx.statemanagement.action;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.example.ibb_ecodation_javafx.statemanagement.enums.CountryCode;

@Getter
@AllArgsConstructor
public class TranslatorAction {
    private final CountryCode countryCode;
}
