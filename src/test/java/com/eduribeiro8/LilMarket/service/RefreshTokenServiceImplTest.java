package com.eduribeiro8.LilMarket.service;

import com.eduribeiro8.LilMarket.dto.RefreshTokenRequestDTO;
import com.eduribeiro8.LilMarket.entity.RefreshToken;
import com.eduribeiro8.LilMarket.entity.User;
import com.eduribeiro8.LilMarket.repository.RefreshTokenRepository;
import com.eduribeiro8.LilMarket.rest.exception.RefreshTokenException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.OffsetDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
@DisplayName("RefreshTokenService Unit Tests")
class RefreshTokenServiceImplTest {

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @InjectMocks
    private RefreshTokenServiceImpl refreshTokenService;

    private User user;
    private RefreshToken refreshToken;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(refreshTokenService, "expirationTime", 86400000L);
        user = User.builder().id(1L).username("testuser").build();
        refreshToken = RefreshToken.builder()
                .id(1L)
                .token("test-token")
                .user(user)
                .expirationDate(OffsetDateTime.now().plusDays(1))
                .isRevoked(false)
                .build();
    }

    @Nested
    @DisplayName("Create Token Tests")
    class CreateToken {
        @Test
        @DisplayName("Deve criar um novo token com sucesso")
        void createNewToken_Success() {
            Mockito.when(refreshTokenRepository.save(any(RefreshToken.class))).thenReturn(refreshToken);

            RefreshToken result = refreshTokenService.createNewToken(user);

            assertNotNull(result);
            assertEquals("test-token", result.getToken());
            Mockito.verify(refreshTokenRepository).save(any(RefreshToken.class));
        }
    }

    @Nested
    @DisplayName("Exists Token Tests")
    class ExistsToken {
        @Test
        @DisplayName("Deve retornar token quando encontrado")
        void existsToken_Found() {
            Mockito.when(refreshTokenRepository.findByToken("test-token")).thenReturn(Optional.of(refreshToken));

            RefreshToken result = refreshTokenService.existsToken("test-token");

            assertNotNull(result);
            assertEquals("test-token", result.getToken());
        }

        @Test
        @DisplayName("Deve lançar exceção quando token não for encontrado")
        void existsToken_NotFound() {
            Mockito.when(refreshTokenRepository.findByToken("invalid")).thenReturn(Optional.empty());

            assertThrows(RefreshTokenException.class, () -> refreshTokenService.existsToken("invalid"));
        }
    }

    @Nested
    @DisplayName("Generate New Token Tests")
    class GenerateNewToken {
        @Test
        @DisplayName("Deve rotacionar o token com sucesso")
        void generateNewToken_Success() {
            RefreshTokenRequestDTO request = new RefreshTokenRequestDTO("test-token");
            Mockito.when(refreshTokenRepository.findByToken("test-token")).thenReturn(Optional.of(refreshToken));
            Mockito.when(refreshTokenRepository.save(any(RefreshToken.class))).thenReturn(refreshToken);

            RefreshToken result = refreshTokenService.generateNewToken(request);

            assertNotNull(result);
            Mockito.verify(refreshTokenRepository).delete(refreshToken);
            Mockito.verify(refreshTokenRepository).save(any(RefreshToken.class));
        }

        @Test
        @DisplayName("Deve lançar exceção se o token estiver revogado")
        void generateNewToken_Revoked() {
            refreshToken.setIsRevoked(true);
            RefreshTokenRequestDTO request = new RefreshTokenRequestDTO("test-token");
            Mockito.when(refreshTokenRepository.findByToken("test-token")).thenReturn(Optional.of(refreshToken));

            assertThrows(RefreshTokenException.class, () -> refreshTokenService.generateNewToken(request));
        }

        @Test
        @DisplayName("Deve lançar exceção e deletar se o token estiver expirado")
        void generateNewToken_Expired() {
            refreshToken.setExpirationDate(OffsetDateTime.now().minusMinutes(1));
            RefreshTokenRequestDTO request = new RefreshTokenRequestDTO("test-token");
            Mockito.when(refreshTokenRepository.findByToken("test-token")).thenReturn(Optional.of(refreshToken));

            assertThrows(RefreshTokenException.class, () -> refreshTokenService.generateNewToken(request));
            Mockito.verify(refreshTokenRepository).delete(refreshToken);
        }
    }
}
