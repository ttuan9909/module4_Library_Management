package com.example.library.entity;

import com.example.library.entity.enums.RenewRequestStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "borrow_renew_request")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BorrowRenewRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "request_id")
    private Long requestId;

    @ManyToOne(optional = false)
    @JoinColumn(name = "borrow_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_rr_borrow"))
    private BorrowTicket borrowTicket;

    @ManyToOne(optional = false)
    @JoinColumn(name = "requested_by_card_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_rr_card"))
    private LibraryCard requestedBy;   // người mượn

    @Column(name = "requested_at", nullable = false)
    private LocalDateTime requestedAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 16)
    private RenewRequestStatus status;

    @ManyToOne
    @JoinColumn(name = "approved_by_employee_id",
            foreignKey = @ForeignKey(name = "fk_rr_employee"))
    private Employee approvedBy;

    @Column(name = "approved_at")
    private LocalDateTime approvedAt;

    @Column(name = "proposed_due_date", nullable = false)
    private LocalDate proposedDueDate;

    @Column(name = "notes", length = 255)
    private String notes;

    @PrePersist
    void prePersist() {
        if (requestedAt == null) requestedAt = LocalDateTime.now();
        if (status == null) status = RenewRequestStatus.PENDING;
    }
}
