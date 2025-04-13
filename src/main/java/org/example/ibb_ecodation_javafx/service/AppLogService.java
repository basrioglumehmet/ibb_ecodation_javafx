package org.example.ibb_ecodation_javafx.service;

import org.example.ibb_ecodation_javafx.core.service.Crud;
import org.example.ibb_ecodation_javafx.model.AppLog;

import java.util.List;

public interface AppLogService extends Crud<AppLog> {
    List<AppLog> readAll();
}