package org.example.ibb_ecodation_javafx.core.mapper;

import java.util.List;

public interface GenericMapper<D, E> {
    D toDto(E entity);
    E toEntity(D dto);
    List<D> toDtoList(List<E> entityList);
    List<E> toEntityList(List<D> dtoList);
}
