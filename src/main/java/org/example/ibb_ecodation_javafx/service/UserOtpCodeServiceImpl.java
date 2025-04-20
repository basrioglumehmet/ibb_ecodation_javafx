package org.example.ibb_ecodation_javafx.service;

import lombok.RequiredArgsConstructor;
import org.example.ibb_ecodation_javafx.core.db.Entity;
import org.example.ibb_ecodation_javafx.core.db.EntityFilter;
import org.example.ibb_ecodation_javafx.model.UserOtpCode;
import org.example.ibb_ecodation_javafx.model.dto.OtpCodeDto;
import org.example.ibb_ecodation_javafx.model.dto.UserDetailDto;
import org.example.ibb_ecodation_javafx.repository.UserOtpCodeRepository;
import org.example.ibb_ecodation_javafx.repository.UserRepository;
import org.example.ibb_ecodation_javafx.statemanagement.Store;
import org.example.ibb_ecodation_javafx.statemanagement.state.UserState;
import org.example.ibb_ecodation_javafx.utils.SceneUtil;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class UserOtpCodeServiceImpl implements UserOtpCodeService {

    private final UserOtpCodeRepository userOtpCodeRepository;
    private final UserService userService;

    private Store store = Store.getInstance();



    @Override
    public UserOtpCode save(UserOtpCode entity) {
        return userOtpCodeRepository.save(entity);
    }

    @Override
    public void update(UserOtpCode entity) {
        userOtpCodeRepository.update(entity);
    }


    @Override
    public Optional<UserOtpCode> findById(Integer integer) {
        return userOtpCodeRepository.findById(integer);
    }

    @Override
    public List<UserOtpCode> findAll() {
        return userOtpCodeRepository.findAll();
    }
    /**
     *
     * @param id USER ID
     * @return List of User otp code
     */
    @Override
    public List<UserOtpCode> findAllById(Integer id) {
        return userOtpCodeRepository.findAllById(id);
    }

    @Override
    public List<UserOtpCode> findAllByFilter(List<EntityFilter> filters) {
        return userOtpCodeRepository.findAllByFilter(filters);
    }

    @Override
    public void delete(Integer integer) {
        userOtpCodeRepository.delete(integer);
    }

    @Override
    public Optional<UserOtpCode> findFirstByFilter(List<EntityFilter> filters) {
        return userOtpCodeRepository.findFirstByFilter(filters);
    }

    @Override
    public OtpCodeDto verify(String otpCode) {
        List<EntityFilter> filters =  List.of(EntityFilter.builder()
                .column("otp")
                .operator("=")
                .value(otpCode)
                .build());
        var result = findFirstByFilter(filters);

        if(result.isPresent()){
            UserOtpCode userOtpCode = result.get();
//            delete(userOtpCode.getUserId());
            //Read user id and set the user detail to store
            var optionalUser = userService.findById(userOtpCode.getUserId());

            if(optionalUser.isPresent()){
                var user = optionalUser.get();
                var userDetail = UserDetailDto.builder()
                        .userId(user.getId())
                        .password(user.getPassword())
                        .role(user.getRole().toString())
                        .isLocked(user.isLocked())
                        .username(user.getUsername())
                        .email(user.getEmail())
                        .isVerified(user.isVerified()).build();
                var state = new UserState(
                        userDetail,
                        true,
                        null,
                        null
                );
                store.dispatch(UserState.class,state);
            }
            return new OtpCodeDto(true, userOtpCode.getUserId());

        }

        return new OtpCodeDto(false,0);
    }
}
