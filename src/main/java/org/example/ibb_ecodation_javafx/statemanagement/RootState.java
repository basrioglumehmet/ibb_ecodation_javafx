package org.example.ibb_ecodation_javafx.statemanagement;

import org.example.ibb_ecodation_javafx.statemanagement.state.*;

public class RootState extends AppState {
    private final CounterState counterState;
    private final UserState userState;
    private final DarkModeState darkModeState;
    private final TranslatorState translatorState;
    private final VatTableState vatTableState;

    public RootState(CounterState counterState, UserState userState, DarkModeState darkModeState, TranslatorState translatorState, VatTableState vatTableState) {
        this.counterState = counterState;
        this.userState = userState;
        this.darkModeState = darkModeState;
        this.translatorState = translatorState;
        this.vatTableState = vatTableState;
    }

    public CounterState getCounterState() {
        return counterState;
    }

    public UserState getUserState() {
        return userState;
    }

    public DarkModeState getDarkModeState() {
        return darkModeState;
    }

    public TranslatorState getTranslatorState() {
        return translatorState;
    }

    public VatTableState getVatTableState() {
        return vatTableState;
    }


    public RootState updateCounter(CounterState newCounterState) {
        return new RootState(newCounterState, this.userState, this.darkModeState,  this.translatorState, this.vatTableState);
    }

    public RootState updateUser(UserState newUserState) {
        return new RootState(this.counterState, newUserState, this.darkModeState,  this.translatorState, this.vatTableState);
    }

    public RootState updateDarkMode(DarkModeState newDarkModeState) {
        return new RootState(this.counterState, this.userState, newDarkModeState, this.translatorState, this.vatTableState);
    }

    public RootState updateTranslator(TranslatorState newTranslastorState) {
        return new RootState(this.counterState, this.userState, this.darkModeState, newTranslastorState, this.vatTableState);
    }

    public RootState updateVat(VatTableState newVatTableState) {
        return new RootState(this.counterState, this.userState, this.darkModeState, this.translatorState, newVatTableState);
    }

    @Override
    public String toString() {
        return "RootState {" + "\n" +
                "  " + counterState.toString() + "\n" +
                "  " + userState.toString() + "\n" +
                "  " + darkModeState.toString() + "\n" +
                "  " + translatorState.toString() + "\n" +
                "  " + vatTableState.toString() + "\n" +
                "}";
    }

}
