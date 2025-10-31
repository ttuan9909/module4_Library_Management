package com.example.library.service;

import com.example.library.dto.response.Bookdto;

import java.util.Optional;

public interface IBookService {
    Optional<Bookdto> findById(Long id);
}
