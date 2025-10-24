package com.example.library.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class BookAuthorId implements Serializable {
    @Column(name = "book_id")
    private Long bookId;

    @Column(name = "author_id")
    private Integer authorId;
}
