package org.example.ibb_ecodation_javafx.model;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Authentication {
    private String email;
    private String password;
}
