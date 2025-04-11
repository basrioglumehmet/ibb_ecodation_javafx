package org.example.ibb_ecodation_javafx.core.i18n;

import java.util.Locale;
import java.util.ResourceBundle;

public interface LanguageLoader {
    ResourceBundle load(Locale locale);
}
