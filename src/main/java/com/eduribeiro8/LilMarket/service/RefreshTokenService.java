package com.eduribeiro8.LilMarket.service;

import com.eduribeiro8.LilMarket.dto.RefreshTokenRequestDTO;
import com.eduribeiro8.LilMarket.entity.RefreshToken;
import com.eduribeiro8.LilMarket.entity.User;

import java.util.Optional;

public interface RefreshTokenService {

    RefreshToken createNewToken(User user);

    RefreshToken existsToken(String token);

    RefreshToken generateNewToken(RefreshTokenRequestDTO tokenRequestDTO);
}
