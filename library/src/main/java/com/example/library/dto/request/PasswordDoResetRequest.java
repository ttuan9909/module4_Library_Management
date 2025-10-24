package com.example.library.dto.request;

import jakarta.validation.constraints.NotBlank;

public record PasswordDoResetRequest(
        @NotBlank String token,
        @NotBlank String newPassword
) {}
