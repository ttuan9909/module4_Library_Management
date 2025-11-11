package com.example.library.repository;

import com.example.library.entity.BorrowTicket;
import com.example.library.entity.FineTicket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FineTicketRepository extends JpaRepository<FineTicket, Long> {
    Optional<FineTicket> findByBorrowTicket(BorrowTicket ticket);
}
