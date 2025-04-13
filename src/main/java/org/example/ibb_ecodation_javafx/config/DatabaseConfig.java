package org.example.ibb_ecodation_javafx.config;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Data
@ToString
public class DatabaseConfig {
    private String url;
    private String username;
    private String password;
    private String driver;
}