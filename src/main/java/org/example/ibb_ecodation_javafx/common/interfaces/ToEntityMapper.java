package org.example.ibb_ecodation_javafx.common.interfaces;

// Converts DTO -> Entity
public interface ToEntityMapper<Entity, Dto> {
    Entity toEntity(Dto dto);
}