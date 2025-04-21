package org.example.ibb_ecodation_javafx.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import org.example.ibb_ecodation_javafx.core.service.LanguageService;
import org.example.ibb_ecodation_javafx.statemanagement.Store;
import org.example.ibb_ecodation_javafx.statemanagement.enums.CountryCode;
import org.example.ibb_ecodation_javafx.statemanagement.state.DarkModeState;
import org.example.ibb_ecodation_javafx.statemanagement.state.TranslatorState;
import org.example.ibb_ecodation_javafx.statemanagement.state.UserState;
import org.example.ibb_ecodation_javafx.ui.button.ShadcnButton;
import org.example.ibb_ecodation_javafx.ui.combobox.ShadcnLanguageComboBox;
import org.example.ibb_ecodation_javafx.ui.listItem.ShadcnListItem;
import io.reactivex.rxjava3.disposables.Disposable;

import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
@Controller
public class DashboardConfigController {
    private Store store;

    @FXML private ShadcnListItem themeToggler;

    @FXML private ShadcnListItem languageItem;
    @FXML private Label header;
    @FXML private HBox buttonBox;
    @FXML private ShadcnButton resetButton;

    private Map<String, Disposable> switchButtonSubscriptions = new HashMap<>();
    private Disposable languageSubscription;
    private final LanguageService languageService;
    private String languageCode;


    public void initialize() {

        languageCode = ShadcnLanguageComboBox.getCurrentLanguageCode();
        languageService.loadAll(languageCode);


        dispose();
        store = Store.getInstance();


        languageItem.setHeaderText(languageService.translate("item.language.header"));
        languageItem.setDescriptionText(languageService.translate("item.language.description"));
        languageItem.setGlyphIconName("LANGUAGE");
        themeToggler.setHeaderText(languageService.translate("item.theme.header"));
        themeToggler.setDescriptionText(languageService.translate("item.theme.description"));
        themeToggler.setGlyphIconName("MOON");



        header.setText(languageService.translate("label.admin.settings"));
        resetButton.setText(languageService.translate("button.reset.settings"));


        addSwitchButtonListener(themeToggler);



        store.getState().subscribe(stateRegistry -> {
            boolean darkModeEnabled = stateRegistry.getState(DarkModeState.class).isEnabled();
            themeToggler.getSwitchButton().setValue(darkModeEnabled);
            header.setStyle("-fx-font-size: 24px; -fx-text-fill:" + (!darkModeEnabled ? "black" : "white") + ";");
        });


        if (languageItem.getChildren().size() > 1) {

            languageSubscription = ShadcnLanguageComboBox.watchLanguageValue().subscribe(pair -> {
                languageCode = pair.getKey();
                languageService.loadAll(languageCode);
                store.dispatch(TranslatorState.class,new TranslatorState(CountryCode.fromCode(languageCode)));
                updateUIWithLanguage();
            });
        }
    }

    private void addSwitchButtonListener(ShadcnListItem listItem) {
        if (listItem.getSwitchButton() != null) {
            Disposable subscription = listItem.getSwitchButton().watchIsActive()
                    .subscribe(value -> {
                        System.out.println(languageService.translate("log.switch.state.changed") + " (" + listItem.getId() + "): " + value);
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

    private void updateUIWithLanguage() {
        header.setText(languageService.translate("label.admin.settings"));
        languageItem.setHeaderText(languageService.translate("item.language.header"));
        languageItem.setDescriptionText(languageService.translate("item.language.description"));
        themeToggler.setHeaderText(languageService.translate("item.theme.header"));
        themeToggler.setDescriptionText(languageService.translate("item.theme.description"));
        resetButton.setText(languageService.translate("button.reset.settings"));
    }

    public void dispose() {
        switchButtonSubscriptions.values().forEach(sub -> {
            if (sub != null && !sub.isDisposed()) {
                sub.dispose();
            }
        });
        switchButtonSubscriptions.clear();
        if (languageSubscription != null && !languageSubscription.isDisposed()) {
            languageSubscription.dispose();
        }
    }

    @FXML
    public void reset(){
        store.dispatch(
                DarkModeState.class,
                new DarkModeState(false)
        );
        languageItem.resetLanguage();
        store.dispatch(
                TranslatorState.class,
                new TranslatorState(CountryCode.EN)
        );
    }
    @FXML
    public void onDestroy() {
        dispose();
    }
}
