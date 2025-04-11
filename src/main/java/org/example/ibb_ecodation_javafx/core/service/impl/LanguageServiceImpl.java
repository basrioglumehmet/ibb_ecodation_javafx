package org.example.ibb_ecodation_javafx.core.service.impl;


import org.example.ibb_ecodation_javafx.core.i18n.LanguageLoader;
import org.example.ibb_ecodation_javafx.core.service.LanguageService;
import org.springframework.stereotype.Service;

import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

@Service
public class LanguageServiceImpl implements LanguageService {
    private final LanguageLoader fileLanguageLoader;
    private ResourceBundle bundle;

    public LanguageServiceImpl(LanguageLoader fileLanguageLoader) {
        this.fileLanguageLoader = fileLanguageLoader;
    }

    @Override
    public ResourceBundle loadAll(String languageCode) {
        Locale locale = new Locale(languageCode);
        bundle = fileLanguageLoader.load(locale);
        return bundle;
    }

    @Override
    public String translate(String key) {
        try {
            return bundle.getString(key);
        } catch (MissingResourceException e) {
            System.err.println("Missing translation for key: " + key);
            return key; // Fallback to the key itself
        }
    }

}

