package org.example.ibb_ecodation_javafx.model.dto;

public class SendGridConfigDto {
    private SendGridConfigDetails sendgrid;

    public SendGridConfigDetails getSendgrid() {
        return sendgrid;
    }

    public void setSendgrid(SendGridConfigDetails sendgrid) {
        this.sendgrid = sendgrid;
    }

    public static class SendGridConfigDetails {
        private String apiUrl;
        private String apiKey;
        private String otpTemplateId;
        private String fromEmail;

        public String getApiUrl() {
            return apiUrl;
        }

        public void setApiUrl(String apiUrl) {
            this.apiUrl = apiUrl;
        }

        public String getApiKey() {
            return apiKey;
        }

        public void setApiKey(String apiKey) {
            this.apiKey = apiKey;
        }

        public String getOtpTemplateId() {
            return otpTemplateId;
        }

        public void setOtpTemplateId(String otpTemplateId) {
            this.otpTemplateId = otpTemplateId;
        }

        public String getFromEmail() {
            return fromEmail;
        }

        public void setFromEmail(String fromEmail) {
            this.fromEmail = fromEmail;
        }
    }
}