package com.example.library.controller;

import com.example.library.dto.request.BookListDTO;
import com.example.library.repository.BookRepository;
import com.example.library.service.IBookService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class BookAdminController {
    private final IBookService bookService;
    private final BookRepository bookRepository; // Lưu ý: Nên dùng BookService để truy cập repo


    @GetMapping("/books")
    public String showBookListPage(Model model) {
        // Model không cần thiết nếu dùng AJAX/jQuery, nhưng vẫn giữ convention
        return "admin/book/book"; // Điều hướng đến view mới
    }

    // CHI TIẾT SÁCH
    @GetMapping("/books/{id}/detail")
    public String showBookDetail(@PathVariable("id") Long id, Model model) {

        BookListDTO dto = bookService.getBookById(id);

        model.addAttribute("book", dto);
        return "admin/book/book-detail";
    }
}
