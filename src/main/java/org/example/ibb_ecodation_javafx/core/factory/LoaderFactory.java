package org.example.ibb_ecodation_javafx.core.factory;

import org.example.ibb_ecodation_javafx.loader.FileLanguageLoader;

public class LoaderFactory {
    public static FileLanguageLoader createFileLanguageLoader(){
        return new FileLanguageLoader();
    }
}
