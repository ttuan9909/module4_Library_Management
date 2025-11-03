package com.example.library.service;

import com.example.library.dto.request.ChangePasswordRequest;
import com.example.library.dto.request.ProfileUpdateRequest;
import com.example.library.dto.request.UserAccountDTO;
import com.example.library.entity.UserAccount;

import java.util.List;

public interface IUserAccountService {

    //USER PROFILE METHODS
    UserAccount me(String username);

    UserAccount updateProfile(String username, ProfileUpdateRequest request);

    void changePassword(String username, ChangePasswordRequest request);


    //ADMIN USER MANAGEMENT METHODS
    List<UserAccountDTO> getAllUsers();

    UserAccountDTO getUserById(Long id);

    UserAccountDTO createUser(UserAccountDTO dto);

    UserAccountDTO updateUser(Long id, UserAccountDTO dto);

    void deleteUser(Long id);
    // UserAccount findByUserIdOrPhoneNumberOrEmail(Long userId, String phoneNumber, String email);
}

