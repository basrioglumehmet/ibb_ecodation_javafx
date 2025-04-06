package org.example.ibb_ecodation_javafx.repository.base;

import org.example.ibb_ecodation_javafx.exception.OptimisticLockException;
import org.example.ibb_ecodation_javafx.repository.GenericRepository;
import org.example.ibb_ecodation_javafx.utils.TrayUtil;
import org.springframework.stereotype.Repository;

import java.lang.reflect.Field;
import java.sql.*;
import java.util.List;

import static org.example.ibb_ecodation_javafx.utils.TrayUtil.showTrayNotification;

public class BaseRepository<T> implements GenericRepository<T> {

    protected final Connection connection;

    public BaseRepository(Connection connection) {
        this.connection = connection;
    }

    @Override
    public T create(T entity, String query, List<Object> params) {
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            setParameters(preparedStatement, params);
            int affectedRows = preparedStatement.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("Kayıt başarıyla eklendi.");
                return entity;
            } else {
                throw new RuntimeException("Kayıt eklenemedi.");
            }
        } catch (SQLException e) {
            if (e.getMessage().contains("PRIMARY KEY constraint")) {
                throw new RuntimeException("Create hatası (Duplicate veri): " + e.getMessage());
                // Gerekirse özel bir işlem yap
            } else {
                throw new RuntimeException("Create hatası: " + e.getMessage(), e);
            }
        }
    }

    @Override
    public T read(Class<T> entityClass, String query, List<Object> params) {
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            setParameters(preparedStatement, params);
            System.out.println("PreparedStatement: " + preparedStatement);  // Sorguyu logla
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return mapToEntity(entityClass, resultSet);
                } else {
                    System.out.println("Verilen ID ile ilgili entity bulunamadı." + params.get(0));  // ID'yi logla
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Read hatası: " + e.getMessage(), e);
        }
        return null;  // Veri bulunmazsa null döner
    }

    @Override
    public T update(T entity, String query, List<Object> params) {
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            // Include the version in the query parameters
            setParameters(preparedStatement, params);

            int affectedRows = preparedStatement.executeUpdate();

            // If no rows were updated, it means the version didn't match, indicating a conflict
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
            return; // No parameters to set
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

                Object value = resultSet.getObject(columnName);
                if (field.getType().isEnum() && value != null) {
                    System.out.println("Enum geldi aq");
                    field.set(entity, Enum.valueOf((Class<Enum>) field.getType(), value.toString()));
                } else {
                    field.set(entity, value);
                }
            }

            return entity;

        } catch (Exception e) {
            throw new SQLException("Error mapping ResultSet to entity: " + e.getMessage(), e);
        }
    }
}
