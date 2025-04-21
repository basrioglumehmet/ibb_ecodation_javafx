package org.example.ibb_ecodation_javafx.service;

import lombok.RequiredArgsConstructor;
import org.bouncycastle.util.Times;
import org.example.ibb_ecodation_javafx.core.db.EntityFilter;
import org.example.ibb_ecodation_javafx.mapper.UserMapper;
import org.example.ibb_ecodation_javafx.model.*;
import org.example.ibb_ecodation_javafx.model.dto.RegisterDto;
import org.example.ibb_ecodation_javafx.model.dto.SignInDto;
import org.example.ibb_ecodation_javafx.model.enums.AuthenticationResult;
import org.example.ibb_ecodation_javafx.model.enums.Role;
import org.example.ibb_ecodation_javafx.utils.OtpUtil;
import org.example.ibb_ecodation_javafx.utils.SystemInfoUtil;
import org.mindrot.jbcrypt.BCrypt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {
    private static final Logger logger = LoggerFactory.getLogger(AuthenticationServiceImpl.class);
    private static final int OTP_LENGTH = 5;
    private static final int OTP_VERSION = 1;

    private final UserService userService;
    private final UserPictureService userPictureService;
    private final UserMapper userMapper;
    private final UserOtpCodeService otpCodeService;
    private final MailService mailService;
    private final AppLogService appLogService;

    @Override
    public SignInDto signin(Authentication authentication) {
        try {
            Optional<User> userOptional = findUserByEmail(authentication.getEmail());
            User user = userOptional.orElseGet(() -> {
                logger.warn("User not found for email: {}", authentication.getEmail());
                return null;
            });

            if (user == null) {
                return new SignInDto(AuthenticationResult.ERROR, null, null);
            }

            Optional<UserPicture> pictureOptional = findUserPictureByUserId(user.getId());
            UserPicture userPicture = pictureOptional.orElseGet(() -> {
                logger.warn("User picture not found for user ID: {}", user.getId());
                return new UserPicture(
                        user.getId(),
                        null,
                        0
                );
            });



            if (!BCrypt.checkpw(authentication.getPassword(), user.getPassword())) {
                logger.info("Password mismatch for email: {}", authentication.getEmail());
                return new SignInDto(AuthenticationResult.PASSWORD_MIS_MATCH, null, null);
            }

            logger.info("Successful login for user: {}", authentication.getEmail());
            var entity = new AppLog();
            entity.setIpAddresses(SystemInfoUtil.getIpAddress());
            entity.setDescription(String.format("Successful login for user: {%s}", authentication.getEmail()));
            entity.setAtTime(Timestamp.valueOf(LocalDateTime.now()));
            entity.setComputerName(SystemInfoUtil.getComputerName());
            appLogService.save(entity);
            return new SignInDto(AuthenticationResult.OK, user, userPicture);

        } catch (Exception e) {
            var entity = new AppLog();
            entity.setIpAddresses(SystemInfoUtil.getIpAddress());
            entity.setDescription(String.format("Sign-in failed for email: {%s}, %s", authentication.getEmail(), e));
            entity.setAtTime(Timestamp.valueOf(LocalDateTime.now()));
            entity.setComputerName(SystemInfoUtil.getComputerName());
            appLogService.save(entity);
            logger.error("Sign-in failed for email: {}", authentication.getEmail(), e);
            return new SignInDto(AuthenticationResult.ERROR, null, null);
        }
    }

    @Override
    public Boolean logout() {
        logger.info("Logout operation called but not implemented");
        throw new UnsupportedOperationException("Logout operation is not implemented yet.");
    }

    @Override
    public AuthenticationResult signup(RegisterDto registerDto) {
        try {
            if (userExistsByEmail(registerDto.getEmail())) {
                logger.info("User already exists with email: {}", registerDto.getEmail());
                return AuthenticationResult.EXISTS;
            }

            User user = createUserFromDto(registerDto);
            userService.save(user);

            Optional<User> savedUserOptional = findUserByEmail(registerDto.getEmail());
            if (savedUserOptional.isEmpty()) {
                logger.error("Failed to retrieve saved user for email: {}", registerDto.getEmail());
                throw new IllegalStateException("Failed to save user: User not found after save");
            }
            User savedUser = savedUserOptional.get();

            UserOtpCode otpCode = createAndSaveOtp(savedUser.getId());
            sendVerificationEmail(savedUser.getEmail(), otpCode.getOtpCode());

            logger.info("User successfully registered with email: {}", registerDto.getEmail());
            var entity = new AppLog();
            entity.setIpAddresses(SystemInfoUtil.getIpAddress());
            entity.setDescription(String.format("User successfully registered with email: {%s}", registerDto.getEmail()));
            entity.setAtTime(Timestamp.valueOf(LocalDateTime.now()));
            entity.setComputerName(SystemInfoUtil.getComputerName());
            appLogService.save(entity);
            return AuthenticationResult.CREATED;

        } catch (Exception e) {
            logger.error("Signup failed for email: {}", registerDto.getEmail(), e);
            var entity = new AppLog();
            entity.setIpAddresses(SystemInfoUtil.getIpAddress());
            entity.setDescription(String.format("Signup failed for email: {%s}, %s", registerDto.getEmail(), e));
            entity.setAtTime(Timestamp.valueOf(LocalDateTime.now()));
            entity.setComputerName(SystemInfoUtil.getComputerName());
            appLogService.save(entity);
            return AuthenticationResult.ERROR;
        }
    }

    private Optional<User> findUserByEmail(String email) {
        return userService.findFirstByFilter(createEmailFilter(email));
    }

    private Optional<UserPicture> findUserPictureByUserId(Integer userId) {
        return userPictureService.findFirstByFilter(createUserIdFilter(userId));
    }

    private boolean userExistsByEmail(String email) {
        return findUserByEmail(email).isPresent();
    }

    private User createUserFromDto(RegisterDto registerDto) {
        User user = userMapper.toEntity(registerDto);
        user.setUsername(registerDto.getUsername());
        user.setEmail(registerDto.getEmail());
        user.setPassword(BCrypt.hashpw(registerDto.getPassword(), BCrypt.gensalt()));
        user.setLocked(false);
        user.setVerified(false);
        user.setRole(Role.USER);
        user.setVersion(0);
        return user;
    }

    private UserOtpCode createAndSaveOtp(Integer userId) {
        String otpCode = OtpUtil.random(OTP_LENGTH);
        UserOtpCode otpEntity = new UserOtpCode(userId, otpCode, OTP_VERSION);
        return otpCodeService.save(otpEntity);
    }

    private void sendVerificationEmail(String email, String otpCode) {
        mailService.sendMail(email.trim(), otpCode);
    }

    private List<EntityFilter> createEmailFilter(String email) {
        return List.of(EntityFilter.builder()
                .column("email")
                .operator("=")
                .value(email)
                .build());
    }

    private List<EntityFilter> createUserIdFilter(Integer userId) {
        return List.of(EntityFilter.builder()
                .column("user_id")
                .operator("=")
                .value(userId)
                .build());
    }
}