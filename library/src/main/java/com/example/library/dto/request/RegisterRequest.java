package com.example.library.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
        @NotBlank String fullName,
        @Email @NotBlank String email,
        String phoneOrNull,
        @NotBlank @Size(min=6, max=60) String username,
        @NotBlank @Size(min=6, max=100) String password
) {}