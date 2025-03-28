package org.example.ibb_ecodation_javafx.mapper;

import org.example.ibb_ecodation_javafx.common.interfaces.ToEntityMapper;
import org.example.ibb_ecodation_javafx.common.interfaces.ToDtoMapper;
import org.example.ibb_ecodation_javafx.dto.UserDto;
import org.example.ibb_ecodation_javafx.enums.Role;
import org.example.ibb_ecodation_javafx.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface UserMapper extends ToEntityMapper<User, UserDto>, ToDtoMapper<User, UserDto> {
    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    // Enum'dan String'e dönüşüm
    default String map(Role role) {
        return role != null ? role.toString() : null;
    }

    // String'den Enum'a dönüşüm
    default Role map(String role) {
        return Role.fromString(role);
    }
}
