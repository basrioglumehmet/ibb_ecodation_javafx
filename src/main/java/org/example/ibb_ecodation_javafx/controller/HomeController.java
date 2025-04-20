package org.example.ibb_ecodation_javafx.controller;

import javafx.fxml.FXML;
import javafx.scene.Parent;
import lombok.RequiredArgsConstructor;
import org.example.ibb_ecodation_javafx.model.enums.Role;
import org.example.ibb_ecodation_javafx.service.UserNotificationService;
import org.example.ibb_ecodation_javafx.statemanagement.Store;
import org.example.ibb_ecodation_javafx.statemanagement.state.UserState;
import org.example.ibb_ecodation_javafx.ui.splitpane.ShadcnSplitPane;
import org.example.ibb_ecodation_javafx.utils.SceneUtil;
import org.springframework.stereotype.Controller;

import java.io.IOException;

@Controller
@RequiredArgsConstructor
public class HomeController {
    @FXML
    private ShadcnSplitPane splitPane;

    private final UserNotificationService userNotificationService;
    private final SceneUtil sceneUtil; // Inject SceneUtil
    private final Store store = Store.getInstance();

    public void initialize() {
        try {
            // Load left panel (user-management-view.fxml)
            Parent userPane = sceneUtil.loadParent("/org/example/ibb_ecodation_javafx/views/user-management-view.fxml");

            // Load right panel (vat-management-view.fxml)
            Parent kdvPane = sceneUtil.loadParent("/org/example/ibb_ecodation_javafx/views/vat-management-view.fxml");

            // Set panels in splitPane
            var userState = store.getCurrentState(UserState.class).getUserDetail();
            if (userState.getRole().equals(Role.USER.toString())) {
                splitPane.toggleLeftContent();
            }
            splitPane.setLeftContent(userPane);
            splitPane.setRightContent(kdvPane);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}