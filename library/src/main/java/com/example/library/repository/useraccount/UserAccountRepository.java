package com.example.library.repository.useraccount;

import com.example.library.entity.UserAccount;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserAccountRepository extends JpaRepository<UserAccount, Long> {
    Optional<UserAccount> findByUsername(String username);
    boolean existsByEmail(String email);
    boolean existsByPhoneNumber(String phoneNumber);
    boolean existsByUsername(String username);
    Optional<UserAccount> findByEmail(String email);
    Optional<UserAccount> findByPhoneNumber(String phoneNumber);
}


