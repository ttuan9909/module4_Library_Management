package com.example.library.service;

import com.example.library.dto.request.ReturnBookRequest;
import com.example.library.entity.BorrowTicket;
import com.example.library.entity.FineTicket;
import com.example.library.entity.enums.BorrowStatus;
import com.example.library.entity.enums.FineType;
import com.example.library.repository.BorrowTicketRepository;
import com.example.library.repository.FineTicketRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;

@Service
@RequiredArgsConstructor
public class ReturnService implements IReturnService {
    private final BorrowTicketRepository borrowRepo;
    private final FineTicketRepository fineRepo;


    @Override
    @Transactional
    public void returnBooks(ReturnBookRequest request) {
        System.out.println("=== [ReturnService] Start returnBooks ===");

        for (ReturnBookRequest.UserReturn user : request.getUsers()) {
            Long userId = user.getUserId();
            System.out.println(">> Processing userId = " + userId);

            for (ReturnBookRequest.BookReturn book : user.getBooks()) {
                System.out.println("---- Book: publicationId = " + book.getPublicationId()
                        + ", returnDate = " + book.getReturnDate()
                        + ", fineAmount = " + book.getFineAmount()
                        + ", status = " + book.getStatus());

                try {
                    BorrowTicket ticket = borrowRepo
                            .findByPublicationPublicationIdAndCardUserUserId(book.getPublicationId(), userId)
                            .orElseThrow(() -> new IllegalArgumentException(
                                    "No borrow record found for publicationId " + book.getPublicationId()
                                            + " and userId " + userId));

                    LocalDate returnDate;
                    try {
                        returnDate = (book.getReturnDate() != null && !book.getReturnDate().isBlank())
                                ? LocalDate.parse(book.getReturnDate())
                                : LocalDate.now();
                    } catch (DateTimeParseException e) {
                        throw new IllegalArgumentException(
                                "Invalid returnDate format for publicationId " + book.getPublicationId());
                    }
                    ticket.setReturnDate(returnDate);

                    if (book.getStatus() != null) {
                        ticket.setStatus(book.getStatus());
                    } else {
                        ticket.setStatus(BorrowStatus.Returned);
                    }

                    if (book.getFineAmount() != null) {
                        ticket.setFineAmount(book.getFineAmount());
                    }

                    borrowRepo.save(ticket);

                    if (book.getFineAmount() != null && book.getFineAmount().compareTo(BigDecimal.ZERO) > 0) {

                        FineType fineType;
                        if (ticket.getStatus() == BorrowStatus.LostOrDamaged) {
                            fineType = FineType.LostOrDamaged;
                        } else if (ticket.getStatus() == BorrowStatus.Overdue) {
                            fineType = FineType.Overdue;
                        } else {
                            fineType = FineType.Other;
                        }

                        FineTicket fine = fineRepo.findByBorrowTicket(ticket)
                                .orElse(FineTicket.builder().borrowTicket(ticket).build());
                        fine.setFineAmount(book.getFineAmount());
                        fine.setDescription("Auto calculated fine");
                        fine.setFineType(fineType);
                        fine.setCreatedAt(LocalDateTime.now());

                        fineRepo.save(fine);
                    }

                    System.out.println("Book processed successfully: publicationId = " + book.getPublicationId());

                } catch (Exception e) {
                    System.err.println("Error processing publicationId=" + book.getPublicationId() + ": " + e.getMessage());
                    throw e;
                }
            }
        }

        System.out.println("=== [ReturnService] End returnBooks ===");
    }
}
