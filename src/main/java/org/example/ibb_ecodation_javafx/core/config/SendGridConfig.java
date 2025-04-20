package org.example.ibb_ecodation_javafx.core.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Data
public class SendGridConfig {
    @Value("${sendgrid.apikey}")
    private String apiKey;
    @Value("${sendgrid.template_id}")
    private String templateId;
    @Value("${sendgrid.boot.template_id}")
    private String bootTemplateId;
}
