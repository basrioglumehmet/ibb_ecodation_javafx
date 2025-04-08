package org.example.ibb_ecodation_javafx.repository;

import java.util.List;

public interface GenericRepository<T> {

    // Verilen sorgu ve parametrelerle bir kayıt oluşturur
    T create(T entity, String query, List<Object> params);

    // Verilen sorgu ve parametrelerle bir entity'yi okur
    T read(Class<T> entityClass, String query, List<Object> params);


    // Verilen sorgu ve parametrelerle bir entity'yi günceller
    T update(T entity, String query, List<Object> params);

    // Verilen sorgu ve parametrelerle bir entity'yi siler
    Boolean delete(String query, List<Object> params);
}
