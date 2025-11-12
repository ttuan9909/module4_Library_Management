package com.example.library.controller;

import com.example.library.dto.request.BookListDTO;
import com.example.library.entity.Category;
import com.example.library.service.IBookService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/books")
@RequiredArgsConstructor
public class BookRestController {
    private final IBookService bookService;

    @GetMapping
    public ResponseEntity<List<BookListDTO>> getAllBooks() {
        return ResponseEntity.ok(bookService.getAllBooks());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getBookById(@PathVariable("id") Long id) {
        try {
            return ResponseEntity.ok(bookService.getBookById(id));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Không tìm thấy sách ID: " + id));
        }
    }

    @PostMapping
    public ResponseEntity<?> createBook(@Valid @RequestBody BookListDTO dto) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(bookService.createBook(dto));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateBook(@PathVariable("id") Long id, @Valid @RequestBody BookListDTO dto) {
        try {
            return ResponseEntity.ok(bookService.updateBook(id, dto));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteBook(@PathVariable("id") Long id) {
        bookService.deleteBook(id);
        return ResponseEntity.ok(Map.of("message", "Xóa sách thành công!"));
    }

    @GetMapping("/categories")
    public ResponseEntity<List<Category>> getAllCategories() {
        return ResponseEntity.ok(bookService.getAllCategories());
    }

    @GetMapping("/by-publication-barcode/{barcode}")
    public BookListDTO getBookByPublicationBarcode(@PathVariable String barcode) {
        return bookService.getBookByPublicationBarcode(barcode);
    }
}
