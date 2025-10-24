package com.example.library.entity;

import com.example.library.entity.enums.PublicationStatus;
import jakarta.persistence.*;
import lombok.*;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(
        name = "publication",
        uniqueConstraints = @UniqueConstraint(name = "uk_publication_barcode", columnNames = "barcode")
)
@Entity
@Builder
public class Publication {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "publication_id")
    private Long publicationId;

    @ManyToOne(optional = false)
    @JoinColumn(name = "book_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_publication_book"))
    private Book book;

    @Column(name = "barcode", nullable = false, length = 64)
    private String barcode;

    @Column(name = "shelf_location", nullable = false, length = 100)
    private String shelfLocation;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 12)
    private PublicationStatus status = PublicationStatus.Available;

    @Column(name = "notes", length = 255)
    private String notes;
}
