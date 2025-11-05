package com.example.library.service;

import com.example.library.dto.request.BookListDTO;
import com.example.library.entity.Category;

import java.util.List;

public interface IBookListService {

    List<BookListDTO> getAllBooks();

    BookListDTO getBookById(Long id);

    BookListDTO createBook(BookListDTO dto);

    BookListDTO updateBook(Long id, BookListDTO dto);

    void deleteBook(Long id);

    List<Category> getAllCategories();

}