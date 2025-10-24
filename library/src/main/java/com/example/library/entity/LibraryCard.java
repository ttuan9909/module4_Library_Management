package com.example.library.entity;

import com.example.library.entity.enums.LibraryCardStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(
        name = "library_card",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_card_number", columnNames = "card_number"),
                @UniqueConstraint(name = "uk_card_user", columnNames = "user_id")
        }
)
@Entity
@Builder
public class LibraryCard {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "card_id")
    private Long cardId;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true,
            foreignKey = @ForeignKey(name = "fk_card_user"))
    private UserAccount user;

    @Column(name = "card_number", nullable = false, length = 30)
    private String cardNumber;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 10)
    private LibraryCardStatus status = LibraryCardStatus.ACTIVE;

    @Column(name = "notes", length = 255)
    private String notes;
}
