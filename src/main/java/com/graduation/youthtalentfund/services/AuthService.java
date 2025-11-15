package com.graduation.youthtalentfund.services;

import com.graduation.youthtalentfund.dtos.response.AuthResponseDTO;
import com.graduation.youthtalentfund.dtos.request.LoginDTO;
import com.graduation.youthtalentfund.dtos.request.RegisterDTO;

public interface AuthService {
    void register(RegisterDTO registerDTO);
    AuthResponseDTO login(LoginDTO loginDTO);
}
