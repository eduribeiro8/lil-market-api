package com.eduribeiro8.LilMarket.mapper;

import com.eduribeiro8.LilMarket.dto.LoginResponseDTO;
import com.eduribeiro8.LilMarket.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface LoginMapper {
    @Mapping(source = "user.id", target = "id")
    @Mapping(source = "user.username", target = "username")
    @Mapping(source = "user.role", target = "userRole")
    @Mapping(source = "token", target = "token")
    @Mapping(source = "expiresIn", target = "expiresIn")
    LoginResponseDTO toResponse(User user, String token, long expiresIn);
}
