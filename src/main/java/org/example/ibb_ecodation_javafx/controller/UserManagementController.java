package org.example.ibb_ecodation_javafx.controller;

import javafx.fxml.FXML;
import javafx.scene.layout.VBox;
import org.example.ibb_ecodation_javafx.core.context.SpringContext;
import org.example.ibb_ecodation_javafx.model.User;
import org.example.ibb_ecodation_javafx.model.enums.Role;
import org.example.ibb_ecodation_javafx.service.UserService;
import org.example.ibb_ecodation_javafx.statemanagement.Store;
import org.example.ibb_ecodation_javafx.statemanagement.state.UserState;
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

    private Map<String, String> comboItems;
    private List<User> userList = new ArrayList<>();

    private String nameFilter = "";
    private String roleFilter = "";

    public void initialize() {
        if (userTable == null) return;

        userTable.setHeaderText("User Management");
        userTable.setDescriptionText("Manage all user accounts and permissions");

        userTable.addHeaders("ID", "Username", "Email", "Password", "Role", "Verified", "Locked", "Version");

        comboItems = new HashMap<>();
        comboItems.put("add", "Add User");
        comboItems.put("remove", "Remove User");
        comboItems.put("update", "Update User");
        comboItems.put("print", "Print User to Printer");
        comboItems.put("refresh", "Refresh");
        comboItems.put("export_backup", "Export Backup");
        comboItems.put("import_backup", "Import Backup");

        userTable.setComboBoxTitle("Actions");
        userTable.setComboBoxItems(comboItems);

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
                    exportToPdf();
                    break;
                case "refresh":
                    refreshData();
                    break;
                case "export_backup":
                    userService.createBackup(userList,userPane.getScene().getWindow());
                    break;
                case "import_backup":
                    List<User> userBackup = userService.loadBackup(userPane.getScene().getWindow());
                    if (userBackup != null && !userBackup.isEmpty()) {
                        try {
                            for (User user : userBackup) {
                                if (user.getUsername() == null || user.getEmail() == null) {
                                    //    DialogUtil.showErrorPopup("Backup Import", "Invalid user data detected in backup.");
                                    return;
                                }
                                if (user.getRole() == null) {
                                    user.setRole(Role.USER);
                                }
                            }
                            // userService.saveAll(userBackup);
                            // userList.clear();
                            //  userList.addAll(userBackup);
                            for (User user : userBackup) {
                                System.out.println(user.toString());
                            }
                            refreshData();
                            System.out.println("YÜklendi");
                            //DialogUtil.showInfoPopup("Backup Import", "Successfully imported " + userBackup.size() + " users.");
                        } catch (Exception e) {
                            e.printStackTrace();
                            System.out.println("Failed "+e.getMessage());
                            // DialogUtil.showErrorPopup("Backup Import", "Failed to import backup: " + e.getMessage());
                        }
                    } else {
                        // DialogUtil.showErrorPopup("Backup Import", "No users found in the backup or import was cancelled.");
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
        store.dispatch(UserState.class, new UserState(null, store.getCurrentState(UserState.class).isLoggedIn(), null));
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
            store.dispatch(UserState.class, new UserState(store.getCurrentState(UserState.class).getUserDetail(), true, user));
            DialogUtil.showHelpPopup("/org/example/ibb_ecodation_javafx/views/user-update-dialog-view.fxml", "User Update");
            refreshData();
        });
    }
    private void exportToPdf() {
        List<User> dataToExport = new ArrayList<>(userList); // Kullanıcı listesini olduğu gibi al

        List<String> headers = List.of(
                "ID", "Kullanıcı Adı", "Email",
                "Rol"
        );

        File pdf = PdfExportUtil.exportVatInvoiceFromList(
                userPane.getScene().getWindow(),
                dataToExport,
                headers,
                "Bu belge elektronik ortamda oluşturulmuştur."
        );
        if (pdf != null && pdf.exists()) {
            PdfExportUtil.printPdfFromFile(pdf);
        } else {
            System.err.println("PDF dosyası oluşturulamadı!");
        }
    }

}
