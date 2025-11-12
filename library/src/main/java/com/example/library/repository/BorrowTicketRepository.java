package com.example.library.repository;

import com.example.library.dto.response.BorrowInformation;
import com.example.library.entity.BorrowTicket;
import com.example.library.entity.enums.BorrowStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BorrowTicketRepository extends JpaRepository<BorrowTicket, Long> {
    List<BorrowTicket> findByCard_CardIdAndStatusIn(Long cardId, List<BorrowStatus> statuses);
    @Query("SELECT new com.example.library.dto.response.BorrowInformation(" +
            "b.borrowId, " +
            "u.userId, " +
            "u.fullName, " +
            "bk.title, " +
            "b.borrowDate, " +
            "b.dueDate, " +
            "b.fineAmount, " +
            "b.status, " +
            "p.barcode, " +
            "p.publicationId) " +  // <-- thêm đây
            "FROM BorrowTicket b " +
            "JOIN b.publication p " +
            "JOIN b.card l " +
            "JOIN l.user u " +
            "JOIN p.book bk " +
            "WHERE b.status = com.example.library.entity.enums.BorrowStatus.Borrowing " +
            "AND p.barcode = :barcode")
    BorrowInformation findBorrowInformationByBarcode(@Param("barcode") String barcode);
    Optional<BorrowTicket> findByPublicationPublicationIdAndCardUserUserId(Long publicationId, Long userId);
}
