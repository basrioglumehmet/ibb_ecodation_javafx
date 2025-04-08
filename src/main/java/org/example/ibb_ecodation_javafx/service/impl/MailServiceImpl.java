package org.example.ibb_ecodation_javafx.service.impl;

import org.example.ibb_ecodation_javafx.service.MailService;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.io.IOException;

@Service
public class MailServiceImpl implements MailService {
    private final String SENDGRID_API_KEY = "";
    private final String SENDGRID_API_URL = "https://api.sendgrid.com/v3/mail/send";
    private final String TEMPLATE_ID = "d-03558ef2da8a41c4891e895128b8748e";

    @Override
    public void sendMail(String to, String subject, String content) {
        // Build the JSON payload
        String jsonPayload = String.format(
                "{\"personalizations\": [{\"to\": [{\"email\": \"%s\"}], \"dynamic_template_data\": {\"otpCode\": \"666\"}}], " +
                        "\"from\": {\"email\": \"basrioglumehmet@gmail.com\"}, " +
                        "\"subject\": \"%s\", " +
                        "\"template_id\": \"%s\"}",
                to, subject, TEMPLATE_ID
        );

        // Create HttpClient
        HttpClient client = HttpClient.newHttpClient();

        // Build the HTTP request
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(SENDGRID_API_URL))
                .header("Authorization", "Bearer " + SENDGRID_API_KEY)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonPayload))
                .build();

        try {
            // Send the request and get the response
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            int statusCode = response.statusCode();
            System.out.println("Status Code: " + statusCode);
            System.out.println("Response Body: " + response.body());

            if (statusCode != 202) {
                throw new RuntimeException("Failed to send email: HTTP " + statusCode);
            }
        } catch (IOException | InterruptedException ex) {
            throw new RuntimeException("Failed to send email via SendGrid API", ex);
        }
    }
}