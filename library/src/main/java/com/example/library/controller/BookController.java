package com.example.library.controller;

import com.example.library.dto.response.Bookdto;
import com.example.library.entity.Book;
import com.example.library.repository.BookRepository;
import com.example.library.service.IBookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;

@RequestMapping("/book")
@Controller
public class BookController {
    @Autowired
    BookRepository bookRepository;
    @Autowired
    IBookService bookService;

    @GetMapping("")
    public String showBooks(@RequestParam(value = "q", required = false) String keyword,
                            @RequestParam(value = "type", required = false) String type,
                            Model model) {

        List<Book> books = List.of();

        if (keyword == null || keyword.isEmpty()) {
            books = bookRepository.findAll();
        } else {
            // có keyword
            if (type == null || type.isEmpty()) {
                books = bookRepository.findByTitleContainingIgnoreCase(keyword);
            } else {
                switch (type) {
                    case "category":
                        books = bookRepository.findByCategoryName(keyword);
                        break;
                    case "author":
                        books = bookRepository.findByAuthorName(keyword);
                        break;
                    case "year":
                        try {
                            int year = Integer.parseInt(keyword);
                            books = bookRepository.findByPublishYear(year);
                        } catch (NumberFormatException e) {
                            books = List.of();
                        }
                        break;
                    default:
                        books = bookRepository.findByTitleContainingIgnoreCase(keyword);
                        break;
                }
            }
        }

        model.addAttribute("books", books);
        model.addAttribute("keyword", keyword);
        model.addAttribute("type", type);

        return "books-media-list-view";
    }
    @GetMapping("/{id}")
    public String findById(@PathVariable Long id, Model model, RedirectAttributes redirectAttrs) {
        if (id == null) {
            redirectAttrs.addFlashAttribute("error", "Invalid book id.");
            return "redirect:/books"; // chuyển về list page
        }

        Bookdto book = bookService.findById(id).orElse(null);
        if (book == null) {
            redirectAttrs.addFlashAttribute("error", "Book not found.");
            return "redirect:/books";
        }

        model.addAttribute("book", book);
        // trả về template thymeleaf: src/main/resources/templates/books/detail.html
        return "books/detail";
    }
}
