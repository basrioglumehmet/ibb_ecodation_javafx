package org.example.ibb_ecodation_javafx.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.ibb_ecodation_javafx.mapper.UserMapper;
import org.example.ibb_ecodation_javafx.model.Authentication;
import org.example.ibb_ecodation_javafx.model.User;
import org.example.ibb_ecodation_javafx.model.UserOtpCode;
import org.example.ibb_ecodation_javafx.model.dto.RegisterDto;
import org.example.ibb_ecodation_javafx.model.dto.UserDto;
import org.example.ibb_ecodation_javafx.model.enums.AuthenticationResult;
import org.example.ibb_ecodation_javafx.service.AuthenticationService;
import org.example.ibb_ecodation_javafx.service.MailService;
import org.example.ibb_ecodation_javafx.service.UserOtpCodeService;
import org.example.ibb_ecodation_javafx.service.UserService;
import org.example.ibb_ecodation_javafx.utils.OtpUtil;
import org.springframework.stereotype.Service;

import java.util.function.Consumer;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {
    private final UserService userService;
    private final UserMapper userMapper;
    private final UserOtpCodeService userOtpCodeService;
    private final MailService mailService;
    @Override
    public void signin(Authentication authentication, Consumer<AuthenticationResult> callback) {
        userService.readByEmail(authentication.getEmail(), cb -> {
            if(cb != null){
                if(!cb.isVerified()){
                    callback.accept(AuthenticationResult.OTP_REQUIRED);
                }
            }
            else{
                callback.accept(null);
            }
        });
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
            convertedUser.setEmail(registerDto.getEmail());
            convertedUser.setPassword(registerDto.getPassword());
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
