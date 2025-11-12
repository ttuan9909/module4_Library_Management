package com.example.library.service;

import com.example.library.dto.request.UserAccountDTO;

import java.util.List;

public interface IUserAccountService {
    List<UserAccountDTO> getAllUsers();

    // Phương thức đã dùng cho AJAX View Detail và Edit
    UserAccountDTO getUserById(Long id);

    UserAccountDTO createUser(UserAccountDTO dto);

    UserAccountDTO updateUser(Long id, UserAccountDTO dto);

    void deleteUser(Long id);
}
