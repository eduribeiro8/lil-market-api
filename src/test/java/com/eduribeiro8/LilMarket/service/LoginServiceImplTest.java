package com.eduribeiro8.LilMarket.service;

import com.eduribeiro8.LilMarket.dto.LoginRequestDTO;
import com.eduribeiro8.LilMarket.dto.LoginResponseDTO;
import com.eduribeiro8.LilMarket.entity.User;
import com.eduribeiro8.LilMarket.entity.UserRole;
import com.eduribeiro8.LilMarket.mapper.LoginMapper;
import com.eduribeiro8.LilMarket.repository.UserRepository;
import com.eduribeiro8.LilMarket.rest.exception.InvalidCredentialsException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("LoginService Unit Tests")
class LoginServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private LoginMapper loginMapper;

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
            LoginResponseDTO expectedResponse = new LoginResponseDTO(1, "joao", UserRole.ROLE_ADMIN);

            Mockito.when(userRepository.findByUsername(loginRequestDTO.username())).thenReturn(Optional.of(user));
            Mockito.when(passwordEncoder.matches(loginRequestDTO.password(), user.getPassword())).thenReturn(true);
            Mockito.when(loginMapper.toResponse(user)).thenReturn(expectedResponse);

            // Act
            LoginResponseDTO response = loginService.login(loginRequestDTO);

            // Assert
            assertNotNull(response);
            assertEquals(expectedResponse.id(), response.id());
            assertEquals(expectedResponse.username(), response.username());
            assertEquals(expectedResponse.userRole(), response.userRole());

            Mockito.verify(userRepository).findByUsername(loginRequestDTO.username());
            Mockito.verify(passwordEncoder).matches(loginRequestDTO.password(), user.getPassword());
            Mockito.verify(loginMapper).toResponse(user);
            Mockito.verifyNoMoreInteractions(userRepository, passwordEncoder, loginMapper);
        }

        @Test
        @DisplayName("Deve lançar InvalidCredentialsException quando o usuário não for encontrado")
        void login_Fail_UserNotFound() {
            // Arrange
            Mockito.when(userRepository.findByUsername(loginRequestDTO.username())).thenReturn(Optional.empty());

            // Act & Assert
            InvalidCredentialsException exception = assertThrows(InvalidCredentialsException.class, () -> {
                loginService.login(loginRequestDTO);
            });

            assertEquals("Username or password is incorrect!", exception.getMessage());

            Mockito.verify(userRepository).findByUsername(loginRequestDTO.username());
            Mockito.verifyNoInteractions(passwordEncoder, loginMapper);
            Mockito.verifyNoMoreInteractions(userRepository);
        }

        @Test
        @DisplayName("Deve lançar InvalidCredentialsException quando a senha estiver incorreta")
        void login_Fail_IncorrectPassword() {
            // Arrange
            Mockito.when(userRepository.findByUsername(loginRequestDTO.username())).thenReturn(Optional.of(user));
            Mockito.when(passwordEncoder.matches(loginRequestDTO.password(), user.getPassword())).thenReturn(false);

            // Act & Assert
            InvalidCredentialsException exception = assertThrows(InvalidCredentialsException.class, () -> {
                loginService.login(loginRequestDTO);
            });

            assertEquals("Username or password is incorrect!", exception.getMessage());

            Mockito.verify(userRepository).findByUsername(loginRequestDTO.username());
            Mockito.verify(passwordEncoder).matches(loginRequestDTO.password(), user.getPassword());
            Mockito.verifyNoInteractions(loginMapper);
            Mockito.verifyNoMoreInteractions(userRepository, passwordEncoder);
        }
    }
}
