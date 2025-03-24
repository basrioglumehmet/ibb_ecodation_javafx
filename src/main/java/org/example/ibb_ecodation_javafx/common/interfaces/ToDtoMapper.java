package org.example.ibb_ecodation_javafx.common.interfaces;

// Converts Entity -> DTO
public interface ToDtoMapper<Entity, Dto> {
    Dto toDto(Entity entity);
}