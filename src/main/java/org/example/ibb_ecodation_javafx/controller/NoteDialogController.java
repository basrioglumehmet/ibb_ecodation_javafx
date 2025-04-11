package org.example.ibb_ecodation_javafx.controller;

import javafx.fxml.FXML;
import javafx.scene.layout.VBox;
import org.example.ibb_ecodation_javafx.core.context.SpringContext;
import org.example.ibb_ecodation_javafx.model.UserNote;
import org.example.ibb_ecodation_javafx.service.UserNoteService;
import org.example.ibb_ecodation_javafx.statemanagement.Store;
import org.example.ibb_ecodation_javafx.statemanagement.state.DarkModeState;
import org.example.ibb_ecodation_javafx.ui.navbar.ShadcnNavbar;
import org.example.ibb_ecodation_javafx.utils.DialogUtil;

import java.time.LocalDateTime;

import static org.example.ibb_ecodation_javafx.utils.ThemeUtil.changeNavbarColor;
import static org.example.ibb_ecodation_javafx.utils.ThemeUtil.changeRootPaneColor;

public class NoteDialogController {
    @FXML
    private ShadcnNavbar navbar;
    @FXML
    private VBox rootPane;
    private final Store store = Store.getInstance();
    private final UserNoteService userNoteService = SpringContext.getContext().getBean(UserNoteService.class);


    public void initialize(){
        store.getState().subscribe(stateRegistry -> {
            var darkModeValue = stateRegistry.getState(DarkModeState.class).isEnabled();
            changeNavbarColor(darkModeValue, navbar);
            changeRootPaneColor(darkModeValue, rootPane);
        });
    }
    @FXML
    private void closeDialog() {
        DialogUtil.closeDialog();
    }

    @FXML
    private void insert() {
            var entity = new UserNote();
            entity.setId(0);
            entity.setUserId(1);
            entity.setHeader("Hello world");
            entity.setDescription("Hello world");
            entity.setReportAt(LocalDateTime.now());
            userNoteService.create(entity);
    }
}
