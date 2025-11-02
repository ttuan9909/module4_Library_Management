package com.example.library.repository;

import com.example.library.entity.BorrowTicket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BorrowTicketRepository extends JpaRepository<BorrowTicket, Long> {
}
