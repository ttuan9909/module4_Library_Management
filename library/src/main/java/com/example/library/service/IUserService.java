package com.example.library.service;

import com.example.library.dto.request.ChangePasswordRequest;
import com.example.library.dto.request.ProfileUpdateRequest;
import com.example.library.entity.UserAccount;

public interface IUserService {
    UserAccount me(String username);
    UserAccount updateProfile(String username, ProfileUpdateRequest request);
    void changePassword(String username, ChangePasswordRequest request);
    UserAccount findByUserIdOrPhoneNumberOrEmail(Long userId, String phoneNumber, String email);
}
