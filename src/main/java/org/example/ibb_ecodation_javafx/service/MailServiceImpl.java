package org.example.ibb_ecodation_javafx.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.example.ibb_ecodation_javafx.core.config.SendGridConfig;
import org.example.ibb_ecodation_javafx.core.service.LanguageService;
import org.example.ibb_ecodation_javafx.model.request.SendGridRequest;
import org.example.ibb_ecodation_javafx.model.response.MailResponse;
import org.example.ibb_ecodation_javafx.model.response.SendGridErrorResponse;
import org.example.ibb_ecodation_javafx.model.response.SendGridResponse;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;

@Service
@RequiredArgsConstructor
public class MailServiceImpl implements MailService {

    private final SendGridConfig sendGridConfig;
    private final String SENDGRID_API_URL = "https://api.sendgrid.com/v3/mail/send";

    //private final SecurityLogger securityLogger;
    private final LanguageService languageService;
    private final ObjectMapper objectMapper;

    @Override
    public void sendMail(String to, String otpCode) {
        String subject = languageService.translate("mail.otp.subject");

        String jsonPayload = String.format(
                "{\"personalizations\": [{\"to\": [{\"email\": \"%s\"}], \"dynamic_template_data\": {\"otpCode\": \"%s\", \"subject\": \"%s\"}}], " +
                        "\"from\": {\"email\": \"basrioglumehmet@gmail.com\"}, " +
                        "\"subject\": \"%s\", " +
                        "\"template_id\": \"%s\"}",
                to, otpCode, subject, subject, sendGridConfig.getTemplateId()
        );

        System.out.println("Sending payload: " + jsonPayload);

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(SENDGRID_API_URL))
                .header("Authorization", "Bearer " + sendGridConfig.getApiKey())
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonPayload))
                .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            int statusCode = response.statusCode();
            String responseBody = response.body();
            // securityLogger.logUserOperation(to, "otp mail gönderme");

            if (statusCode != 202) {
                throw new RuntimeException("Failed to send email: HTTP " + statusCode + " - " + responseBody);
            }
        } catch (IOException | InterruptedException ex) {
            throw new RuntimeException("Failed to send email via SendGrid API", ex);
        }
    }

    @Override
    public void sendMailWithAttachment(String to, String subject, Path attachmentPath, String attachmentName) {
        try {
            byte[] fileContent = Files.readAllBytes(attachmentPath);
            String base64Content = Base64.getEncoder().encodeToString(fileContent);
            String contentType = Files.probeContentType(attachmentPath);
            String mailText = languageService.translate("mail.attachment.text");

            String jsonPayload = String.format(
                    "{\"personalizations\": [{\"to\": [{\"email\": \"%s\"}]}], " +
                            "\"from\": {\"email\": \"basrioglumehmet@gmail.com\"}, " +
                            "\"subject\": \"%s\", " +
                            "\"content\": [{\"type\": \"text/plain\", \"value\": \"%s\"}], " +
                            "\"attachments\": [{\"content\": \"%s\", \"type\": \"%s\", \"filename\": \"%s\"}]}",
                    to, subject, mailText, base64Content,
                    contentType != null ? contentType : "application/octet-stream",
                    attachmentName
            );

            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(SENDGRID_API_URL))
                    .header("Authorization", "Bearer " + sendGridConfig.getApiKey())
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonPayload))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            int statusCode = response.statusCode();
            String responseBody = response.body();
           // securityLogger.logUserOperation(to, "mail with attachment gönderme");

            if (statusCode != 202) {
                throw new RuntimeException("Failed to send email with attachment: HTTP " + statusCode + " - " + responseBody);
            }
        } catch (IOException | InterruptedException ex) {
            throw new RuntimeException("Failed to send email with attachment via SendGrid API", ex);
        }
    }

    @Override
    public MailResponse sendTestMail() {
        String to = "basrioglumehmet@gmail.com";
        String subject = languageService.translate("boot.test.subject");
        String otpCode = "TEST123";
        String fromEmail = "basrioglumehmet@gmail.com";

        // Use SendGridRequest to build the payload
        SendGridRequest sendGridRequest = SendGridRequest.forOtpEmail(to, otpCode, subject, fromEmail, sendGridConfig.getBootTemplateId());

        String jsonPayload;
        try {
            jsonPayload = objectMapper.writeValueAsString(sendGridRequest);
        } catch (JsonProcessingException ex) {
            return MailResponse.builder()
                    .isSuccess(false)
                    .message(languageService.translate("boot.error.serialization") + ": " + ex.getMessage())
                    .build();
        }

        System.out.println("Sending payload: " + jsonPayload);

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(SENDGRID_API_URL))
                .header("Authorization", "Bearer " + sendGridConfig.getApiKey())
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonPayload))
                .build();

        try {
            System.out.println("Sending test mail...");
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            int statusCode = response.statusCode();
            String responseBody = response.body();
            System.out.println("Response body: " + responseBody);
            System.out.println("Status code: " + statusCode);

            if (statusCode == 202) {
                System.out.println("Test mail sent successfully.");
                return MailResponse.builder()
                        .isSuccess(true)
                        .message(languageService.translate("boot.success.mail_sent"))
                        .build();
            } else if (statusCode == 401) {
                System.out.println("Failed to send test mail: Invalid API key");
                return MailResponse.builder()
                        .isSuccess(false)
                        .message(languageService.translate("boot.error.mail_api_key"))
                        .build();
            } else {
                try {
                    SendGridResponse sendGridResponse = objectMapper.readValue(responseBody, SendGridResponse.class);
                    String errorMessage = (sendGridResponse.getErrors() != null && !sendGridResponse.getErrors().isEmpty())
                            ? sendGridResponse.getErrors().get(0).getMessage()
                            : languageService.translate("boot.error.unknown");
                    System.out.println("Failed to send test mail: HTTP " + statusCode + ", Error: " + errorMessage);
                    return MailResponse.builder()
                            .isSuccess(false)
                            .message(languageService.translate("boot.error.mail_failed") + ": HTTP " + statusCode + ", " + errorMessage)
                            .build();
                } catch (JsonProcessingException ex) {
                    System.out.println("Failed to parse error response: " + ex.getMessage());
                    return MailResponse.builder()
                            .isSuccess(false)
                            .message(languageService.translate("boot.error.parse_failed") + ": HTTP " + statusCode + ", " + responseBody)
                            .build();
                }
            }
        } catch (IOException | InterruptedException ex) {
            System.out.println("Exception during test mail sending: " + ex.getMessage());
            return MailResponse.builder()
                    .isSuccess(false)
                    .message(languageService.translate("boot.error.exception") + ": " + ex.getMessage())
                    .build();
        }
    }
}