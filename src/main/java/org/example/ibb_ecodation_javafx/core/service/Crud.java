package org.example.ibb_ecodation_javafx.core.service;

import org.example.ibb_ecodation_javafx.core.db.Create;
import org.example.ibb_ecodation_javafx.core.db.Delete;
import org.example.ibb_ecodation_javafx.core.db.Read;
import org.example.ibb_ecodation_javafx.core.db.Update;

public interface Crud<T> extends Create<T>, Read<T>, Update<T>, Delete<T> {
}
