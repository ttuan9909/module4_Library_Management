package com.example.library.service;

import com.example.library.dto.request.BookListDTO;
import com.example.library.dto.response.Bookdto;
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
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookService implements IBookService {

    private final BookRepository bookRepository;

    // =========================================================
    // GET LIST
    // =========================================================
    @Override
    public List<BookListDTO> getAllBooks() {
        return bookRepository.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    // =========================================================
    // GET BY ID (DTO)
    // =========================================================
    @Override
    public BookListDTO getBookById(Long id) {
        Book book = findBookOrThrow(id);
        return toDto(book);
    }

    // =========================================================
    // GET BY ID (API dành cho public)
    // =========================================================
    @Override
    public Optional<Bookdto> findById(Long id) {
        return bookRepository.findById(id)
                .map(book -> new Bookdto(
                        book.getBookId(),
                        book.getTitle(),
                        book.getCategory(),
                        book.getPublisher(),
                        book.getPublishYear(),
                        book.getLanguage(),
                        book.getDescription(),
                        book.getStatus()
                ));
    }

    // =========================================================
    // CREATE
    // =========================================================
    @Override
    @Transactional
    public BookListDTO createBook(BookListDTO dto) {
        Book book = new Book();
        copyDtoToEntity(dto, book);

        // status mặc định Available
        book.setStatus(dto.getStatus() != null ? dto.getStatus() : BookStatus.Available);

        Book saved = bookRepository.save(book);
        log.info("✅ Tạo sách thành công! ID: {}", saved.getBookId());
        return toDto(saved);
    }

    // =========================================================
    // UPDATE
    // =========================================================
    @Override
    @Transactional
    public BookListDTO updateBook(Long id, BookListDTO dto) {
        Book book = findBookOrThrow(id);
        copyDtoToEntity(dto, book);
        book.setStatus(dto.getStatus() != null ? dto.getStatus() : book.getStatus());

        Book updated = bookRepository.save(book);
        log.info("♻️ Cập nhật sách thành công! ID: {}", id);
        return toDto(updated);
    }

    // =========================================================
    // DELETE
    // =========================================================
    @Override
    @Transactional
    public void deleteBook(Long id) {
        if (!bookRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy sách ID: " + id);
        }
        bookRepository.deleteById(id);
        log.info("🗑 Xóa sách thành công! ID: {}", id);
    }

    // =========================================================
    // GET ALL CATEGORIES (đúng logic)
    // =========================================================
    @Override
    public List<Category> getAllCategories() {
        return bookRepository.findAll().stream()
                .map(Book::getCategory)
                .filter(c -> c != null)
                .distinct()
                .collect(Collectors.toList());
    }

    // =========================================================
    // PRIVATE METHODS
    // =========================================================
    private Book findBookOrThrow(Long id) {
        return bookRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy sách ID: " + id));
    }

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

    private Integer calculateQuantity(Book book) {
        return 10; // default
    }

    private void copyDtoToEntity(BookListDTO dto, Book book) {
        book.setTitle(dto.getTitle());
        book.setCategory(dto.getCategory());
        book.setPublisher(dto.getPublisher());
        book.setPublishYear(dto.getPublishYear());
        book.setLanguage(dto.getLanguage());
        book.setDescription(dto.getDescription());
    }
}
