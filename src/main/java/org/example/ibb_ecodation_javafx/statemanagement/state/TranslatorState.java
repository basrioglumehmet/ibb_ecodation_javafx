package org.example.ibb_ecodation_javafx.statemanagement.state;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.example.ibb_ecodation_javafx.statemanagement.AppState;
import org.example.ibb_ecodation_javafx.statemanagement.enums.CountryCode;

@AllArgsConstructor
public class TranslatorState extends AppState {
    private CountryCode countryCode;

    public TranslatorState setCountryCode(CountryCode countryCode){
        return new TranslatorState(countryCode);
    }

    public CountryCode countryCode(){
        return countryCode;
    }

    @Override
    public String toString() {
        return String.format("Ülke kodu: %s", countryCode.toString());
    }
}
