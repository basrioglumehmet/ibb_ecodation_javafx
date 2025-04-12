package org.example.ibb_ecodation_javafx.service;

import org.example.ibb_ecodation_javafx.core.service.Crud;
import org.example.ibb_ecodation_javafx.model.JsonBackup;

import java.util.List;

public interface JsonBackupService extends Crud<JsonBackup> {
    List<JsonBackup> readAll();
}