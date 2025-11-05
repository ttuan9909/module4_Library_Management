package com.example.library.repository;

import com.example.library.entity.BorrowTicket;
import com.example.library.entity.enums.BorrowStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BorrowTicketRepository extends JpaRepository<BorrowTicket, Long> {
    List<BorrowTicket> findByCard_CardIdAndStatusIn(Long cardId, List<BorrowStatus> statuses);
}
