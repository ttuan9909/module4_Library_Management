package com.example.library.repository;

import com.example.library.entity.LibraryCard;
import com.example.library.entity.UserAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LibraryCardRepository extends JpaRepository<LibraryCard, Long> {
    boolean existsByCardNumber(String cardNumber);
    Optional<LibraryCard> findByUser_UserId(Long userId);
    Optional<LibraryCard> findByUser(UserAccount user);
}
