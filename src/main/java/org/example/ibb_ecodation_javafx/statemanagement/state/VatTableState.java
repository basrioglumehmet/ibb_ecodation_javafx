package org.example.ibb_ecodation_javafx.statemanagement.state;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.example.ibb_ecodation_javafx.model.Vat;
import org.example.ibb_ecodation_javafx.model.dto.UserDetailDto;
import org.example.ibb_ecodation_javafx.statemanagement.AppState;
import org.example.ibb_ecodation_javafx.statemanagement.enums.CountryCode;

import java.util.List;

@AllArgsConstructor
public class VatTableState extends AppState {
    private final List<Vat> vatList;
    private final Vat selectedVatItem;

    public Vat getSelectedVatItem() {
        return selectedVatItem;
    }

    public VatTableState setVatTableList(List<Vat> vatList){
        return new VatTableState(vatList,null);
    }

    public VatTableState setSelectedVatItem(Vat selectedVatItem) {
        return new VatTableState(vatList, selectedVatItem);
    }

    public List<Vat> vatList(){
        return vatList;
    }

    @Override
    public String toString() {
        return String.format("Vat Tablo boyutu: %d", vatList.size());
    }
}
