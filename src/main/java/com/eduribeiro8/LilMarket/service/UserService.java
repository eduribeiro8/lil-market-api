package com.eduribeiro8.LilMarket.service;

import com.eduribeiro8.LilMarket.dto.UserRequestDTO;
import com.eduribeiro8.LilMarket.dto.UserResponseDTO;
import com.eduribeiro8.LilMarket.entity.User;

public interface UserService {

    UserResponseDTO save(UserRequestDTO user);

    UserResponseDTO findUserByUsernameDTO(String username);

    User findUserByUsername(String username);

    User findById(Long id);
}
