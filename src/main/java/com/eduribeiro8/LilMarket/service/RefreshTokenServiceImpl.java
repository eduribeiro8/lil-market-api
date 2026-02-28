package com.eduribeiro8.LilMarket.service;

import com.eduribeiro8.LilMarket.dto.RefreshTokenRequestDTO;
import com.eduribeiro8.LilMarket.entity.RefreshToken;
import com.eduribeiro8.LilMarket.entity.User;
import com.eduribeiro8.LilMarket.repository.RefreshTokenRepository;
import com.eduribeiro8.LilMarket.rest.exception.RefreshTokenException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenServiceImpl implements RefreshTokenService{

    private final RefreshTokenRepository refreshTokenRepository;

    @Value("${api.security.refresh-token.expiration}")
    private Long expirationTime;

    @Override
    @Transactional
    public RefreshToken createNewToken(User user) {

        RefreshToken refreshToken = RefreshToken.builder()
                .user(user)
                .token(UUID.randomUUID().toString())
                .expirationDate(OffsetDateTime.now().plusNanos(expirationTime * 1_000_000))
                .isRevoked(false)
                .build();

        return refreshTokenRepository.save(refreshToken);
    }

    @Override
    @Transactional(readOnly = true)
    public RefreshToken existsToken(String token) {
        return refreshTokenRepository.findByToken(token)
                .orElseThrow(() -> new RefreshTokenException("Refresh Token não encontrado"));
    }

    @Override
    @Transactional
    public RefreshToken generateNewToken(RefreshTokenRequestDTO tokenRequestDTO) {
        RefreshToken refreshToken = existsToken(tokenRequestDTO.refreshToken());

        verifyToken(refreshToken);

        if (refreshToken.getIsRevoked()){
            throw new RefreshTokenException("Refresh Token cancelado");
        }

        refreshTokenRepository.delete(refreshToken);

        return createNewToken(refreshToken.getUser());
    }

    private void verifyToken(RefreshToken refreshToken){
        if (refreshToken.getExpirationDate().isBefore(OffsetDateTime.now())){
            refreshTokenRepository.delete(refreshToken);
            throw new RefreshTokenException("Refresh Token expirado");
        }
    }
}
