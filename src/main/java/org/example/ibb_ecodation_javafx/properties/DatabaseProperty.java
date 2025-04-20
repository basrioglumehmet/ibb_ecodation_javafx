package org.example.ibb_ecodation_javafx.properties;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Data
@ToString
public class DatabaseProperty {
    private String url;
    private String username;
    private String password;
    private String driver;
}