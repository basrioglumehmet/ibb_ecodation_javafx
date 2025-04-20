package org.example.ibb_ecodation_javafx.model.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Collections;
import java.util.List;

@Data
public class SendGridRequest {

    @JsonProperty("personalizations")
    private List<Personalization> personalizations;

    @JsonProperty("from")
    private Email from;

    @JsonProperty("subject")
    private String subject;

    @JsonProperty("template_id")
    private String templateId;

    @JsonProperty("content")
    private List<Content> content;

    @JsonProperty("attachments")
    private List<Attachment> attachments;

    // Inner class for personalizations
    @Data
    public static class Personalization {
        @JsonProperty("to")
        private List<Email> to;

        @JsonProperty("dynamic_template_data")
        private DynamicTemplateData dynamicTemplateData;
    }

    // Inner class for email addresses
    @Data
    public static class Email {
        @JsonProperty("email")
        private String email;
    }

    // Inner class for dynamic template data
    @Data
    public static class DynamicTemplateData {
        @JsonProperty("otpCode")
        private String otpCode;

        @JsonProperty("subject")
        private String subject;
    }

    // Inner class for content
    @Data
    public static class Content {
        @JsonProperty("type")
        private String type;

        @JsonProperty("value")
        private String value;
    }

    // Inner class for attachments
    @Data
    public static class Attachment {
        @JsonProperty("content")
        private String content;

        @JsonProperty("type")
        private String type;

        @JsonProperty("filename")
        private String filename;
    }

    // Builder method for OTP email
    public static SendGridRequest forOtpEmail(String to, String otpCode, String subject, String fromEmail, String templateId) {
        SendGridRequest request = new SendGridRequest();

        Email toEmail = new Email();
        toEmail.setEmail(to);

        DynamicTemplateData templateData = new DynamicTemplateData();
        templateData.setOtpCode(otpCode);
        templateData.setSubject(subject);

        Personalization personalization = new Personalization();
        personalization.setTo(Collections.singletonList(toEmail));
        personalization.setDynamicTemplateData(templateData);

        Email from = new Email();
        from.setEmail(fromEmail);

        request.setPersonalizations(Collections.singletonList(personalization));
        request.setFrom(from);
        request.setSubject(subject);
        request.setTemplateId(templateId);

        return request;
    }
}