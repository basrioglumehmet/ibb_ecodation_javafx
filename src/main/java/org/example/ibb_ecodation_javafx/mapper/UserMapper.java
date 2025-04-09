package org.example.ibb_ecodation_javafx.mapper;

import org.example.ibb_ecodation_javafx.model.User;
import org.example.ibb_ecodation_javafx.model.dto.RegisterDto;
import org.example.ibb_ecodation_javafx.model.dto.UserDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public abstract class UserMapper extends BaseMapper<UserDto, User> {

    @Override
    public abstract UserDto toDto(User entity);

    @Override
    public abstract User toEntity(UserDto dto);


    public abstract User toEntity(RegisterDto registerDto);
}