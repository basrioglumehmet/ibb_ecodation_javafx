package org.example.ibb_ecodation_javafx.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.ibb_ecodation_javafx.core.logger.SecurityLogger;
import org.example.ibb_ecodation_javafx.service.MailService;
import org.example.ibb_ecodation_javafx.utils.OtpUtil;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;

@RequiredArgsConstructor
@Service
public class MailServiceImpl implements MailService {
    private final String SENDGRID_API_KEY = " ";
    private final String SENDGRID_API_URL = "https://api.sendgrid.com/v3/mail/send";
    private final String TEMPLATE_ID = "d-03558ef2da8a41c4891e895128b8748e";
    private final SecurityLogger securityLogger;

    @Override
    public void sendMail(String to,String otpCode) {

        String jsonPayload = String.format(
                "{\"personalizations\": [{\"to\": [{\"email\": \"%s\"}], \"dynamic_template_data\": {\"otpCode\": \"%s\", \"subject\": \"%s\"}}], " +
                        "\"from\": {\"email\": \"basrioglumehmet@gmail.com\"}, " +
                        "\"subject\": \"deneme\", " +
                        "\"template_id\": \"%s\"}",
                to, otpCode, "Your OTP Verification Code", TEMPLATE_ID
        );

        System.out.println("Sending payload: " + jsonPayload);

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(SENDGRID_API_URL))
                .header("Authorization", "Bearer " + SENDGRID_API_KEY)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonPayload))
                .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            int statusCode = response.statusCode();
            String responseBody = response.body();
            System.out.println("Status Code: " + statusCode);
            System.out.println("Response Body: " + (responseBody.isEmpty() ? "Empty" : responseBody));
            securityLogger.logUserOperation(to,"otp mail gönderme");

            if (statusCode != 202) {
                throw new RuntimeException("Failed to send email: HTTP " + statusCode + " - " + responseBody);
            }
        } catch (IOException | InterruptedException ex) {
            throw new RuntimeException("Failed to send email via SendGrid API", ex);
        }
    }

    public void sendMailWithAttachment(String to, String subject, Path attachmentPath, String attachmentName) {
        try {
            byte[] fileContent = Files.readAllBytes(attachmentPath);
            String base64Content = Base64.getEncoder().encodeToString(fileContent);
            String contentType = Files.probeContentType(attachmentPath);

            String jsonPayload = String.format(
                    "{\"personalizations\": [{\"to\": [{\"email\": \"%s\"}]}], " +
                            "\"from\": {\"email\": \"basrioglumehmet@gmail.com\"}, " +
                            "\"subject\": \"%s\", " +
                            "\"content\": [{\"type\": \"text/plain\", \"value\": \"This is an email with attachment\"}], " +
                            "\"attachments\": [{\"content\": \"%s\", \"type\": \"%s\", \"filename\": \"%s\"}]}",
                    to, subject,
                    base64Content, contentType != null ? contentType : "application/octet-stream", attachmentName
            );

            System.out.println("Sending payload with attachment: " + jsonPayload);

            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(SENDGRID_API_URL))
                    .header("Authorization", "Bearer " + SENDGRID_API_KEY)
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonPayload))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            int statusCode = response.statusCode();
            String responseBody = response.body();
            System.out.println("Status Code: " + statusCode);
            System.out.println("Response Body: " + (responseBody.isEmpty() ? "Empty" : responseBody));
            securityLogger.logUserOperation(to, "mail with attachment gönderme");

            if (statusCode != 202) {
                throw new RuntimeException("Failed to send email with attachment: HTTP " + statusCode + " - " + responseBody);
            }
        } catch (IOException | InterruptedException ex) {
            throw new RuntimeException("Failed to send email with attachment via SendGrid API", ex);
        }
    }
}