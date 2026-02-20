package com.eduribeiro8.LilMarket.service;

import com.eduribeiro8.LilMarket.dto.UserRequestDTO;
import com.eduribeiro8.LilMarket.dto.UserResponseDTO;
import com.eduribeiro8.LilMarket.entity.User;
import com.eduribeiro8.LilMarket.mapper.UserMapper;
import com.eduribeiro8.LilMarket.repository.UserRepository;
import com.eduribeiro8.LilMarket.rest.exception.DuplicateUsernameException;
import com.eduribeiro8.LilMarket.rest.exception.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService{

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserResponseDTO save(UserRequestDTO user) {
        if (userRepository.findByUsername(user.username()).isPresent()){
            throw new DuplicateUsernameException("Username (" + user.username() +") already registered");
        }

        User userToSave = userMapper.toEntity(user);
        userToSave.setRole(user.userRole());
        userToSave.setPassword(passwordEncoder.encode(user.password()));

        User userSaved = userRepository.save(userToSave);

        return userMapper.toResponse(userSaved);
    }

    public UserResponseDTO findUserByUsernameDTO(String username) {
        User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new UserNotFoundException("User(username = " + username+ ") not found"));

        return userMapper.toResponse(user);
    }

    public User findUserByUsername(String username) {

        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User(username = " + username+ ") not found"));
    }

    public User findById(Long id){
        return userRepository.findById(id).orElseThrow(
                () -> new UserNotFoundException("Usuário(id = " + id + ") não encontrado.")
        );
    }
}
