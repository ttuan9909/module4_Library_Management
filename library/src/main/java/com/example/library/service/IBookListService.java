package com.example.library.service;

import com.example.library.dto.request.BookListDTO;
import jakarta.validation.Valid;

import java.util.List;

public interface IBookListService {

    List<BookListDTO> getAllBooks();

    BookListDTO createBook(@Valid BookListDTO dto);

    BookListDTO updateBook(Long id, @Valid BookListDTO dto);

    void deleteBook(Long id);

    BookListDTO getBookById(Long id);

}