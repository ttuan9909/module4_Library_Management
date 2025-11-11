package com.example.library.controller;

import com.example.library.dto.request.BookListDTO;
import com.example.library.entity.Book;
import com.example.library.repository.BookRepository;
import com.example.library.service.IBookService; // ĐÃ SỬA: Thay IBookListService bằng IBookService
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.server.ResponseStatusException;

/**
 * Controller điều hướng VIEW cho Admin
 * → Danh sách: /admin/books
 * → Chi tiết: /admin/books/{id}/detail → TRẢ VỀ BookListDTO
 */
@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class BookController {

    // ĐÃ SỬA: Thay đổi kiểu interface từ IBookListService sang IBookService
    private final IBookService bookService; 
    private final BookRepository bookRepository; // Lưu ý: Nên dùng BookService để truy cập repo

    // DANH SÁCH SÁCH
    /**
     * Thay đổi view trả về từ 'admin/book/booklist' thành 'admin/book/book'
     * để sử dụng trang danh sách sách mới đã gộp.
     */
    @GetMapping("/books")
    public String showBookListPage(Model model) {
        // Model không cần thiết nếu dùng AJAX/jQuery, nhưng vẫn giữ convention
        return "admin/book/book"; // Điều hướng đến view mới
    }

    // CHI TIẾT SÁCH
    @GetMapping("/books/{id}/detail")
    public String showBookDetail(@PathVariable("id") Long id, Model model) {
        
        // **LƯU Ý:** Để nhất quán và tách biệt trách nhiệm, 
        // nên sử dụng bookService để tìm sách thay vì bookRepository trực tiếp.
        BookListDTO dto = bookService.getBookById(id);

        model.addAttribute("book", dto);
        return "admin/book/book-detail";
    }
}