package com.example.library.service;

import com.example.library.dto.request.ChangePasswordRequest;
import com.example.library.dto.request.ProfileUpdateRequest;
import com.example.library.entity.UserAccount;
import com.example.library.repository.UserAccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService implements IUserService {
    private final UserAccountRepository userRepo;
    private final PasswordEncoder encoder;

    @Override
    public UserAccount me(String username) {
        return userRepo.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
    }

    @Override
    @Transactional
    public UserAccount updateProfile(String username, ProfileUpdateRequest req) {
        UserAccount user = me(username);
        user.setFullName(req.fullName());
        user.setEmail(req.email());
        user.setPhoneNumber(req.phoneNumber());
        user.setAddress(req.address());
        user.setDateOfBirth(req.dateOfBirth());
        user.setAvatarUrl(req.avatarUrl());
        return userRepo.save(user);
    }


    @Override
    @Transactional
    public void changePassword(String username, ChangePasswordRequest req) {
        UserAccount user = me(username);
        if (!encoder.matches(req.currentPassword(), user.getPasswordHash())) {
            throw new IllegalArgumentException("Current password incorrect");
        }
        user.setPasswordHash(encoder.encode(req.newPassword()));
        userRepo.save(user);
    }
}
