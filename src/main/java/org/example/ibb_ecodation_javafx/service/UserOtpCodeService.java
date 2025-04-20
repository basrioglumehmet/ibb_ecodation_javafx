package org.example.ibb_ecodation_javafx.service;

import org.example.ibb_ecodation_javafx.core.service.GenericService;
import org.example.ibb_ecodation_javafx.model.UserOtpCode;
import org.example.ibb_ecodation_javafx.model.dto.OtpCodeDto;

public interface UserOtpCodeService extends GenericService<UserOtpCode,Integer> {
     OtpCodeDto verify(String otpCode);
}
