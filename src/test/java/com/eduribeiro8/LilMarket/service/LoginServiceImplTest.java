package com.eduribeiro8.LilMarket.service;

import com.eduribeiro8.LilMarket.dto.LoginRequestDTO;
import com.eduribeiro8.LilMarket.dto.LoginResponseDTO;
import com.eduribeiro8.LilMarket.entity.User;
import com.eduribeiro8.LilMarket.entity.UserRole;
import com.eduribeiro8.LilMarket.mapper.LoginMapper;
import com.eduribeiro8.LilMarket.repository.UserRepository;
import com.eduribeiro8.LilMarket.rest.exception.InvalidCredentialsException;
import com.eduribeiro8.LilMarket.security.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
@DisplayName("LoginService Unit Tests")
class LoginServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private LoginMapper loginMapper;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private LoginServiceImpl loginService;

    private LoginRequestDTO loginRequestDTO;
    private User user;

    @BeforeEach
    void setUp() {
        loginRequestDTO = new LoginRequestDTO("joao", "password123");
        user = User.builder()
                .id(1L)
                .username("joao")
                .password("encodedPassword")
                .role(UserRole.ROLE_ADMIN)
                .build();
    }

    @Nested
    @DisplayName("Login Tests")
    class Login {

        @Test
        @DisplayName("Deve realizar login com sucesso quando as credenciais forem válidas")
        void login_Success() {
            // Arrange
            String token = "jwt.token.here";
            long expiresIn = 3600000L;
            LoginResponseDTO expectedResponse = new LoginResponseDTO(1L, "joao", UserRole.ROLE_ADMIN, token, expiresIn);

            Mockito.when(userRepository.findByUsername(loginRequestDTO.username())).thenReturn(Optional.of(user));
            Mockito.when(jwtService.generateToken(user)).thenReturn(token);
            Mockito.when(jwtService.getExpirationTime(token)).thenReturn(expiresIn);
            Mockito.when(loginMapper.toResponse(user, token, expiresIn)).thenReturn(expectedResponse);

            // Act
            LoginResponseDTO response = loginService.login(loginRequestDTO);

            // Assert
            assertNotNull(response);
            assertEquals(expectedResponse.id(), response.id());
            assertEquals(expectedResponse.username(), response.username());
            assertEquals(expectedResponse.userRole(), response.userRole());
            assertEquals(token, response.token());
            assertEquals(expiresIn, response.expiresIn());

            Mockito.verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
            Mockito.verify(userRepository).findByUsername(loginRequestDTO.username());
            Mockito.verify(jwtService).generateToken(user);
            Mockito.verify(jwtService).getExpirationTime(token);
            Mockito.verify(loginMapper).toResponse(user, token, expiresIn);
            Mockito.verifyNoMoreInteractions(userRepository, jwtService, loginMapper, authenticationManager);
        }

        @Test
        @DisplayName("Deve lançar exceção quando a autenticação falhar")
        void login_Fail_AuthenticationException() {
            // Arrange
            Mockito.when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                    .thenThrow(new BadCredentialsException("Invalid credentials"));

            // Act & Assert
            assertThrows(BadCredentialsException.class, () -> {
                loginService.login(loginRequestDTO);
            });

            Mockito.verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
            Mockito.verifyNoInteractions(userRepository, jwtService, loginMapper);
        }

        @Test
        @DisplayName("Deve lançar InvalidCredentialsException quando o usuário não for encontrado após autenticação")
        void login_Fail_UserNotFound() {
            // Arrange
            Mockito.when(userRepository.findByUsername(loginRequestDTO.username())).thenReturn(Optional.empty());

            // Act & Assert
            InvalidCredentialsException exception = assertThrows(InvalidCredentialsException.class, () -> {
                loginService.login(loginRequestDTO);
            });

            assertEquals("Username or password is incorrect!", exception.getMessage());

            Mockito.verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
            Mockito.verify(userRepository).findByUsername(loginRequestDTO.username());
            Mockito.verifyNoInteractions(jwtService, loginMapper);
        }
    }
}
