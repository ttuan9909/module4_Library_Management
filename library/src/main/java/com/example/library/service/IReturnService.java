package com.example.library.service;

import com.example.library.dto.request.ReturnBookRequest;

public interface IReturnService {
    void returnBooks(ReturnBookRequest request);
}
