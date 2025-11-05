package com.example.library.controller;

import com.example.library.dto.request.BookListDTO;
import com.example.library.entity.Book;
import com.example.library.repository.BookRepository;
import com.example.library.service.IBookListService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.server.ResponseStatusException;

/**
 * Controller điều hướng VIEW
 * → Danh sách: /admin/books
 * → Chi tiết: /admin/books/{id}/detail → TRẢ VỀ BookListDTO
 */
@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class BookListController {

    private final IBookListService bookListService;
    private final BookRepository bookRepository;

    // DANH SÁCH SÁCH
    @GetMapping("/books")
    public String showBookListPage(Model model) {
        model.addAttribute("books", bookListService.getAllBooks()); // List<BookListDTO>
        return "admin/book/booklist";
    }

    @GetMapping("/books/{id}/detail")
public String showBookDetail(@PathVariable("id") Long id, Model model) {
    Book book = bookRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy sách"));

    BookListDTO dto = bookListService.getBookById(id); // DÙNG METHOD CÔNG KHAI
    // HOẶC: BookListDTO dto = bookListService.toDto(book); → nhưng toDto() là private

    model.addAttribute("book", dto);
    return "admin/book/book-detail";
}
}