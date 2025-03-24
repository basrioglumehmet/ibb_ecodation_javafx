package org.example.ibb_ecodation_javafx.common.interfaces;

import org.example.ibb_ecodation_javafx.dto.UserDto;

public interface Mappable<Entity,Dto> {
    Entity toEntity(Dto dto);
    Dto toDto(Entity user);
}
