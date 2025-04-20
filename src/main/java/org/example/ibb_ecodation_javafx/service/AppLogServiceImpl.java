package org.example.ibb_ecodation_javafx.service;

import lombok.RequiredArgsConstructor;
import org.example.ibb_ecodation_javafx.core.db.EntityFilter;
import org.example.ibb_ecodation_javafx.core.service.GenericService;
import org.example.ibb_ecodation_javafx.model.AppLog;
import org.example.ibb_ecodation_javafx.repository.AppLogRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service("appLogService")
@RequiredArgsConstructor
public class AppLogServiceImpl implements AppLogService {

    private final AppLogRepository appLogRepository;

    @Override
    public AppLog save(AppLog entity) {
        return appLogRepository.save(entity);
    }

    @Override
    public void update(AppLog entity) {
        appLogRepository.update(entity);
    }

    @Override
    public Optional<AppLog> findById(Integer integer) {
        return appLogRepository.findById(integer);
    }

    @Override
    public List<AppLog> findAll() {
        return appLogRepository.findAll();
    }

    @Override
    public List<AppLog> findAllById(Integer id) {
        return appLogRepository.findAllById(id);
    }

    @Override
    public List<AppLog> findAllByFilter(List<EntityFilter> filters) {
        return List.of();
    }

    @Override
    public void delete(Integer integer) {
        appLogRepository.delete(integer);
    }

    @Override
    public Optional<AppLog> findFirstByFilter(List<EntityFilter> filters) {
        return appLogRepository.findFirstByFilter(filters);
    }
}