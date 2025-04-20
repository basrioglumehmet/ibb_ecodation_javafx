package org.example.ibb_ecodation_javafx.core.service;

import org.example.ibb_ecodation_javafx.core.db.EntityFilter;
import org.example.ibb_ecodation_javafx.model.User;

import java.beans.Expression;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public interface GenericService<T, ID> {

    /**
     * Yeni bir kayıt oluşturur.
     *
     * @param entity Oluşturulacak nesne
     * @return Oluşturulan  nesne
     */
    T save(T entity);

    /**
     * kayıt güncellemesi yapar.
     *
     * @param entity güncellenecek nesne
     */
    void update(T entity);



    /**
     * Var olan kayıdı okur.
     *
     * @param id Okunulacak nesne id tanımı
     * @return Optional dönülen nesne
     */

    Optional<T> findById(ID id);
    /**
     * Var olan tüm kayıtları okur.
     *
     * @return Optional dönülen list of T tipi nesne
     */
    List<T> findAll();
    /**
     * Var olan tüm kayıtları verilen id göre okur.
     *
     * @return Optional dönülen list of T tipi nesne
     */
    List<T> findAllById(Integer id);

    List<T> findAllByFilter(List<EntityFilter> filters);
    /**
     * Var olan kayıdı siler.
     *
     * @param id Silenecek nesne id tanımı
     */
    void delete(ID id);

    Optional<T> findFirstByFilter(List<EntityFilter> filters);
}
