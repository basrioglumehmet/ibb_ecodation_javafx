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
    requires javafx.web;

    requires sendgrid.java;

    opens org.example.ibb_ecodation_javafx to javafx.fxml, lombok, org.mapstruct, javafx.base;
    opens org.example.ibb_ecodation_javafx.controller to javafx.fxml, lombok, org.mapstruct;
    opens org.example.ibb_ecodation_javafx.ui.button to javafx.fxml, lombok, org.mapstruct;
    opens org.example.ibb_ecodation_javafx.ui.navbar to javafx.fxml, lombok, org.mapstruct;
    opens org.example.ibb_ecodation_javafx.ui.avatar to javafx.fxml, lombok, org.mapstruct;
    opens org.example.ibb_ecodation_javafx.ui.listItem to javafx.fxml, lombok, org.mapstruct;
    opens org.example.ibb_ecodation_javafx.ui.combobox to javafx.fxml, lombok, org.mapstruct;
    opens org.example.ibb_ecodation_javafx.ui.spinner to javafx.fxml, lombok, org.mapstruct;
    opens org.example.ibb_ecodation_javafx.ui.input to javafx.fxml, lombok, org.mapstruct;
    opens org.example.ibb_ecodation_javafx.service to javafx.fxml, lombok, org.mapstruct, spring.beans;
    opens org.example.ibb_ecodation_javafx.ui.dragndrop to javafx.fxml, lombok, org.mapstruct, spring.beans;
    opens org.example.ibb_ecodation_javafx.ui.splitpane to javafx.fxml, lombok, org.mapstruct, spring.beans;
    opens org.example.ibb_ecodation_javafx.ui.chart to javafx.fxml, lombok, org.mapstruct, spring.beans;
    opens org.example.ibb_ecodation_javafx.ui.table to javafx.fxml, javafx.base, lombok, org.mapstruct, spring.beans;
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
    requires java.net.http;

    exports org.example.ibb_ecodation_javafx.service.impl;
    exports org.example.ibb_ecodation_javafx;
    exports org.example.ibb_ecodation_javafx.mapper;
    exports org.example.ibb_ecodation_javafx.model;
    exports org.example.ibb_ecodation_javafx.model.dto;
}