package org.example.ibb_ecodation_javafx.core.i18n;

import org.springframework.stereotype.Component;

import java.util.Locale;
import java.util.ResourceBundle;

@Component("fileLanguageLoader")
public class FileLanguageLoader implements LanguageLoader {

    private ResourceBundle resourceBundle;

    @Override
    public ResourceBundle load(Locale locale) {
        try {
            System.out.println("Loading ResourceBundle for locale: " + locale);
            resourceBundle = ResourceBundle.getBundle("org.example.ibb_ecodation_javafx.languages.lang", locale);
        } catch (Exception e) {
            System.err.println("Could not load resource bundle for locale " + locale);
            e.printStackTrace();
            resourceBundle = ResourceBundle.getBundle("org.example.ibb_ecodation_javafx.languages.lang");  // Fallback to default
        }
        return resourceBundle;
    }


}
