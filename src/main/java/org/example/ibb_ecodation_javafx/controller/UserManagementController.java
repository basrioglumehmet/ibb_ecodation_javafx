package org.example.ibb_ecodation_javafx.controller;

import javafx.fxml.FXML;
import javafx.scene.layout.VBox;
import org.example.ibb_ecodation_javafx.core.context.SpringContext;
import org.example.ibb_ecodation_javafx.core.service.LanguageService;
import org.example.ibb_ecodation_javafx.model.User;
import org.example.ibb_ecodation_javafx.model.enums.Role;
import org.example.ibb_ecodation_javafx.service.UserService;
import org.example.ibb_ecodation_javafx.statemanagement.Store;
import org.example.ibb_ecodation_javafx.statemanagement.state.UserState;
import org.example.ibb_ecodation_javafx.ui.combobox.ShadcnLanguageComboBox;
import org.example.ibb_ecodation_javafx.ui.input.ShadcnInput;
import org.example.ibb_ecodation_javafx.ui.splitpane.ShadcnSplitPane;
import org.example.ibb_ecodation_javafx.ui.table.DynamicTable;
import org.example.ibb_ecodation_javafx.utils.DialogUtil;
import org.example.ibb_ecodation_javafx.utils.PdfExportUtil;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

public class UserManagementController {

    @FXML
    private ShadcnSplitPane splitPane;

    @FXML
    private VBox userPane;

    @FXML
    private ShadcnInput name;

    @FXML
    private ShadcnInput role;

    @FXML
    private DynamicTable<String> userTable;

    private final Store store = Store.getInstance();
    private final UserService userService = SpringContext.getContext().getBean(UserService.class);
    private final PdfExportUtil pdfExportUtil = SpringContext.getContext().getBean(PdfExportUtil.class);
    private final LanguageService languageService = SpringContext.getContext().getBean(LanguageService.class);

    private Map<String, String> comboItems;
    private List<User> userList = new ArrayList<>();
    private String nameFilter = "";
    private String roleFilter = "";

    public void initialize() {
        if (userTable == null) return;



        ResourceBundle bundle = languageService.loadAll(ShadcnLanguageComboBox.getCurrentLanguageCode());

        userTable.setHeaderText(bundle.getString("user.header"));
        userTable.setDescriptionText(bundle.getString("user.description"));

        userTable.addHeaders(
                bundle.getString("user.id"),
                bundle.getString("user.username"),
                bundle.getString("user.email"),
                bundle.getString("user.password"),
                bundle.getString("user.role"),
                bundle.getString("user.verified"),
                bundle.getString("user.locked"),
                bundle.getString("user.version")
        );

        comboItems = new HashMap<>();
        comboItems.put("add", bundle.getString("user.add"));
        comboItems.put("remove", bundle.getString("user.remove"));
        comboItems.put("update", bundle.getString("user.update"));
        comboItems.put("print", bundle.getString("user.print"));
        comboItems.put("refresh", bundle.getString("user.refresh"));
        comboItems.put("export_backup", bundle.getString("user.export_backup"));
        comboItems.put("import_backup", bundle.getString("user.import_backup"));

        userTable.setComboBoxTitle(bundle.getString("user.actions"));
        userTable.setComboBoxItems(comboItems);

        name.setHeader(bundle.getString("user.name.filter"));
        role.setHeader(bundle.getString("user.role.filter"));

        name.setTextChangeListener(newValue -> {
            nameFilter = newValue != null ? newValue.trim() : "";
            applyFilters();
        });

        role.setTextChangeListener(newValue -> {
            roleFilter = newValue != null ? newValue.trim() : "";
            applyFilters();
        });

        userTable.setSingleSelection(true);
        userTable.watchComboBox().subscribe(pair -> {
            String actionLanguageCode = ShadcnLanguageComboBox.getCurrentLanguageCode();
            switch (pair.getKey()) {
                case "add":
                    DialogUtil.showHelpPopup("/org/example/ibb_ecodation_javafx/views/user-create-dialog-view.fxml", "User Create");
                    break;
                case "remove":
                    deleteSelectedUser();
                    break;
                case "update":
                    updateSelectedUser();
                    break;
                case "print":
                    exportToPdf(actionLanguageCode);
                    break;
                case "refresh":
                    refreshData();
                    break;
                case "export_backup":
                    userService.createBackup(userList, userPane.getScene().getWindow());
                    break;
                case "import_backup":
                    List<User> userBackup = userService.loadBackup(userPane.getScene().getWindow());
                    if (userBackup != null && !userBackup.isEmpty()) {
                        try {
                            for (User user : userBackup) {
                                if (user.getUsername() == null || user.getEmail() == null) {
                                    System.out.println(languageService.translate("error.invalid.user.data"));
                                    return;
                                }
                                if (user.getRole() == null) {
                                    user.setRole(Role.USER);
                                }
                            }
                            userService.saveAll(userBackup);
                            userList.clear();
                            userList.addAll(userBackup);
                            for (User user : userBackup) {
                                System.out.println(user.toString());
                            }
                            refreshData();
                            System.out.println(languageService.translate("info.backup.loaded"));

                        } catch (Exception e) {
                            e.printStackTrace();
                            System.out.println(languageService.translate("error.backup.failed") + ": " + e.getMessage());
                        }
                    } else {
                        System.out.println(languageService.translate("error.no.users.in.backup"));
                    }
                    break;
            }
        });

        refreshData();
    }

    private void refreshData() {
        userList = userService.readAll();
        userTable.clearData();
        userList.forEach(user -> userTable.addData(userToRow(user)));
        applyFilters();
    }

    private String[] userToRow(User user) {
        return new String[]{
                String.valueOf(user.getId()),
                user.getUsername(),
                user.getEmail(),
                "***", // Masked password
                user.getRole().name(),
                String.valueOf(user.isVerified()),
                String.valueOf(user.isLocked()),
                String.valueOf(user.getVersion())
        };
    }

    private void applyFilters() {
        userTable.clearData();
        userList.stream()
                .filter(user -> user.getUsername().toLowerCase().contains(nameFilter.toLowerCase()))
                .filter(user -> user.getRole().name().toLowerCase().contains(roleFilter.toLowerCase()))
                .forEach(user -> userTable.addData(userToRow(user)));
    }

    private void deleteSelectedUser() {
        List<List<String>> selectedData = userTable.getSelectedData();
        if (selectedData.isEmpty()) return;

        String selectedId = selectedData.get(0).get(0);
        userService.delete(Integer.parseInt(selectedId));
        store.dispatch(UserState.class, new UserState(null, store.getCurrentState(UserState.class).isLoggedIn(), null,store.getCurrentState(UserState.class).getSelectedUserNote()));
        refreshData();
    }

    private void updateSelectedUser() {
        List<List<String>> selectedData = userTable.getSelectedData();
        if (selectedData.isEmpty()) return;

        String selectedId = selectedData.get(0).get(0);
        Optional<User> selectedUser = userList.stream()
                .filter(user -> String.valueOf(user.getId()).equals(selectedId))
                .findFirst();

        selectedUser.ifPresent(user -> {
            store.dispatch(UserState.class, new UserState(store.getCurrentState(UserState.class).getUserDetail(), true, user,store.getCurrentState(UserState.class).getSelectedUserNote()));
            DialogUtil.showHelpPopup("/org/example/ibb_ecodation_javafx/views/user-update-dialog-view.fxml", "User Update");
            refreshData();
        });
    }

    private void exportToPdf(String languageCode) {
        List<User> dataToExport = new ArrayList<>(userList);

        List<String> headers = List.of(
                "user.id",
                "user.username",
                "user.email",
                "user.role"
        );

        File pdf = pdfExportUtil.exportToPdf(
                userPane.getScene().getWindow(),
                dataToExport,
                headers,
                "footer.message",
                languageCode
        );
        if (pdf != null && pdf.exists()) {
            pdfExportUtil.printPdfFromFile(pdf, languageCode);
        } else {
            System.err.println(languageService.translate("error.pdf.creation.failed"));
        }
    }
}