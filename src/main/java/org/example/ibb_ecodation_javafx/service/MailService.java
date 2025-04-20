package org.example.ibb_ecodation_javafx.service;

import org.example.ibb_ecodation_javafx.model.response.MailResponse;

import java.nio.file.Path;

public interface MailService {
    void sendMail(String to,String otpCode);
    void sendMailWithAttachment(String to, String subject, Path attachmentPath, String attachmentName);
    MailResponse sendTestMail();
}
