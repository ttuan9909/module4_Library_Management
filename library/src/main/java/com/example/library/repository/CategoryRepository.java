package com.example.library.repository;

import com.example.library.entity.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Integer> {
    Page<Category> findByCategoryNameContaining(String categoryName, Pageable pageable);
    boolean existsByCategoryNameContaining(String categoryName);
}
