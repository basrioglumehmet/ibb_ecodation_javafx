package org.example.ibb_ecodation_javafx.repository.base;

import org.example.ibb_ecodation_javafx.annotation.JdbcNamedField;
import org.example.ibb_ecodation_javafx.exception.OptimisticLockException;
import org.example.ibb_ecodation_javafx.repository.GenericRepository;
import org.example.ibb_ecodation_javafx.utils.TrayUtil;
import org.springframework.stereotype.Repository;

import java.lang.reflect.Field;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.example.ibb_ecodation_javafx.utils.TrayUtil.showTrayNotification;

public class BaseRepository<T> implements GenericRepository<T> {

    protected final Connection connection;


    public BaseRepository(Connection connection) {
        this.connection = connection;
    }
    public void saveAll(String query,List<List<Object>> paramsList) {
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            for (List<Object> params : paramsList) {
                for (int i = 0; i < params.size(); i++) {
                    preparedStatement.setObject(i + 1, params.get(i));
                }
                preparedStatement.addBatch();
            }
            preparedStatement.executeBatch();  // Veritabanına toplu ekleme yapıyoruz
        } catch (SQLException e) {
            throw new RuntimeException("SaveAll hatası: " + e.getMessage(), e);
        }
    }

    @Override
    public T create(T entity, String query, List<Object> params) {
        try (PreparedStatement preparedStatement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            setParameters(preparedStatement, params);
            int affectedRows = preparedStatement.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        T createdEntity = entity;
                        try {
                            Field idField = entity.getClass().getDeclaredField("id");
                            if (idField != null) {
                                idField.setAccessible(true);
                                idField.setInt(createdEntity, generatedKeys.getInt(1));
                                System.out.println("Kayıt başarıyla eklendi. Generated ID: " + generatedKeys.getInt(1));
                            }
                        } catch (NoSuchFieldException e) {
                            // id alanı yoksa devam et
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        }

                        return createdEntity;
                    }
                    else {
                        System.out.println("Kayıt eklendi ancak generated keys alınamadı.");
                        return entity;
                    }
                }
            } else {
                throw new RuntimeException("Kayıt eklenemedi.");
            }
        } catch (SQLException e) {
            if (e.getMessage().contains("PRIMARY KEY constraint")) {
                throw new RuntimeException("Create hatası (Duplicate veri): " + e.getMessage());
            } else {
                throw new RuntimeException("Create hatası: " + e.getMessage(), e);
            }
        }
    }

    @Override
    public T read(Class<T> entityClass, String query, List<Object> params) {
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            setParameters(preparedStatement, params);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return mapToEntity(entityClass, resultSet);
                } else {
                    System.out.println("Verilen ID ile ilgili entity bulunamadı." + params.get(0));
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Read hatası: " + e.getMessage(), e);
        }
        return null;
    }

    @Override
    public List<T> readAll(Class<T> entityClass, String query, List<Object> params) {
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            setParameters(preparedStatement, params);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                List<T> entities = new ArrayList<>();
                while (resultSet.next()) {
                    T entity = mapToEntity(entityClass, resultSet);
                    entities.add(entity);
                }
                return entities;
            }
        } catch (Exception e) {
            throw new RuntimeException("ReadAll hatası: " + e.getMessage(), e);
        }
    }


    @Override
    public T update(T entity, String query, List<Object> params) {
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            setParameters(preparedStatement, params);
            int affectedRows = preparedStatement.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("Kayıt başarıyla güncellendi.");
                return entity;
            } else {
                showTrayNotification("Optimistic locking! Entity Versiyonu uyuşmuyor.", "IBB ve Ecodation Bootcamp Projesi");
                throw new OptimisticLockException("Optimistic locking hatası: versiyon uyuşmazlığı.");
            }
        } catch (Exception e) {
            throw new RuntimeException("Update hatası: " + e.getMessage(), e);
        }
    }

    @Override
    public Boolean delete(String query, List<Object> params) {
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            setParameters(preparedStatement, params);
            int affectedRows = preparedStatement.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("Kayıt başarıyla silindi.");
                return true;
            } else {
                showTrayNotification("Optimistic locking! Entity Versiyonu uyuşmuyor.", "IBB ve Ecodation Bootcamp Projesi");
                throw new OptimisticLockException("Optimistic locking hatası: versiyon uyuşmazlığı.");
            }
        } catch (Exception e) {
            throw new RuntimeException("Delete hatası: " + e.getMessage(), e);
        }
    }

    private void setParameters(PreparedStatement preparedStatement, List<Object> params) throws SQLException {
        if (params == null || params.isEmpty()) {
            return;
        }
        for (int i = 0; i < params.size(); i++) {
            preparedStatement.setObject(i + 1, params.get(i));
        }
    }

    private T mapToEntity(Class<T> entityClass, ResultSet resultSet) throws SQLException {
        try {
            T entity = entityClass.getDeclaredConstructor().newInstance();
            for (Field field : entityClass.getDeclaredFields()) {
                field.setAccessible(true);
                String columnName = field.getName();
                JdbcNamedField annotation = field.getAnnotation(JdbcNamedField.class);
                if (annotation != null && !annotation.dbFieldName().isEmpty()) {
                    columnName = annotation.dbFieldName();
                }
                Object value = resultSet.getObject(columnName);
                if (value != null) {
                    if (field.getType().equals(LocalDateTime.class) && value instanceof Timestamp) {
                        field.set(entity, ((Timestamp) value).toLocalDateTime());
                    } else if (field.getType().isEnum()) {
                        field.set(entity, Enum.valueOf((Class<Enum>) field.getType(), value.toString()));
                    } else {
                        field.set(entity, value);
                    }
                }
            }
            return entity;
        } catch (Exception e) {
            throw new SQLException("Result set verisini entity modeline çevirirken sorun oldu " + e.getMessage(), e);
        }
    }

}