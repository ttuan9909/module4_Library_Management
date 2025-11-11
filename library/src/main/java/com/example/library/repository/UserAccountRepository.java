package com.example.library.repository;

import com.example.library.entity.UserAccount;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository cho entity {@link UserAccount}.
 * Cung cấp các phương thức CRUD và truy vấn tùy chỉnh.
 */
@Repository
public interface UserAccountRepository extends JpaRepository<UserAccount, Long> {

    /**
     * Tìm tất cả UserAccount cùng với Role (tránh LazyInitializationException).
     * Sử dụng @EntityGraph thay vì @Query để tối ưu và sạch hơn.
     */
    @EntityGraph(attributePaths = "role")
    List<UserAccount> findAll();

    /**
     * Tìm UserAccount theo username.
     */
    Optional<UserAccount> findByUsername(String username);

    /**
     * Kiểm tra email đã tồn tại chưa.
     */
    boolean existsByEmail(String email);

    /**
     * Kiểm tra số điện thoại đã tồn tại chưa.
     */
    boolean existsByPhoneNumber(String phoneNumber);

    /**
     * Kiểm tra username đã tồn tại chưa.
     */
    boolean existsByUsername(String username);

    /**
     * Tìm UserAccount theo userId, phoneNumber hoặc email (dùng cho tìm kiếm linh hoạt).
     */
    UserAccount findByUserIdOrPhoneNumberOrEmail(Long userId, String phoneNumber, String email);

    /**
     * Tìm UserAccount theo email.
     */
    Optional<UserAccount> findByEmail(String email);

    /**
     * Tìm UserAccount theo số điện thoại.
     */
    Optional<UserAccount> findByPhoneNumber(String phoneNumber);
}