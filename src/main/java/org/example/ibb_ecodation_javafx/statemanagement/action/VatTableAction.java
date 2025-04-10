package org.example.ibb_ecodation_javafx.statemanagement.action;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.example.ibb_ecodation_javafx.model.Vat;
import org.example.ibb_ecodation_javafx.statemanagement.enums.CountryCode;

import java.util.List;

@Getter
@AllArgsConstructor
public class VatTableAction {
    private final List<Vat> vatList;
}
