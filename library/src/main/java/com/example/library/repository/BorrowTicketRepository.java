package com.example.library.repository;

import com.example.library.entity.BorrowTicket;
import com.example.library.entity.enums.BorrowStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BorrowTicketRepository extends JpaRepository<BorrowTicket, Long> {
    List<BorrowTicket> findByCard_CardIdAndStatusIn(Long cardId, List<BorrowStatus> statuses);
}
