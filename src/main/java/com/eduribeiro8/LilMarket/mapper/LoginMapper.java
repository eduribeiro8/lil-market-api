package com.eduribeiro8.LilMarket.mapper;

import com.eduribeiro8.LilMarket.dto.LoginResponseDTO;
import com.eduribeiro8.LilMarket.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface LoginMapper {
    @Mapping(source = "role", target = "userRole")
    LoginResponseDTO toResponse(User user);
}
