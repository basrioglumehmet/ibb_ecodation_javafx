package org.example.ibb_ecodation_javafx.service;

import lombok.RequiredArgsConstructor;
import org.example.ibb_ecodation_javafx.core.service.GenericService;
import org.example.ibb_ecodation_javafx.model.AppLog;
import org.example.ibb_ecodation_javafx.repository.AppLogRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

public interface AppLogService extends GenericService<AppLog, Integer>{


}