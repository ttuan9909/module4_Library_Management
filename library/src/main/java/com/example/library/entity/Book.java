package com.example.library.entity;

import com.example.library.entity.enums.BookStatus;
import jakarta.persistence.*;
import lombok.*;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "book")
@Entity
@Builder
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "book_id")
    private Long bookId;

    @Column(name = "title", nullable = false, length = 255)
    private String title;

    @ManyToOne
    @JoinColumn(name = "category_id", foreignKey = @ForeignKey(name = "fk_book_category"))
    private Category category;

    @Column(name = "publisher", length = 150)
    private String publisher;

    @Column(name = "publish_year")
    private Integer publishYear;

    @Column(name = "language", length = 50)
    private String language;

    @Lob
    @Column(name = "description")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 12)
    private BookStatus status = BookStatus.Available;
    
}
