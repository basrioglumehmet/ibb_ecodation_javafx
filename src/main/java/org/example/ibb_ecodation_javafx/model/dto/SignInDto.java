package org.example.ibb_ecodation_javafx.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.example.ibb_ecodation_javafx.model.User;
import org.example.ibb_ecodation_javafx.model.UserPicture;
import org.example.ibb_ecodation_javafx.model.enums.AuthenticationResult;

@Getter
@Setter
@Data
@AllArgsConstructor
public class SignInDto {
    private AuthenticationResult authenticationResult;
    private User user;
    private UserPicture userPicture;
}
