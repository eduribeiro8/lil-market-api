package com.eduribeiro8.LilMarket.service;

import com.eduribeiro8.LilMarket.dto.LoginRequestDTO;
import com.eduribeiro8.LilMarket.dto.LoginResponseDTO;
import com.eduribeiro8.LilMarket.entity.User;
import com.eduribeiro8.LilMarket.mapper.LoginMapper;
import com.eduribeiro8.LilMarket.repository.UserRepository;
import com.eduribeiro8.LilMarket.rest.exception.InvalidCredentialsException;
import com.eduribeiro8.LilMarket.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LoginServiceImpl implements LoginService{

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final LoginMapper loginMapper;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    @Override
    public LoginResponseDTO login(LoginRequestDTO loginRequestDTO) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequestDTO.username(),
                        loginRequestDTO.password()
                )
        );

        User user = userRepository
                .findByUsername(loginRequestDTO.username())
                .orElseThrow(() -> new InvalidCredentialsException("Username or password is incorrect!"));

       String jwtToken = jwtService.generateToken(user);
       long expiresIn = jwtService.getExpirationTime(jwtToken);

        return loginMapper.toResponse(user, jwtToken, expiresIn);
    }
}
