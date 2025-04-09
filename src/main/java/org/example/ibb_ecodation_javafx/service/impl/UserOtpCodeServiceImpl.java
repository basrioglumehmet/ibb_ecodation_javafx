package org.example.ibb_ecodation_javafx.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.ibb_ecodation_javafx.model.UserOtpCode;
import org.example.ibb_ecodation_javafx.repository.UserOtpCodeRepository;
import org.example.ibb_ecodation_javafx.repository.query.UserOtpCodeQuery;
import org.example.ibb_ecodation_javafx.service.UserOtpCodeService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

@Service
@RequiredArgsConstructor
public class UserOtpCodeServiceImpl implements UserOtpCodeService {

    private final UserOtpCodeRepository userOtpCodeRepository;

    public void createAndCallback(UserOtpCode entity,Consumer<Boolean> callback) {
        var created = this.create(entity);
        if(Objects.nonNull(created)){
            callback.accept(true);
        }
        else{
            callback.accept(false);
        }
    }

    @Override
    public UserOtpCode create(UserOtpCode entity) {
        return userOtpCodeRepository.create(entity, UserOtpCodeQuery.CREATE_USER_OTP_CODE, List.of(entity.getUserId(), entity.getOtpCode()));
    }

    @Override
    public void verifyOtp(String otpCode,Consumer<Boolean> callback){
        var otpEntity = userOtpCodeRepository.read(UserOtpCode.class,UserOtpCodeQuery.READ_BY_OTP_CODE,List.of(otpCode));
        if(Objects.nonNull(otpEntity)){
            delete(otpEntity.getUserId());
            callback.accept(true);
        }
        else{
            callback.accept(false);
        }
    }

    @Override
    public void delete(int id) {
        userOtpCodeRepository.delete(UserOtpCodeQuery.DELETE_USER_OTP_BY_USER_ID, List.of(id));
    }


    @Override
    public void read(int id, Consumer<UserOtpCode> callback) {
        callback.accept(userOtpCodeRepository.read(UserOtpCode.class,UserOtpCodeQuery.READ_USER_OTP_CODE_BY_USER_ID,List.of(id)));
    }

    @Override
    public void update(UserOtpCode entity, Consumer<UserOtpCode> callback) {
        callback.accept(userOtpCodeRepository.update(entity,UserOtpCodeQuery.UPDATE_USER_OTP_CODE_BY_USER_ID, List.of(
                entity.getOtpCode(),
                entity.getUserId(),
                entity.getVersion()
        )));
    }
}
