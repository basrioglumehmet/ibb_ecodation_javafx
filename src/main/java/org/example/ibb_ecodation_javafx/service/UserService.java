package org.example.ibb_ecodation_javafx.service;

import javafx.stage.Window;
import org.example.ibb_ecodation_javafx.core.service.GenericService;
import org.example.ibb_ecodation_javafx.model.User;

import java.util.List;

public interface UserService extends GenericService<User,Integer> {

    /**
     * Yeni bir toplu batch kayıt oluşturur.
     *
     * @param entities Oluşturulacak nesneler
     */
    void saveAll(List<User> entities);

    List<User> loadBackup(Window window);


}
