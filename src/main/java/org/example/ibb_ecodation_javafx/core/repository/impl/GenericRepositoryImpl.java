package org.example.ibb_ecodation_javafx.core.repository.impl;

import org.example.ibb_ecodation_javafx.core.db.EntityFilter;
import org.example.ibb_ecodation_javafx.core.repository.GenericRepository;
import org.example.ibb_ecodation_javafx.core.repository.preparator.BatchInsertPreparator;
import org.example.ibb_ecodation_javafx.exception.OptimisticLockingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import javax.sql.DataSource;
import java.lang.reflect.Method;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public abstract class GenericRepositoryImpl<T, ID> implements GenericRepository<T, ID> {

    protected final JdbcTemplate jdbcTemplate;
    private final Environment environment;

    @Autowired
    public GenericRepositoryImpl(DataSource dataSource, Environment environment) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.environment = environment;
        System.out.println("GenericRepositoryImpl constructor. Active profiles: " + Arrays.toString(environment.getActiveProfiles()));
    }

    protected abstract String getTableName();
    protected abstract RowMapper<T> getRowMapper();
    protected abstract String getInsertSql();
    protected abstract String getUpdateSql();
    protected abstract String getSelectByIdSql();
    protected abstract String getSelectAllByIdSql();
    protected abstract String getSelectAllSql();
    protected abstract String getDeleteSql();
    protected abstract Object[] getInsertParams(T entity);
    protected abstract Object[] getUpdateParams(T entity);
    protected abstract Integer getVersion(T entity);

    @Override
    public T save(T entity) {
        jdbcTemplate.update(getInsertSql(), getInsertParams(entity));
        return entity;
    }

    @Override
    public void saveAll(List<T> entities, BatchInsertPreparator<T> preparator) {
        /// Batch process yapmaktayız.
        jdbcTemplate.batchUpdate(
                getInsertSql(), //Insert Query
                new BatchPreparedStatementSetter() {
                    @Override
                    public void setValues(PreparedStatement ps, int i) throws SQLException {
                        preparator.setValues(ps, entities.get(i));
                    }

                    @Override
                    public int getBatchSize() {
                        //Batch veri boyutudur : Batch data size
                        return entities.size();
                    }
                }
        );
    }

    @Override
    public Optional<T> findById(ID id) {
        List<T> results = jdbcTemplate.query(getSelectByIdSql(), getRowMapper(), id);
        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
    }

    @Override
    public List<T> findAll() {
        return jdbcTemplate.query(getSelectAllSql(), getRowMapper());
    }

    @Override
    public List<T> findAllById(ID id) {
        return jdbcTemplate.query(getSelectAllByIdSql(), getRowMapper(), id);
    }

    @Override
    public void update(T entity) {
        Object[] params = getUpdateParams(entity);
        Integer version = getVersion(entity);
        Object[] updateParamsWithVersion = new Object[params.length + 1];
        System.arraycopy(params, 0, updateParamsWithVersion, 0, params.length - 1);
        updateParamsWithVersion[params.length - 1] = params[params.length - 1]; // id
        updateParamsWithVersion[params.length] = version;

        String optimisticUpdateSql = getUpdateSql() + " AND version = ?";
        System.out.println("SQL: " + optimisticUpdateSql);
        System.out.println("Parameters: " + Arrays.toString(updateParamsWithVersion));

        int rowsAffected = jdbcTemplate.update(optimisticUpdateSql, updateParamsWithVersion);
        if (rowsAffected == 0) {
            Optional<T> current = findById((ID) params[params.length - 1]);
            if (current.isEmpty()) {
                throw new IllegalArgumentException("Entity with ID " + params[params.length - 1] + " not found");
            }
            throw new OptimisticLockingException("Update failed due to version mismatch for entity: " + entity.getClass());
        }

    }

    @Override
    public void delete(ID id) {
        Optional<T> entityOptional = findById(id);
        if (entityOptional.isEmpty()) {
            throw new IllegalArgumentException("Entity with ID " + id + " not found");
        }

        T entity = entityOptional.get();
        Integer version = getVersion(entity);

        String optimisticDeleteSql = getDeleteSql() + " AND version = ?";
        System.out.println("Gelen id" + id);
        System.out.println(getVersion(entity));
        int rowsAffected = jdbcTemplate.update(optimisticDeleteSql, id, version);
        if (rowsAffected == 0) {
            throw new OptimisticLockingException("Delete failed due to version mismatch for ID: " + id);
        }
    }

    @Override
    public List<T> findAllByFilter(List<EntityFilter> filters) {
        StringBuilder sql = new StringBuilder("SELECT * FROM " + getTableName());
        List<Object> params = new ArrayList<>();
        List<String> conditions = new ArrayList<>();

        if (filters != null && !filters.isEmpty()) {
            sql.append(" WHERE ");
            for (int i = 0; i < filters.size(); i++) {
                EntityFilter filter = filters.get(i);
                String column = filter.getColumn().replaceAll("[^a-zA-Z0-9_]", "");
                String operator = filter.getOperator();

                if (!isValidOperator(operator)) {
                    throw new IllegalArgumentException("Invalid operator: " + operator);
                }

                conditions.add(column + " " + operator + " ?");
                params.add(normalizeValue(filter.getValue(), operator));
            }
            sql.append(String.join(" AND ", conditions));
        }

        return jdbcTemplate.query(sql.toString(), params.toArray(), getRowMapper());
    }

    @Override
    public Optional<T> findFirstByFilter(List<EntityFilter> filters) {
        StringBuilder sql = new StringBuilder("SELECT * FROM " + getTableName());
        List<Object> params = new ArrayList<>();
        List<String> conditions = new ArrayList<>();

        if (filters != null && !filters.isEmpty()) {
            sql.append(" WHERE ");
            for (int i = 0; i < filters.size(); i++) {
                EntityFilter filter = filters.get(i);
                String column = filter.getColumn().replaceAll("[^a-zA-Z0-9_]", "");
                String operator = filter.getOperator();

                if (!isValidOperator(operator)) {
                    throw new IllegalArgumentException("Invalid operator: " + operator);
                }

                conditions.add(column + " " + operator + " ?");
                params.add(normalizeValue(filter.getValue(), operator));
            }
            sql.append(String.join(" AND ", conditions));
        }

        String activeProfile = getActiveProfile();
        System.out.println("findFirstByFilter - Active profile: " + activeProfile);
        if ("mssql".equalsIgnoreCase(activeProfile)) {
            sql.insert(7, "TOP 1 ");
        } else {
            sql.append(" LIMIT 1");
        }

        List<T> results = jdbcTemplate.query(sql.toString(), params.toArray(), getRowMapper());
        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
    }

    private boolean isValidOperator(String operator) {
        return List.of("=", ">", "<", ">=", "<=", "!=", "LIKE").contains(operator);
    }

    private Object normalizeValue(Object value, String operator) {
        if ("LIKE".equalsIgnoreCase(operator) && value instanceof String) {
            return "%" + value + "%";
        }
        return value;
    }

    private String getActiveProfile() {
        String[] activeProfiles = environment.getActiveProfiles();
        System.out.println("getActiveProfile - All active profiles: " + Arrays.toString(activeProfiles));
        if (activeProfiles.length == 0) {
            System.out.println("getActiveProfile - No active profiles found, defaulting to 'default'");
            return "default";
        }
        String profile = activeProfiles[0];
        System.out.println("getActiveProfile - Detected active profile: " + profile);
        return profile;
    }
}