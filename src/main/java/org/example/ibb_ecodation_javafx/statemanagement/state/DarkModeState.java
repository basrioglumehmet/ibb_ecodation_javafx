package org.example.ibb_ecodation_javafx.statemanagement.state;

import lombok.AllArgsConstructor;
import org.example.ibb_ecodation_javafx.statemanagement.AppState;


@AllArgsConstructor
public class DarkModeState extends AppState {
    private  boolean isEnabled;

    public DarkModeState setIsEnabled(boolean username) {
        return new DarkModeState(true);
    }

    public boolean isEnabled() {
        return isEnabled;
    }

    @Override
    public String toString() {
        return String.format("Dark Mode: %s",this.isEnabled ? "enabled":"disabled");
    }
}
