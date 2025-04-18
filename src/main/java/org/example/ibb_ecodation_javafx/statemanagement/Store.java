package org.example.ibb_ecodation_javafx.statemanagement;

import io.reactivex.rxjava3.subjects.BehaviorSubject;
import org.example.ibb_ecodation_javafx.model.dto.UserDetailDto;
import org.example.ibb_ecodation_javafx.statemanagement.enums.CountryCode;
import org.example.ibb_ecodation_javafx.statemanagement.state.*;

import java.util.ArrayList;

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
            initialRegistry.registerState(UserState.class, new UserState(new UserDetailDto(), false, null,
                    null));
            initialRegistry.registerState(DarkModeState.class, new DarkModeState(true));
            initialRegistry.registerState(TranslatorState.class, new TranslatorState(CountryCode.EN));
            initialRegistry.registerState(VatTableState.class, new VatTableState(new ArrayList<>(),null));
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
