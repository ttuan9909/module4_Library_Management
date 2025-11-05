package com.example.library.repository;

import com.example.library.entity.LibraryCard;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LibraryCardRepository extends JpaRepository<LibraryCard, Long> {
    boolean existsByCardNumber(String cardNumber);
    Optional<LibraryCard> findByUser_UserId(Long userId);
}
