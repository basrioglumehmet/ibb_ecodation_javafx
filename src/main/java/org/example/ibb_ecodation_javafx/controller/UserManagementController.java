package org.example.ibb_ecodation_javafx.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.VBox;
import org.example.ibb_ecodation_javafx.ui.input.ShadcnInput;
import org.example.ibb_ecodation_javafx.ui.splitpane.ShadcnSplitPane;
import org.example.ibb_ecodation_javafx.ui.table.DynamicTable;
import org.example.ibb_ecodation_javafx.utils.SceneUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

    private Map<String, String> comboItems;
    private List<List<String>> originalTableData;
    private String nameFilter = "";
    private String roleFilter = "";

    @FXML
    private DynamicTable<String> userTable;

    public void initialize() {
        if (userTable != null) {
            originalTableData = new ArrayList<>();

            userTable.setHeaderText("User Management");
            userTable.setDescriptionText("Manage all user accounts and permissions");

            comboItems = new HashMap<>();
            comboItems.put("add", "Add User");
            comboItems.put("remove", "Remove User");
            comboItems.put("update", "Update User");
            comboItems.put("print", "Print User to Printer");

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

            userTable.watchComboBox().subscribe(pair -> {
                String key = pair.getKey();
                String value = pair.getValue();
                System.out.println("Selected combo action: " + key + " = " + value);
                switch (key) {
                    case "add":
                        // Handle add user
                        break;
                    case "remove":
                        // Handle remove user
                        break;
                    case "update":
                        // Handle update user
                        break;
                }
            });

            userTable.addHeaders("ID", "Username", "Email", "Role");

            addTableData("1", "johndoe", "johndoe@example.com", "Admin");
            addTableData("2", "janedoe", "janedoe@example.com", "Editor");
            addTableData("3", "Mehmet Basrioğlu", "mehmet@example.com", "Editor");
        }
    }

    private void addTableData(String... data) {
        userTable.addData(data);
        originalTableData.add(new ArrayList<>(List.of(data)));
    }

    private void applyFilters() {
        userTable.clearData();

        List<List<String>> filteredData = originalTableData.stream()
                .filter(row -> {
                    boolean nameMatch = nameFilter.isEmpty() ||
                            row.get(1).toLowerCase().contains(nameFilter.toLowerCase());
                    boolean roleMatch = roleFilter.isEmpty() ||
                            row.get(3).toLowerCase().contains(roleFilter.toLowerCase());
                    return nameMatch && roleMatch;
                })
                .collect(Collectors.toList());

        filteredData.forEach(data -> userTable.addData(data.toArray(new String[0])));
    }
}