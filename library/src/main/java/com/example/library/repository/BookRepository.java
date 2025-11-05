package com.example.library.repository;

import com.example.library.entity.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BookRepository extends JpaRepository<Book, Integer> {

    List<Book> findByTitleContainingIgnoreCase(String title);

    @Query("SELECT b FROM Book b WHERE LOWER(b.category.categoryName) LIKE LOWER(CONCAT('%', :categoryName, '%'))")
    List<Book> findByCategoryName(@Param("categoryName") String categoryName);

    @Query("SELECT b FROM Book b WHERE LOWER(b.author.authorName) LIKE LOWER(CONCAT('%', :authorName, '%'))")
    List<Book> findByAuthorName(@Param("authorName") String authorName);

    List<Book> findByPublishYear(Integer publishYear);
}
