package com.example.library.repository;

import com.example.library.entity.Author;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AuthorRepository extends JpaRepository<Author, Integer> {
    Optional<Author> findByAuthorName(String authorName);
}
