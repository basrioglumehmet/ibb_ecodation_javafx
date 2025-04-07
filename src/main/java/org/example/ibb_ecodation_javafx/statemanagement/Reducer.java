package org.example.ibb_ecodation_javafx.statemanagement;

import org.example.ibb_ecodation_javafx.statemanagement.action.*;
import org.example.ibb_ecodation_javafx.statemanagement.state.CounterState;
import org.example.ibb_ecodation_javafx.statemanagement.state.DarkModeState;
import org.example.ibb_ecodation_javafx.statemanagement.state.TranslatorState;
import org.example.ibb_ecodation_javafx.statemanagement.state.UserState;

public class Reducer {
    public static RootState reduce(RootState currentState, Object action) {
        if (action instanceof IncrementAction) {
            CounterState newCounterState = currentState.getCounterState().increment();
            return currentState.updateCounter(newCounterState);
        } else if (action instanceof DecrementAction) {
            CounterState newCounterState = currentState.getCounterState().decrement();
            return currentState.updateCounter(newCounterState);
        } else if (action instanceof LoginAction) {
            UserState newUserState = currentState.getUserState().login(((LoginAction) action).getUserDetail());
            return currentState.updateUser(newUserState);
        } else if (action instanceof LogoutAction) {
            UserState newUserState = currentState.getUserState().logout();
            return currentState.updateUser(newUserState);
        }
        else if (action instanceof DarkModeAction) {
            DarkModeState newDarkModeState = currentState.getDarkModeState();
            return currentState.updateDarkMode(newDarkModeState);
        }
        else if (action instanceof TranslatorAction) {
            TranslatorState newTranslatorState = currentState.getTranslatorState();
            return currentState.updateTranslator(newTranslatorState);
        }
        return currentState;
    }
}
