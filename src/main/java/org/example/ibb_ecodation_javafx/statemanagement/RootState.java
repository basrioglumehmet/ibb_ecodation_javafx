package org.example.ibb_ecodation_javafx.statemanagement;

import org.example.ibb_ecodation_javafx.statemanagement.state.CounterState;
import org.example.ibb_ecodation_javafx.statemanagement.state.DarkModeState;
import org.example.ibb_ecodation_javafx.statemanagement.state.TranslatorState;
import org.example.ibb_ecodation_javafx.statemanagement.state.UserState;

public class RootState extends AppState {
    private final CounterState counterState;
    private final UserState userState;
    private final DarkModeState darkModeState;
    private final TranslatorState translatorState;

    public RootState(CounterState counterState, UserState userState, DarkModeState darkModeState, TranslatorState translatorState) {
        this.counterState = counterState;
        this.userState = userState;
        this.darkModeState = darkModeState;
        this.translatorState = translatorState;
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


    public RootState updateCounter(CounterState newCounterState) {
        return new RootState(newCounterState, this.userState, this.darkModeState,  this.translatorState);
    }

    public RootState updateUser(UserState newUserState) {
        return new RootState(this.counterState, newUserState, this.darkModeState,  this.translatorState);
    }

    public RootState updateDarkMode(DarkModeState newDarkModeState) {
        return new RootState(this.counterState, this.userState, newDarkModeState, this.translatorState);
    }

    public RootState updateTranslator(TranslatorState newTranslastorState) {
        return new RootState(this.counterState, this.userState, this.darkModeState, newTranslastorState);
    }

    @Override
    public String toString() {
        return "RootState {" + "\n" +
                "  " + counterState.toString() + "\n" +
                "  " + userState.toString() + "\n" +
                "  " + darkModeState.toString() + "\n" +
                "  " + translatorState.toString() + "\n" +
                "}";
    }

}
