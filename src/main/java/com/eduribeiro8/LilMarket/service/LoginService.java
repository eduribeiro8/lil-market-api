package com.eduribeiro8.LilMarket.service;

import com.eduribeiro8.LilMarket.dto.LoginRequestDTO;
import com.eduribeiro8.LilMarket.dto.LoginResponseDTO;

public interface LoginService {

    LoginResponseDTO login(LoginRequestDTO loginRequestDTO);

}
