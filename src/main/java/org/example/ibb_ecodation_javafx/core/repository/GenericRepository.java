package org.example.ibb_ecodation_javafx.core.repository;

import org.example.ibb_ecodation_javafx.core.db.EntityFilter;
import org.example.ibb_ecodation_javafx.core.repository.preparator.BatchInsertPreparator;
import org.example.ibb_ecodation_javafx.model.User;

import java.util.List;
import java.util.Optional;

public interface GenericRepository<T, ID> {
    T save(T entity);
    void saveAll(List<T> entities, BatchInsertPreparator<T> preparator);
    Optional<T> findById(ID id);
    List<T> findAll();
    List<T> findAllById(ID id);
    List<T> findAllByFilter(List<EntityFilter> filters); // New method
    Optional<T> findFirstByFilter(List<EntityFilter> filters);
    void delete(ID id);
    void update(T entity);
}