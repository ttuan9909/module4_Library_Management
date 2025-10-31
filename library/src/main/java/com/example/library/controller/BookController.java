package com.example.library.controller;

import com.example.library.dto.response.Bookdto;
import com.example.library.repository.BookRepository;
import com.example.library.service.IBookService;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/books")
@RequiredArgsConstructor
public class BookController {
    @Autowired
    IBookService bookService;

    @GetMapping("/{id}")
    public ResponseEntity<?> findById(@PathVariable Long id) {
        if (id == null) {
            return ResponseEntity.status(400).build();
        } else {
            Bookdto book = bookService.findById(id).orElse(null);
            if (book == null) {
                return ResponseEntity.status(404).build();
            } else {
                return ResponseEntity.ok(book);
            }
        }
    }
}
