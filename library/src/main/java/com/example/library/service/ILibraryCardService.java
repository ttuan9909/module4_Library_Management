package com.example.library.service;

import com.example.library.entity.LibraryCard;

import java.util.Optional;

public interface ILibraryCardService {
    Optional<LibraryCard> findByUserUserId(Long userId);
}
