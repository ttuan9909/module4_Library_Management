package com.example.library.service;

import com.example.library.dto.request.BookListDTO;
import com.example.library.dto.response.Bookdto;
import com.example.library.entity.Category;

import java.util.List;
import java.util.Optional;

public interface IBookService {

    // Lấy danh sách sách (dùng cho admin CRUD)
    List<BookListDTO> getAllBooks();

    // Lấy sách chi tiết theo ID (dùng cho admin edit/detail)
    BookListDTO getBookById(Long id);

    // Tạo mới sách
    BookListDTO createBook(BookListDTO dto);

    // Cập nhật sách
    BookListDTO updateBook(Long id, BookListDTO dto);

    // Xóa sách
    void deleteBook(Long id);

    // Lấy tất cả thể loại (dùng cho dropdown)
    List<Category> getAllCategories();

    // Lấy chi tiết sách cho API công khai (nếu có frontend riêng)
    Optional<Bookdto> findById(Long id);
}