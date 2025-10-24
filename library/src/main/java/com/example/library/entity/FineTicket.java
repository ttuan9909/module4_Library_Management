package com.example.library.entity;

import com.example.library.entity.enums.FineType;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "fine_ticket")
@Entity
@Builder
public class FineTicket {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "fine_id")
    private Long fineId;

    @OneToOne(optional = false)
    @JoinColumn(name = "borrow_id", nullable = false, unique = true,
            foreignKey = @ForeignKey(name = "fk_fine_borrow"))
    private BorrowTicket borrowTicket;

    @Enumerated(EnumType.STRING)
    @Column(name = "fine_type", nullable = false, length = 16)
    private FineType fineType;

    @Column(name = "fine_amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal fineAmount;

    @Column(name = "description", length = 255)
    private String description;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    void prePersist() {
        if (createdAt == null) createdAt = LocalDateTime.now();
    }
}
