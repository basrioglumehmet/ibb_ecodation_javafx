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
    requires org.mapstruct;

    // Açılması gereken paketler:
    opens org.example.ibb_ecodation_javafx to javafx.fxml, lombok,org.mapstruct;
    opens org.example.ibb_ecodation_javafx.mapper to org.mapstruct;  // Mapper için açılmalı
    opens org.example.ibb_ecodation_javafx.controller to javafx.fxml, lombok;
    opens org.example.ibb_ecodation_javafx.dto to javafx.base, lombok;
    opens org.example.ibb_ecodation_javafx.model to javafx.base, lombok;
    opens org.example.ibb_ecodation_javafx.dao to java.sql, lombok;
    opens org.example.ibb_ecodation_javafx.database to java.sql, lombok;

    // Java Compiler modülünü ekleyin
    requires java.compiler;  // Burayı ekledik


    exports org.example.ibb_ecodation_javafx;
    exports org.example.ibb_ecodation_javafx.dao;
    exports org.example.ibb_ecodation_javafx.dto;
    exports org.example.ibb_ecodation_javafx.model;
    exports org.example.ibb_ecodation_javafx.database;
}
