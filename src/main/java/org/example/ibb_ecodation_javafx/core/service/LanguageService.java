package org.example.ibb_ecodation_javafx.core.service;

import java.util.ResourceBundle;

public interface LanguageService {
    ResourceBundle loadAll(String languageCode);
    String translate(String key);
}
