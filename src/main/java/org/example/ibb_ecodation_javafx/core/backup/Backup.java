package org.example.ibb_ecodation_javafx.core.backup;


import javafx.stage.Window;
import org.example.ibb_ecodation_javafx.utils.JsonBackupUtil;

import java.util.List;

public abstract class Backup<T> implements BackupService {
    protected void createBackup(List<T> entities, Window window) {
        JsonBackupUtil.exportToJsonWithDialog(entities, window);
    }

    protected List<T> loadBackup(Window window, Class<T> clazz) {
        return JsonBackupUtil.importFromJsonWithDialog(window, clazz);
    }
}
