package com.example.library.entity;

import com.example.library.entity.enums.BorrowStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(
        name = "borrow_ticket",
        uniqueConstraints = @UniqueConstraint(name = "uq_bt_ticket_pub", columnNames = {"ticket_number","publication_id"}),
        indexes = {
                @Index(name = "idx_bt_ticket_number", columnList = "ticket_number"),
                @Index(name = "idx_bt_card", columnList = "card_id"),
                @Index(name = "idx_bt_publication", columnList = "publication_id")
        }
)
@Entity
@Builder
public class BorrowTicket {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "borrow_id")
    private Long borrowId;

    @Column(name = "ticket_number", nullable = false, length = 30)
    private String ticketNumber;

    @ManyToOne(optional = false)
    @JoinColumn(name = "card_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_bt_card"))
    private LibraryCard card;

    @ManyToOne
    @JoinColumn(name = "employee_id",
            foreignKey = @ForeignKey(name = "fk_bt_employee"))
    private Employee staff;

    @ManyToOne(optional = false)
    @JoinColumn(name = "publication_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_bt_publication"))
    private Publication publication;

    @Column(name = "borrow_date", nullable = false)
    private LocalDateTime borrowDate;

    @Column(name = "due_date", nullable = false)
    private LocalDate dueDate;

    @Column(name = "return_date")
    private LocalDate returnDate;

    @Column(name = "renewal_count", nullable = false)
    private Integer renewalCount;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 16)
    private BorrowStatus status;

    @Column(name = "fine_amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal fineAmount;

    @Column(name = "notes", length = 255)
    private String notes;

    @PrePersist
    void prePersist() {
        if (borrowDate == null) borrowDate = LocalDateTime.now();
        if (renewalCount == null) renewalCount = 0;
        if (status == null) status = BorrowStatus.Borrowing;
        if (fineAmount == null) fineAmount = BigDecimal.ZERO;
    }
}
