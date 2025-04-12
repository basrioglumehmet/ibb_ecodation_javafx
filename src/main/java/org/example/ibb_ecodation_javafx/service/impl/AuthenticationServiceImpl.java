package org.example.ibb_ecodation_javafx.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.ibb_ecodation_javafx.core.logger.SecurityLogger;
import org.example.ibb_ecodation_javafx.mapper.UserMapper;
import org.example.ibb_ecodation_javafx.model.Authentication;
import org.example.ibb_ecodation_javafx.model.UserOtpCode;
import org.example.ibb_ecodation_javafx.model.UserPicture;
import org.example.ibb_ecodation_javafx.model.dto.RegisterDto;
import org.example.ibb_ecodation_javafx.model.dto.SignInDto;
import org.example.ibb_ecodation_javafx.model.enums.AuthenticationResult;
import org.example.ibb_ecodation_javafx.model.enums.Role;
import org.example.ibb_ecodation_javafx.service.*;
import org.example.ibb_ecodation_javafx.utils.ImageUtil;
import org.example.ibb_ecodation_javafx.utils.OtpUtil;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {
    private final UserService userService;
    private final UserMapper userMapper;
    private final UserOtpCodeService userOtpCodeService;
    private final MailService mailService;
    private final SecurityLogger securityLogger;
    private final UserPictureService userPictureService;

    @Override
    public void signin(Authentication authentication, Consumer<SignInDto> callback) {
        userService.readByEmail(authentication.getEmail(), user -> {
            if (user == null) {
                callback.accept(new SignInDto(AuthenticationResult.ERROR, null, null));
                return;
            }

            // Kullanıcı resmi yoksa, varsayılan bir resim kullan
            userPictureService.read(user.getId(), picture -> {
                byte[] pictureBytes;

                // Eğer resim yoksa, varsayılan bir resim yükle
                if (picture == null) {
                    pictureBytes = loadDefaultImage(); // Varsayılan resmi yükle
                } else {
                    pictureBytes = picture.getImageData(); // Veritabanındaki resmi kullan
                }

                // Authentication sonucu belirle
                AuthenticationResult result;
                if (!user.isVerified()) {
                    result = AuthenticationResult.OTP_REQUIRED;
                } else if (user.isLocked()) {
                    result = AuthenticationResult.LOCKED;
                } else if (!BCrypt.checkpw(authentication.getPassword(), user.getPassword())) {
                    result = AuthenticationResult.PASSWORD_MIS_MATCH;
                } else {
                    result = AuthenticationResult.OK;
                }

                // Eğer picture null ise, varsayılan ID ve versiyon değerleriyle UserPicture oluşturuluyor
                UserPicture userPicture = picture == null ?
                        new UserPicture(0, pictureBytes, 0) : // Varsayılan resim
                        new UserPicture(picture.getUserId(), pictureBytes, picture.getVersion()); // Veritabanındaki resim

                // DTO'yu oluştur ve callback ile dön
                SignInDto dto = new SignInDto(result, user, userPicture);
                callback.accept(dto);
            });
        });
    }


    private byte[] loadDefaultImage() {
        try {
            // Burada varsayılan resmi yükle
            BufferedImage defaultImage = ImageIO.read(Objects.requireNonNull(getClass().
                    getResourceAsStream("/org/example/ibb_ecodation_javafx/assets/avatar.png")));
            return ImageUtil.convertImageToByteArray(defaultImage); // Byte dizisine çevir
        } catch (IOException e) {
            System.out.println("Varsayılan resim yüklenemedi: " + e.getMessage());
            return null;
        }
    }



    @Override
    public void logout(Consumer<Boolean> callback) {

    }

    @Override
    public void signup(RegisterDto registerDto, Consumer<AuthenticationResult> callback) {
        var emailExists = userService.isEmailExists(registerDto.getEmail());
        if(emailExists){
            callback.accept(AuthenticationResult.EXISTS);
        }
        else{
            var convertedUser = userMapper.toEntity(registerDto);
            convertedUser.setUsername(registerDto.getUsername());
            convertedUser.setEmail(registerDto.getEmail());
            convertedUser.setPassword(registerDto.getPassword());
            convertedUser.setLocked(false);
            convertedUser.setVerified(false);
            convertedUser.setRole(Role.USER);
            convertedUser.setVersion(0);
            System.out.println(convertedUser);
           var createdUser = userService.create(convertedUser);
            var otpCode = OtpUtil.random(5);
            var otpEntity = new UserOtpCode(createdUser.getId(),otpCode,1);
            userOtpCodeService.createAndCallback(otpEntity,cb -> {
                if(cb){
                    mailService.sendMail(createdUser.getEmail(), otpEntity.getOtpCode());
                    callback.accept(AuthenticationResult.CREATED);
                }
            });
        }

    }
}
