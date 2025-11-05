package com.example.library.repository;

import com.example.library.entity.BorrowRenewRequest;
import com.example.library.entity.enums.RenewRequestStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BorrowRenewRequestRepository extends JpaRepository<BorrowRenewRequest, Long> {
    boolean existsByBorrowTicket_BorrowIdAndStatus(Long borrowId, RenewRequestStatus status);
    List<BorrowRenewRequest> findByStatus(RenewRequestStatus status);
}
