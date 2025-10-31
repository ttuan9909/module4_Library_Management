package com.example.library.service;

import com.example.library.dto.response.Bookdto;
import com.example.library.entity.Book;
import com.example.library.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class BookService implements IBookService{
    @Autowired
    BookRepository bookRepository;

    @Override
    public Optional<Bookdto> findById(Long id) {
        Book book = bookRepository.findById(id).orElse(null);
        Bookdto bookdto = new Bookdto(
                book.getBookId(),
                book.getTitle(),
                book.getCategory(),
                book.getPublisher(),
                book.getPublishYear(),
                book.getLanguage(),
                book.getDescription(),
                book.getStatus());
        return Optional.of(bookdto);
    }
}
