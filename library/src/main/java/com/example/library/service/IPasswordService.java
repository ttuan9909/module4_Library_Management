package com.example.library.service;

public interface IPasswordService {
    String requestReset(String usernameOrEmail);
    void reset(String token, String newPassword);
}
