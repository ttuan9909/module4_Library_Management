package com.example.library.service;

import com.example.library.dto.request.LoginRequest;
import com.example.library.dto.request.RegisterRequest;
import com.example.library.dto.response.AuthResponse;

public interface IAuthService {
    void register(RegisterRequest request, boolean asAdmin);
    AuthResponse login(LoginRequest request);
}
