package com.example.library.service.useraccount;

import com.example.library.dto.request.ChangePasswordRequest;
import com.example.library.dto.request.ProfileUpdateRequest;
import com.example.library.dto.useraccount.UserAccountDTO;
import com.example.library.entity.UserAccount;

import java.util.List;
import java.util.Optional;

public interface IUserAccountService {
    UserAccount me(String username);

    UserAccount updateProfile(String username, ProfileUpdateRequest request);

    void changePassword(String username, ChangePasswordRequest request);


    //This is just a placeholder for future methods related to user account management
    List<UserAccountDTO> getAllUsers();

    UserAccountDTO getUserById(Long id);

    UserAccountDTO createUser(UserAccountDTO dto);

    UserAccountDTO updateUser(Long id, UserAccountDTO dto);

    void deleteUser(Long id);
}

