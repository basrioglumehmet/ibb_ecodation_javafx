package org.example.ibb_ecodation_javafx.service;

import lombok.RequiredArgsConstructor;
import org.example.ibb_ecodation_javafx.core.db.EntityFilter;
import org.example.ibb_ecodation_javafx.model.JsonBackup;
import org.example.ibb_ecodation_javafx.repository.JsonBackupRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class JsonBackupServiceImpl implements JsonBackupService{
    private final JsonBackupRepository jsonBackupRepository;

    @Override
    public JsonBackup save(JsonBackup entity) {
        return jsonBackupRepository.save(entity);
    }

    @Override
    public void update(JsonBackup entity) {
        jsonBackupRepository.update(entity);
    }

    @Override
    public Optional<JsonBackup> findById(Integer integer) {
        return jsonBackupRepository.findById(integer);
    }

    @Override
    public List<JsonBackup> findAll() {
        return jsonBackupRepository.findAll();
    }

    @Override
    public List<JsonBackup> findAllById(Integer id) {
        return jsonBackupRepository.findAllById(id);
    }

    @Override
    public List<JsonBackup> findAllByFilter(List<EntityFilter> filters) {
        return List.of();
    }

    @Override
    public void delete(Integer integer) {
        jsonBackupRepository.delete(integer);
    }

    @Override
    public Optional<JsonBackup> findFirstByFilter(List<EntityFilter> filters) {
        return Optional.empty();
    }
}
