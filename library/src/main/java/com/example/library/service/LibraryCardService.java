package com.example.library.service;

import com.example.library.entity.LibraryCard;
import com.example.library.repository.LibraryCardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class LibraryCardService implements ILibraryCardService {
    @Autowired
    LibraryCardRepository libraryCardRepository;

    @Override
    public Optional<LibraryCard> findByUserUserId(Long userId) {
        return libraryCardRepository.findByUserUserId(userId);
    }
}
