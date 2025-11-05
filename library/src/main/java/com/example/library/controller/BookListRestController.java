package com.example.library.controller;

import com.example.library.dto.request.BookListDTO;
import com.example.library.entity.Category;
import com.example.library.service.IBookListService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/booklist")
@RequiredArgsConstructor
public class BookListRestController {

    private final IBookListService bookListService;

    // =========================================================
    // GET ALL BOOKS
    // =========================================================
    @GetMapping
    public ResponseEntity<List<BookListDTO>> getAllBooks() {
        return ResponseEntity.ok(bookListService.getAllBooks());
    }

    // =========================================================
    // GET BOOK BY ID – DÙNG getBookById() CÔNG KHAI
    // =========================================================
    @GetMapping("/{id}")
    public ResponseEntity<?> getBookById(@PathVariable("id") Long id) {
        try {
            BookListDTO dto = bookListService.getBookById(id);
            return ResponseEntity.ok(dto);
        } catch (org.springframework.web.server.ResponseStatusException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Không tìm thấy sách ID: " + id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }
    }

    // =========================================================
    // CREATE BOOK
    // =========================================================
    @PostMapping
    public ResponseEntity<BookListDTO> createBook(@Valid @RequestBody BookListDTO dto) {
        BookListDTO saved = bookListService.createBook(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    // =========================================================
    // UPDATE BOOK
    // =========================================================
    @PutMapping("/{id}")
    public ResponseEntity<BookListDTO> updateBook(@PathVariable("id") Long id, @Valid @RequestBody BookListDTO dto) {
        BookListDTO updated = bookListService.updateBook(id, dto);
        return ResponseEntity.ok(updated);
    }

    // =========================================================
    // DELETE BOOK
    // =========================================================
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteBook(@PathVariable("id") Long id) {
        bookListService.deleteBook(id);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Xóa sách thành công!");
        return ResponseEntity.ok(response);
    }

    // =========================================================
    // GET ALL CATEGORIES
    // =========================================================
    @GetMapping("/categories")
    public ResponseEntity<List<Category>> getAllCategories() {
        return ResponseEntity.ok(bookListService.getAllCategories());
    }
}