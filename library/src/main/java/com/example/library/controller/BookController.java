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
        List<Book> books = new ArrayList<>();

        if (keyword == null || keyword.isEmpty()) {
            books = bookRepository.findAll();
            switch (type) {
                case "category":
                    books = bookRepository.findByCategoryName(keyword);
                    break;
                case "author":
                    books = bookRepository.findByAuthorName(keyword);
                    break;
                case "year":
                    try {
                        books = bookRepository.findByPublishYear(Integer.parseInt(keyword));
                    } catch (NumberFormatException e) {
                        books = List.of();
                    }
                    break;
                default:
                    books = bookRepository.findByTitleContainingIgnoreCase(keyword);
                    }
                    }

        model.addAttribute("books", books);
        model.addAttribute("keyword", keyword);
        model.addAttribute("type", type);
        return "books-media-list-view";
}
//    @GetMapping("/{id}")
//    public ResponseEntity<?> findById(@PathVariable Long id) {
//        if (id == null) {
//            return ResponseEntity.status(400).build();
//        } else {
//            Bookdto book = bookService.findById(id).orElse(null);
//            if (book == null) {
//                return ResponseEntity.status(404).build();
//            } else {
//                return ResponseEntity.ok(book);
//            }
//        }
//    }
}
