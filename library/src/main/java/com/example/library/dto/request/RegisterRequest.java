package com.example.library.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
        @NotBlank
        String fullName,
        @Email(message = "Email không hợp lệ")
        @NotBlank(message = "Email không được để trống")
        String email,
        String phoneOrNull,
        @NotBlank(message = "Tên đăng nhập không được để trống")
        @Size(min = 6, max = 60, message = "Tên đăng nhập phải có từ 6 đến 100 ký tự")
        String username,
        @NotBlank(message = "Mật khẩu không được để trống")
        @Size(min = 6, max = 100, message = "Mật khẩu phải có từ 6 đến 100 ký tự")
        String password
) {}