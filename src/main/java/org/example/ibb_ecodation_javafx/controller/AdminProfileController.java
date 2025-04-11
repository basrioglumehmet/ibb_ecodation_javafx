package org.example.ibb_ecodation_javafx.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import org.example.ibb_ecodation_javafx.core.context.SpringContext;
import org.example.ibb_ecodation_javafx.core.logger.SecurityLogger;
import org.example.ibb_ecodation_javafx.service.UserPictureService;
import org.example.ibb_ecodation_javafx.service.UserService;
import org.example.ibb_ecodation_javafx.statemanagement.Store;
import org.example.ibb_ecodation_javafx.statemanagement.state.UserState;
import org.example.ibb_ecodation_javafx.ui.avatar.ShadcnAvatar;
import org.example.ibb_ecodation_javafx.ui.dragndrop.Upload;
import org.example.ibb_ecodation_javafx.ui.input.ShadcnInput;


import javax.imageio.ImageIO;

import java.io.File;
import java.io.IOException;

import static org.example.ibb_ecodation_javafx.utils.ImageUtil.convertImageToByteArray;


public class AdminProfileController {
    @FXML
    private ShadcnAvatar shadcnAvatar;
    @FXML
    private ShadcnInput email;
    @FXML
    private ShadcnInput username;
    @FXML
    private ShadcnInput password;
    @FXML
    private ShadcnInput role;
    private final UserService userService;
    private final UserPictureService userPictureService;

    private final Store store = Store.getInstance();
    private final SecurityLogger securityLogger;

    @FXML
    private Upload upload;

    public AdminProfileController() {
        this.userPictureService = SpringContext.getContext().getBean(UserPictureService.class);
        this.userService = SpringContext.getContext().getBean(UserService.class);
        this.securityLogger = SpringContext.getContext().getBean(SecurityLogger.class);
        securityLogger.logOperation("Profil açıldı");
    }

    public void initialize() {
//        updateLabelStyles(header.getParent(), store.getCurrentState(DarkModeState.class).isEnabled() ? "black" : "white");
        setAvatarImageSource();
//        var entity = new User(1,"Mehmet Basrioğlu","admin@admin.com","123456", Role.ADMIN,true,false,0);
//        userService.create(entity);
        userService.read(1, user -> {
            user.setUsername("Mehmet Basrioğlu");
            username.setText(user.getUsername());
            email.setText(user.getEmail());
            role.setText(user.getRole().toString());
//            try {
//                userService.update(user); // Başarılıysa version + 1 olacak
//             //   userService.update(user); // Bu ikinci çağrı çakışmaya neden olacak (version uyuşmazlığı)
//            } catch (OptimisticLockException ole) {
//                System.out.println("Çakışma tespit edildi: " + ole.getMessage());
//            }
        });
    }

    public void updateProfile(ActionEvent actionEvent) {
        try {
            File file = new File(this.upload.getDroppedImagePath());
            if (file.exists()) {
                shadcnAvatar.setImage(ImageIO.read(file));
                //Burası Refactor edilmelidir.
                userPictureService.read(1,callback -> {
                    try {
                        callback.setImageData(convertImageToByteArray(ImageIO.read(file)));
                        userPictureService.update(callback,updateCallback -> {
                            var userState = store.getCurrentState(UserState.class).getUserDetail();
                            try {
                                userState.setProfilePicture(convertImageToByteArray(ImageIO.read(file)));
                                store.dispatch(UserState.class, new UserState(
                                        userState,
                                        store.getCurrentState(UserState.class).isLoggedIn(),
                                        null
                                ));
                            } catch (IOException e) {
                                throw new RuntimeException("Hata: UserDetail güncellerken picture update operasyonunda sorun oluştu. " +  e.getMessage());
                            }
                        });
                    } catch (IOException e) {
                        throw new RuntimeException("Hata: User picture read operasyonunda sorun oluştu. " +  e.getMessage());
                    }
                });
            } else {
                System.out.println("File not found at the specified location: " + file.getAbsolutePath());
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Error reading the image file: " + e.getMessage());
        }
    }


    private void setAvatarImageSource() {
        try {
            shadcnAvatar.setAvatarSize(60);
            shadcnAvatar.setImage(AdminProfileController.class.getResource("/org/example/ibb_ecodation_javafx/assets/avatar.jpg"));
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }


}
