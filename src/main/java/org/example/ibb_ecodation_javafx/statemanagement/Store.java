package org.example.ibb_ecodation_javafx.statemanagement;

import io.reactivex.rxjava3.subjects.BehaviorSubject;
import org.example.ibb_ecodation_javafx.statemanagement.enums.CountryCode;
import org.example.ibb_ecodation_javafx.statemanagement.state.CounterState;
import org.example.ibb_ecodation_javafx.statemanagement.state.DarkModeState;
import org.example.ibb_ecodation_javafx.statemanagement.state.TranslatorState;
import org.example.ibb_ecodation_javafx.statemanagement.state.UserState;

public class Store {
    private static Store instance;
    private final BehaviorSubject<StateRegistry> stateSubject;

    private Store(StateRegistry initialRegistry) {
        this.stateSubject = BehaviorSubject.createDefault(initialRegistry);
    }

    public static Store getInstance() {
        if (instance == null) {
            StateRegistry initialRegistry = new StateRegistry();
            initialRegistry.registerState(CounterState.class, new CounterState(0));
            initialRegistry.registerState(UserState.class, new UserState("", false));
            initialRegistry.registerState(DarkModeState.class, new DarkModeState(false));
            initialRegistry.registerState(TranslatorState.class, new TranslatorState(CountryCode.TR));
            instance = new Store(initialRegistry);
        }
        return instance;
    }

    public BehaviorSubject<StateRegistry> getState() {
        return stateSubject;
    }

    public <T extends AppState> void dispatch(Class<T> stateClass, T newState) {
        StateRegistry currentRegistry = stateSubject.getValue();
        currentRegistry.updateState(stateClass, newState);
        stateSubject.onNext(currentRegistry);
    }

    // New method to get the current state directly
    public <T extends AppState> T getCurrentState(Class<T> stateClass) {
        StateRegistry currentRegistry = stateSubject.getValue();
        return currentRegistry.getState(stateClass);
    }
}
