package com.example.library.entity;

import com.example.library.entity.enums.AuthorRoleType;
import jakarta.persistence.*;
import lombok.*;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "book_author")
@Entity
@Builder
public class BookAuthor {
    @EmbeddedId
    private BookAuthorId id;

    @ManyToOne(optional = false)
    @MapsId("bookId")
    @JoinColumn(name = "book_id",
            foreignKey = @ForeignKey(name = "fk_ba_book"))
    private Book book;

    @ManyToOne(optional = false)
    @MapsId("authorId")
    @JoinColumn(name = "author_id",
            foreignKey = @ForeignKey(name = "fk_ba_author"))
    private Author author;

    @Enumerated(EnumType.STRING)
    @Column(name = "role_type", length = 12)
    private AuthorRoleType roleType = AuthorRoleType.Author;
}
