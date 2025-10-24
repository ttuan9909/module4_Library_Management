package com.example.library.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDate;

public record ProfileUpdateRequest(
        @NotBlank String fullName,
        @Email(message="Invalid email") String email,
        String phoneNumber,
        String address,
        LocalDate dateOfBirth,
        String avatarUrl
) {}