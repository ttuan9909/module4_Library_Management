package com.example.library.service;

import com.example.library.dto.request.BookListDTO;
import com.example.library.entity.Book;
import com.example.library.entity.Category;
import com.example.library.entity.enums.BookStatus;
import com.example.library.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookListService implements IBookListService {

    private final BookRepository bookRepository;

    // =========================================================
    // PUBLIC METHODS – CRUD
    // =========================================================

    @Override
    public List<BookListDTO> getAllBooks() {
        return bookRepository.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public BookListDTO getBookById(Long id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy sách ID: " + id));
        return toDto(book);
    }

    @Override
    @Transactional
    public BookListDTO createBook(BookListDTO dto) {
        Book book = new Book();
        copyDtoToEntity(dto, book);
        book.setStatus(dto.getStatus() != null ? dto.getStatus() : BookStatus.Available);

        Book saved = bookRepository.save(book);
        log.info("Tạo sách thành công! ID: {}", saved.getBookId());
        return toDto(saved);
    }

    @Override
    @Transactional
    public BookListDTO updateBook(Long id, BookListDTO dto) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy sách ID: " + id));

        copyDtoToEntity(dto, book);
        book.setStatus(dto.getStatus() != null ? dto.getStatus() : book.getStatus());

        Book updated = bookRepository.save(book);
        log.info("Cập nhật sách thành công! ID: {}", id);
        return toDto(updated);
    }

    @Override
    @Transactional
    public void deleteBook(Long id) {
        if (!bookRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy sách ID: " + id);
        }
        bookRepository.deleteById(id);
        log.info("Xóa sách thành công! ID: {}", id);
    }

    @Override
    public List<Category> getAllCategories() {
        return bookRepository.findAll().stream()
                .map(Book::getCategory)
                .filter(cat -> cat != null)
                .distinct()
                .collect(Collectors.toList());
    }

    // =========================================================
    // PRIVATE HELPERS – CHỈ 1 METHOD CHUYỂN ĐỔI
    // =========================================================

    /** Chuyển Entity → DTO (có quantity) */
    private BookListDTO toDto(Book book) {
        return BookListDTO.builder()
                .bookId(book.getBookId())
                .title(book.getTitle())
                .category(book.getCategory())
                .publisher(book.getPublisher())
                .publishYear(book.getPublishYear())
                .language(book.getLanguage())
                .description(book.getDescription())
                .status(book.getStatus())
                .quantity(calculateQuantity(book))
                .build();
    }

    /** Tính số lượng sách còn lại */
    private Integer calculateQuantity(Book book) {
        return 10; // Mặc định 10 cuốn – mở rộng sau
    }

    /** Copy dữ liệu từ DTO → Entity */
    private void copyDtoToEntity(BookListDTO dto, Book book) {
        book.setTitle(dto.getTitle());
        book.setCategory(dto.getCategory());
        book.setPublisher(dto.getPublisher());
        book.setPublishYear(dto.getPublishYear());
        book.setLanguage(dto.getLanguage());
        book.setDescription(dto.getDescription());
    }
}