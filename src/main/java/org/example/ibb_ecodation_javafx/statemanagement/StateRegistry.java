package org.example.ibb_ecodation_javafx.statemanagement;

import java.util.HashMap;
import java.util.Map;

public class StateRegistry {
    private final Map<Class<? extends AppState>, AppState> states = new HashMap<>();

    public <T extends AppState> void registerState(Class<T> stateClass, T state) {
        states.put(stateClass, state);
    }

    public <T extends AppState> T getState(Class<T> stateClass) {
        return stateClass.cast(states.get(stateClass));
    }

    public <T extends AppState> void updateState(Class<T> stateClass, T newState) {
        states.put(stateClass, newState);
    }
}
