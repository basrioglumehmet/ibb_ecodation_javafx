module org.example.ibb_ecodation_javafx {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    // #############################################
    // Lombok kütüphanesi, Java'da getter, setter, constructor gibi metotları otomatik oluşturur.
    // Lombok, derleme zamanı (compile-time) kullanıldığı için "static" olarak eklenmiştir.
    requires static lombok;
    requires java.sql;
    requires com.h2database;
    requires  org.mapstruct;

    // Açılması gereken paketler:
    opens org.example.ibb_ecodation_javafx to javafx.fxml, lombok,org.mapstruct;
    opens org.example.ibb_ecodation_javafx.mapper to org.mapstruct, lombok;  // Mapper için açılmalı
    opens org.example.ibb_ecodation_javafx.controller to javafx.fxml, lombok,org.mapstruct;
    opens org.example.ibb_ecodation_javafx.dto to javafx.base, lombok,org.mapstruct;
    opens org.example.ibb_ecodation_javafx.model to javafx.base, lombok,org.mapstruct;
    opens org.example.ibb_ecodation_javafx.dao to java.sql, lombok,org.mapstruct;
    opens org.example.ibb_ecodation_javafx.database to java.sql, lombok,org.mapstruct;
    opens org.example.ibb_ecodation_javafx.security to java.sql, lombok,org.mapstruct;
    opens org.example.ibb_ecodation_javafx.common to java.sql, lombok,org.mapstruct;
    opens org.example.ibb_ecodation_javafx.constant to java.sql, lombok,org.mapstruct;
    // Java Compiler modülünü ekleyin
    requires java.compiler;
    requires jbcrypt;
    requires jaxb.api;  // Burayı ekledik


    exports org.example.ibb_ecodation_javafx;
    exports org.example.ibb_ecodation_javafx.dao;
    exports org.example.ibb_ecodation_javafx.dto;
    exports org.example.ibb_ecodation_javafx.model;
    exports org.example.ibb_ecodation_javafx.security;
    exports org.example.ibb_ecodation_javafx.enums;
    exports org.example.ibb_ecodation_javafx.database;
    exports org.example.ibb_ecodation_javafx.common;
    exports org.example.ibb_ecodation_javafx.constant;
}
