package org.example.ibb_ecodation_javafx.core.mapper;

import java.util.List;
import java.util.stream.Collectors;

public abstract class BaseMapper<D, E> implements GenericMapper<D, E> {

    @Override
    public List<D> toDtoList(List<E> entityList) {
        if (entityList == null) {
            return null;
        }
        return entityList.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<E> toEntityList(List<D> dtoList) {
        if (dtoList == null) {
            return null;
        }
        return dtoList.stream()
                .map(this::toEntity)
                .collect(Collectors.toList());
    }
}