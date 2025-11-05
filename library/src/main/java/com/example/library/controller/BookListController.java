package com.example.library.controller;

import com.example.library.dto.request.BookListDTO;
import com.example.library.entity.Category;
import com.example.library.repository.CategoryRepository;
import com.example.library.service.IBookListService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/admin/books")
@RequiredArgsConstructor
public class BookListController {

    private final IBookListService bookListService;
    private final CategoryRepository categoryRepository;

    // ========== Hiển thị trang quản lý (HTML) ==========
    @GetMapping
    public String showBookListPage(Model model) {
        model.addAttribute("books", bookListService.getAllBooks());
        model.addAttribute("categories", categoryRepository.findAll());
        model.addAttribute("bookDTO", new BookListDTO());
        return "admin/book/booklist";
    }

    // ========== Lấy danh sách sách (JSON cho Ajax) ==========
    @GetMapping("/list")
    @ResponseBody
    public ResponseEntity<List<BookListDTO>> getAllBooks() {
        return ResponseEntity.ok(bookListService.getAllBooks());
    }

    // ========== Lấy chi tiết 1 sách ==========
    @GetMapping("/{id}")
    @ResponseBody
    public ResponseEntity<?> getBookById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(bookListService.getBookById(id));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Không tìm thấy sách có ID: " + id));
        }
    }

    // ========== Tạo mới sách ==========
    @PostMapping
    @ResponseBody
    public ResponseEntity<?> createBook(@Valid @RequestBody BookListDTO dto, BindingResult result) {
        if (result.hasErrors()) {
            return ResponseEntity.badRequest().body(getErrorMap(result));
        }
        try {
            BookListDTO saved = bookListService.createBook(dto);
            return ResponseEntity.status(HttpStatus.CREATED).body(saved);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Không thể tạo sách: " + e.getMessage()));
        }
    }

    // ========== Cập nhật sách ==========
    @PutMapping("/{id}")
    @ResponseBody
    public ResponseEntity<?> updateBook(@PathVariable Long id,
                                        @Valid @RequestBody BookListDTO dto,
                                        BindingResult result) {
        if (result.hasErrors()) {
            return ResponseEntity.badRequest().body(getErrorMap(result));
        }
        try {
            BookListDTO updated = bookListService.updateBook(id, dto);
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Cập nhật thất bại: " + e.getMessage()));
        }
    }

    // ========== Xóa sách ==========
    @DeleteMapping("/{id}")
    @ResponseBody
    public ResponseEntity<?> deleteBook(@PathVariable Long id) {
        try {
            bookListService.deleteBook(id);
            return ResponseEntity.ok(Map.of("message", "Xóa thành công!"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Không tìm thấy sách để xóa!"));
        }
    }

    // ========== Lấy danh sách Category (cho dropdown) ==========
    @GetMapping("/categories")
    @ResponseBody
    public ResponseEntity<List<Category>> getAllCategories() {
        return ResponseEntity.ok(categoryRepository.findAll());
    }

    // ==================== HELPER: CHUYỂN BindingResult → Map lỗi ====================
    private Map<String, String> getErrorMap(BindingResult result) {
        Map<String, String> errors = new HashMap<>();
        for (FieldError error : result.getFieldErrors()) {
            errors.put(error.getField(), error.getDefaultMessage());
        }
        return errors;
    }
}