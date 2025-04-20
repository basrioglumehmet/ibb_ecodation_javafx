package org.example.ibb_ecodation_javafx.properties;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Data
@ToString
public class SendGridProperty {
    private String apiUrl;
    private String apikey;
    private String otpTemplateId;
    private String fromEmail;
}