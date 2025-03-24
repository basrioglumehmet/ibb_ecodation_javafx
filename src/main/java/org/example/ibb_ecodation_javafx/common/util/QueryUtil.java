package org.example.ibb_ecodation_javafx.common.util;

import lombok.experimental.UtilityClass;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Optional;

@UtilityClass
public class QueryUtil {
    private static Connection connection;

    public static void setConnection(Connection conn) {
        connection = conn;
    }

    public static <Entity> Optional<Entity> selectSingle(
            String sql, Class<Entity> entityClass, Object... params) {
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            for (int i = 0; i < params.length; i++) {
                preparedStatement.setObject(i + 1, params[i]);
            }

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    Entity entity = entityClass.getDeclaredConstructor().newInstance();
                    populateEntityFromResultSet(entity, resultSet);
                    return Optional.of(entity);
                }
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return Optional.empty();
    }

    private static <Entity> void populateEntityFromResultSet(Entity entity, ResultSet resultSet) throws Exception {
        Field[] fields = entity.getClass().getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            try {
                Object value = resultSet.getObject(field.getName());
                if (value != null) {
                    field.set(entity, value);
                }
            } catch (Exception ignored) {
                // Ignore if field doesn't exist in ResultSet
            }
        }
    }
}
