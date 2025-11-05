package com.example.library.controller;

import com.example.library.entity.Book;
import com.example.library.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@RequestMapping("/book")
@Controller
public class BookController {
    @Autowired
    BookRepository bookRepository;
    @GetMapping("")
    public String showBooks(@RequestParam(value = "q", required = false) String keyword,
                            @RequestParam(value = "type", required = false) String type,
                            Model model) {
        List<Book> books;

        if (keyword == null || keyword.isEmpty()) {
            books = bookRepository.findAll();
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
}
