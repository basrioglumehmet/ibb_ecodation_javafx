
package org.example.ibb_ecodation_javafx.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.example.ibb_ecodation_javafx.model.dto.SendGridConfigDto;
import org.example.ibb_ecodation_javafx.properties.SendGridProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;

@Configuration
public class SendGridConfig {

    @Bean
    public SendGridProperty sendGridProperty() throws IOException {
        ObjectMapper yamlMapper = new ObjectMapper(new YAMLFactory());
        ClassPathResource resource = new ClassPathResource("smtp-config.yml");

        SendGridConfigDto configDto = yamlMapper.readValue(resource.getInputStream(), SendGridConfigDto.class);

        SendGridProperty sendGridProperty = new SendGridProperty();
        sendGridProperty.setApiUrl(configDto.getSendgrid().getApiUrl());
        sendGridProperty.setApikey(configDto.getSendgrid().getApiKey());
        sendGridProperty.setOtpTemplateId(configDto.getSendgrid().getOtpTemplateId());
        sendGridProperty.setFromEmail(configDto.getSendgrid().getFromEmail());

        return sendGridProperty;
    }
}