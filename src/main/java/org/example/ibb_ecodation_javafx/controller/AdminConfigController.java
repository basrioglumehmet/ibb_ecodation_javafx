package org.example.ibb_ecodation_javafx.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import org.example.ibb_ecodation_javafx.statemanagement.Store;
import org.example.ibb_ecodation_javafx.statemanagement.state.DarkModeState;
import org.example.ibb_ecodation_javafx.ui.listItem.ShadcnListItem;
import io.reactivex.rxjava3.disposables.Disposable;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class AdminConfigController {
    private Store store;

    @FXML
    private ShadcnListItem themeToggler;

    @FXML
    private Label header;

    @FXML
    private ShadcnListItem shadcnListItem2;

    private Map<String, Disposable> switchButtonSubscriptions = new HashMap<>();

    public void initialize() {
        dispose();
        store = Store.getInstance();

        // Add listeners for the switch buttons
        addSwitchButtonListener(themeToggler);
        addSwitchButtonListener(shadcnListItem2);

        store.getState().subscribe(stateRegistry -> {
            themeToggler.getSwitchButton().setValue(stateRegistry.getState(DarkModeState.class).isEnabled());

            header.setStyle("-fx-font-size: 24px; " +
                    String.format("-fx-text-fill:%s;",stateRegistry.getState(DarkModeState.class).isEnabled() ? "black":"white"));
        });
    }

    private void addSwitchButtonListener(ShadcnListItem listItem) {
        if (listItem.getSwitchButton() != null) {
            Disposable subscription = listItem.getSwitchButton().watchIsActive()
                    .subscribe(value -> {
                        System.out.println("Switch state changed (" + listItem.getId() + "): " + value);

                        // Avoid setting the same value that is already in the store
                        if ("themeToggler".equals(listItem.getId())) {
                            boolean currentState = store.getCurrentState(DarkModeState.class).isEnabled();
                            if (currentState != value) {
                                store.dispatch(DarkModeState.class, new DarkModeState(value));
                                listItem.getSwitchButton().setValue(value);
                            }
                        }
                    });

            switchButtonSubscriptions.put(listItem.getId(), subscription);
        }
    }

    public void dispose() {
        switchButtonSubscriptions.values().forEach(sub -> {
            if (sub != null && !sub.isDisposed()) {
                sub.dispose();
            }
        });
        switchButtonSubscriptions.clear();
    }

    @FXML
    public void onDestroy() {
        dispose();
    }
}
