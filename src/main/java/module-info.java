module org.example.ibb_ecodation_javafx {
    requires javafx.fxml;
    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires static lombok;
    requires java.sql;
    requires com.h2database;
    requires org.mapstruct;

    // Açılması gereken paketler:
    opens org.example.ibb_ecodation_javafx to javafx.fxml, lombok, org.mapstruct, javafx.base;
    opens org.example.ibb_ecodation_javafx.controller to javafx.fxml, lombok, org.mapstruct;
    opens org.example.ibb_ecodation_javafx.ui.button to javafx.fxml, lombok, org.mapstruct;
    opens org.example.ibb_ecodation_javafx.ui.navbar to javafx.fxml, lombok, org.mapstruct;
    opens org.example.ibb_ecodation_javafx.ui.avatar to javafx.fxml, lombok, org.mapstruct;
    opens org.example.ibb_ecodation_javafx.ui.listItem to javafx.fxml, lombok, org.mapstruct;
    opens org.example.ibb_ecodation_javafx.ui.combobox to javafx.fxml, lombok, org.mapstruct;
    opens org.example.ibb_ecodation_javafx.ui.input to javafx.fxml, lombok, org.mapstruct;
    opens org.example.ibb_ecodation_javafx.service to javafx.fxml, lombok, org.mapstruct, spring.beans;
    opens org.example.ibb_ecodation_javafx.ui.dragndrop to javafx.fxml, lombok, org.mapstruct, spring.beans;

    // Bu paketleri açıyoruz
    opens org.example.ibb_ecodation_javafx.service.impl to spring.beans, org.mapstruct;
    opens org.example.ibb_ecodation_javafx.mapper to spring.beans;
    opens org.example.ibb_ecodation_javafx.repository to spring.beans;

    requires java.compiler;
    requires jbcrypt;
    requires jaxb.api;
    requires io.reactivex.rxjava3;
    requires de.jensd.fx.glyphs.fontawesome;
    requires java.desktop;
    requires com.fasterxml.jackson.databind;
    requires spring.context;
    requires spring.beans;

    exports org.example.ibb_ecodation_javafx.service.impl;
    exports org.example.ibb_ecodation_javafx;
    exports org.example.ibb_ecodation_javafx.mapper;
    exports org.example.ibb_ecodation_javafx.model;
}

//module org.example.ibb_ecodation_javafx {
//    requires javafx.fxml;
//    requires org.controlsfx.controls;
//    requires com.dlsc.formsfx;
//    requires net.synedra.validatorfx;
//    requires org.kordamp.ikonli.javafx;
//    requires org.kordamp.bootstrapfx.core;
//    // #############################################
//    // Lombok kütüphanesi, Java'da getter, setter, constructor gibi metotları otomatik oluşturur.
//    // Lombok, derleme zamanı (compile-time) kullanıldığı için "static" olarak eklenmiştir.
//    requires static lombok;
//    requires java.sql;
//    requires com.h2database;
//    requires  org.mapstruct;
//
//    // Açılması gereken paketler:
//
//
//    opens org.example.ibb_ecodation_javafx to javafx.fxml, lombok,org.mapstruct, javafx.base;
////    opens org.example.ibb_ecodation_javafx.mapper to org.mapstruct, lombok;  // Mapper için açılmalı
//    opens org.example.ibb_ecodation_javafx.controller to javafx.fxml, lombok,org.mapstruct;
//    opens org.example.ibb_ecodation_javafx.ui.button to javafx.fxml, lombok,org.mapstruct;
//    opens org.example.ibb_ecodation_javafx.ui.navbar to javafx.fxml, lombok,org.mapstruct;
//    opens org.example.ibb_ecodation_javafx.ui.avatar to javafx.fxml, lombok,org.mapstruct;
//    opens org.example.ibb_ecodation_javafx.ui.listItem to javafx.fxml, lombok,org.mapstruct;
//    opens org.example.ibb_ecodation_javafx.ui.combobox to javafx.fxml, lombok,org.mapstruct;
//    opens org.example.ibb_ecodation_javafx.ui.input to javafx.fxml, lombok,org.mapstruct;
//    opens org.example.ibb_ecodation_javafx.service to javafx.fxml, lombok,org.mapstruct, spring.beans;
//    opens org.example.ibb_ecodation_javafx.service.impl to spring.beans, org.mapstruct;
//    opens org.example.ibb_ecodation_javafx.mapper to spring.beans;
//    // Mapper için açılmalı
//    // Java Compiler modülünü ekleyin
//    requires java.compiler;
//    requires jbcrypt;
//    requires jaxb.api;
//    requires io.reactivex.rxjava3;  // Burayı ekledik
//    requires de.jensd.fx.glyphs.fontawesome;
//    requires java.desktop;
//    requires com.fasterxml.jackson.databind;
//    requires spring.context;
//    requires spring.beans;
//    exports org.example.ibb_ecodation_javafx.service.impl;
//    exports org.example.ibb_ecodation_javafx;
//    exports org.example.ibb_ecodation_javafx.mapper ;
//    exports org.example.ibb_ecodation_javafx.service;
//}
