package org.example.ibb_ecodation_javafx.service.impl;

import org.example.ibb_ecodation_javafx.service.MailService;
import org.example.ibb_ecodation_javafx.utils.OtpUtil;
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

            if (statusCode != 202) {
                throw new RuntimeException("Failed to send email: HTTP " + statusCode + " - " + responseBody);
            }
        } catch (IOException | InterruptedException ex) {
            throw new RuntimeException("Failed to send email via SendGrid API", ex);
        }
    }
}