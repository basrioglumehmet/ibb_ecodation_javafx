package org.example.ibb_ecodation_javafx.model;

import lombok.*;

// Lombok
@Getter
@Setter
@AllArgsConstructor // Parametreli Constructor
@NoArgsConstructor  // Parametresiz Constructor
@ToString
@Builder
public class User {
    private Integer id;
    private String username;
    private String password;
    private String email;
}
