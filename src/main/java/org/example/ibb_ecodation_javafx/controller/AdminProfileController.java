package org.example.ibb_ecodation_javafx.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import org.example.ibb_ecodation_javafx.core.context.SpringContext;
import org.example.ibb_ecodation_javafx.exception.OptimisticLockException;
import org.example.ibb_ecodation_javafx.model.UserPicture;
import org.example.ibb_ecodation_javafx.service.UserPictureService;
import org.example.ibb_ecodation_javafx.service.UserService;
import org.example.ibb_ecodation_javafx.statemanagement.Store;
import org.example.ibb_ecodation_javafx.ui.avatar.ShadcnAvatar;
import org.example.ibb_ecodation_javafx.ui.dragndrop.Upload;
import org.example.ibb_ecodation_javafx.ui.input.ShadcnInput;
import org.example.ibb_ecodation_javafx.utils.ImageUtil;


import javax.imageio.ImageIO;

import java.io.File;
import java.io.IOException;



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

    @FXML
    private Upload upload;

    public AdminProfileController() {
        this.userPictureService = SpringContext.getContext().getBean(UserPictureService.class);
        this.userService = SpringContext.getContext().getBean(UserService.class);
    }

    public void initialize() {
//        updateLabelStyles(header.getParent(), store.getCurrentState(DarkModeState.class).isEnabled() ? "black" : "white");
        setAvatarImageSource();
//        var entity = new User(1,"Mehmet Basrioğlu","admin@admin.com","123456", Role.ADMIN,true,false,0);
//        userService.create(entity);
//        userService.read(1, user -> {
//            user.setUsername("Mehmet Basrioğlu");
//            username.setText(user.getUsername());
//            email.setText(user.getEmail());
//            role.setText(user.getRole().toString());
//            try {
//                userService.update(user); // Başarılıysa version + 1 olacak
//             //   userService.update(user); // Bu ikinci çağrı çakışmaya neden olacak (version uyuşmazlığı)
//            } catch (OptimisticLockException ole) {
//                System.out.println("Çakışma tespit edildi: " + ole.getMessage());
//            }
//        });
    }

    public void updateProfile(ActionEvent actionEvent) {
        try {
            // Make sure the file path is valid and exists
            File file = new File(this.upload.getDroppedImagePath());
            if (file.exists()) {
                // Read the image file and set it to the image view
                shadcnAvatar.setImage(ImageIO.read(file));
                userPictureService.create(new UserPicture(1, ImageUtil.convertImageToByteArray(ImageIO.read(file)), 1));
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
