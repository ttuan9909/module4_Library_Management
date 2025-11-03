package com.example.library.repository;

import com.example.library.entity.LibraryCard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LibraryCardRepository extends JpaRepository<LibraryCard, Long> {

    /**
     * Tìm kiếm thẻ thư viện bằng số thẻ (Card Number).
     * @param cardNumber Số thẻ cần tìm
     * @return Optional<LibraryCard>
     */
    Optional<LibraryCard> findByCardNumber(String cardNumber);

    /**
     * Kiểm tra sự tồn tại của thẻ thư viện bằng số thẻ.
     * @param cardNumber Số thẻ cần kiểm tra
     * @return true nếu tồn tại
     */
    boolean existsByCardNumber(String cardNumber);

    /**
     * Tìm thẻ thư viện liên kết với một UserAccount cụ thể bằng userId.
     * Do mối quan hệ OneToOne, chỉ trả về một Optional.
     * @param userId ID của người dùng
     * @return Optional<LibraryCard>
     */
    Optional<LibraryCard> findByUser_UserId(Long userId);

    /**
     * Xóa thẻ thư viện bằng userId.
     * @param userId ID của người dùng
     */
    void deleteByUser_UserId(Long userId);

}