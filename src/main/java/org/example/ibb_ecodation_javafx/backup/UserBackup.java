package org.example.ibb_ecodation_javafx.backup;

import javafx.stage.Window;
import org.example.ibb_ecodation_javafx.core.backup.Backup;
import org.example.ibb_ecodation_javafx.model.User;
import org.springframework.stereotype.Component;

import java.util.List;

@Component("userBackup")
public class UserBackup extends Backup<User> {

    public void export(List<User> users, Window window) {
        createBackup(users, window);
    }

    public List<User> importBackup(Window window) {
        return loadBackup(window, User.class);
    }
}
