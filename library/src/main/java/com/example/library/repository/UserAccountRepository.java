package com.example.library.repository;

import com.example.library.entity.UserAccount;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserAccountRepository extends JpaRepository<UserAccount, Long> {
    Optional<UserAccount> findByUsername(String username);
    boolean existsByEmail(String email);
    boolean existsByPhoneNumber(String phoneNumber);
    boolean existsByUsername(String username);
    UserAccount findByUserIdOrPhoneNumberOrEmail(Long userId, String phoneNumber, String email);

    Optional<Object> findByEmail(@Email(message = "Email không hợp lệ") String email);

    Optional<Object> findByPhoneNumber(@Pattern(regexp = "^\\d{10,11}$", message = "Số điện thoại phải có 10–11 chữ số") String phoneNumber);
}
