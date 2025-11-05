package com.example.library.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "author")
@Entity
@Builder
public class Author {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "author_id")
    private Integer authorId;

    @Column(name = "author_name", nullable = false, unique = true, length = 150)
    private String authorName;

    @Column(name = "birth_date")
    private LocalDate birthDate;

    @Column(name = "nationality", length = 100)
    private String nationality;

    @Lob
    @Column(name = "biography")
    private String biography;
    @OneToMany(mappedBy = "author", cascade = CascadeType.ALL)
    private Set<Book> books = new HashSet<>();

}
