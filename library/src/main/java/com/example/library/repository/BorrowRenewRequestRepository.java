package com.example.library.repository;

import com.example.library.entity.BorrowRenewRequest;
import com.example.library.entity.enums.RenewRequestStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BorrowRenewRequestRepository extends JpaRepository<BorrowRenewRequest, Long> {
    boolean existsByBorrowTicket_BorrowIdAndStatus(Long borrowId, RenewRequestStatus status);
    List<BorrowRenewRequest> findByStatus(RenewRequestStatus status);
}
