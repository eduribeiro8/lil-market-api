package com.eduribeiro8.LilMarket.service;

import com.eduribeiro8.LilMarket.dto.LoginRequestDTO;
import com.eduribeiro8.LilMarket.dto.LoginResponseDTO;
import com.eduribeiro8.LilMarket.entity.User;
import com.eduribeiro8.LilMarket.mapper.LoginMapper;
import com.eduribeiro8.LilMarket.repository.UserRepository;
import com.eduribeiro8.LilMarket.rest.exception.InvalidCredentialsException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LoginServiceImpl implements LoginService{

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final LoginMapper loginMapper;

    @Override
    public LoginResponseDTO login(LoginRequestDTO loginRequestDTO) {
        User user = userRepository
                .findByUsername(loginRequestDTO.username())
                .orElseThrow(() -> new InvalidCredentialsException("Username or password is incorrect!"));

        if(!passwordEncoder.matches(loginRequestDTO.password(), user.getPassword())){
            throw new InvalidCredentialsException("Username or password is incorrect!");
        }

        return loginMapper.toResponse(user);
    }
}
