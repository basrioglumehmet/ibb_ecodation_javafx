package org.example.ibb_ecodation_javafx.controller;

import org.example.ibb_ecodation_javafx.core.context.SpringContext;
import org.example.ibb_ecodation_javafx.core.logger.SecurityLogger;

public class AdminNoteController {
    private final SecurityLogger securityLogger;


    public AdminNoteController() {
        this.securityLogger = SpringContext.getContext().getBean(SecurityLogger.class);
        securityLogger.logOperation("Notlar açıldı");
    }
}
