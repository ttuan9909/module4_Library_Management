package com.example.library.repository;

import com.example.library.entity.UserAccount;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query; // 👈 NHỚ IMPORT
import java.util.List; // 👈 NHỚ IMPORT
import java.util.Optional;

public interface UserAccountRepository extends JpaRepository<UserAccount, Long> {

    // ⭐️ BẢN SỬA CHỮA: Phương thức buộc nạp Role để tránh LazyInitializationException
    @Query("SELECT u FROM UserAccount u LEFT JOIN FETCH u.role")
    List<UserAccount> findAllWithRole(); 
    
    // Các phương thức cũ giữ nguyên
    Optional<UserAccount> findByUsername(String username);
    boolean existsByEmail(String email);
    boolean existsByPhoneNumber(String phoneNumber);
    boolean existsByUsername(String username);
    UserAccount findByUserIdOrPhoneNumberOrEmail(Long userId, String phoneNumber, String email);

    Optional<Object> findByEmail(@Email(message = "Email không hợp lệ") String email);

    Optional<Object> findByPhoneNumber(@Pattern(regexp = "^\\d{10,11}$", message = "Số điện thoại phải có 10–11 chữ số") String phoneNumber);
    
}