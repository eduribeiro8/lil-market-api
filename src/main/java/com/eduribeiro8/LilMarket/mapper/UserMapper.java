package com.eduribeiro8.LilMarket.mapper;

import com.eduribeiro8.LilMarket.dto.UserRequestDTO;
import com.eduribeiro8.LilMarket.dto.UserResponseDTO;
import com.eduribeiro8.LilMarket.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(source = "role", target = "userRole")
    UserResponseDTO toResponse(User user);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "lastLogin", ignore = true)
    @Mapping(target = "role", source = "userRole")
    User toEntity(UserRequestDTO request);
}
