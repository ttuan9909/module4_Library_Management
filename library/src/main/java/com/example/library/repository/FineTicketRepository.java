package com.example.library.repository;

import com.example.library.entity.BorrowTicket;
import com.example.library.entity.FineTicket;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FineTicketRepository extends JpaRepository<FineTicket, Integer> {
    Optional<FineTicket> findByBorrowTicket(BorrowTicket ticket);
}
