package com.example.library.service;

import com.example.library.dto.request.BookListDTO;
import com.example.library.dto.response.Bookdto;
import com.example.library.entity.Book;
import com.example.library.entity.Category;
import com.example.library.entity.Publication;
import com.example.library.entity.enums.BookStatus;
import com.example.library.repository.BookRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class BookService implements IBookService{
    @Autowired
    BookRepository bookRepository;

    @Autowired
    IPublicationService publicationService;

    @Override
    public List<BookListDTO> getAllBooks() {
        return bookRepository.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public BookListDTO getBookById(Long id) {
        Book book = findBookOrThrow(id);
        return toDto(book);
    }

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

    @Override
    @Transactional
    public void deleteBook(Long id) {
        if (!bookRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy sách ID: " + id);
        }
        bookRepository.deleteById(id);
        log.info("🗑 Xóa sách thành công! ID: {}", id);
    }

    @Override
    public List<Category> getAllCategories() {
        return bookRepository.findAll().stream()
                .map(Book::getCategory)
                .filter(c -> c != null)
                .distinct()
                .collect(Collectors.toList());
    }

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
                book.getStatus(),
                book.getCoverImage());
        return Optional.of(bookdto);
    }

    @Override
    public BookListDTO getBookByPublicationBarcode(String barcode) {
        Publication publication = publicationService.findEntityByBarcode(barcode) // <--- GỌI HÀM MỚI
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy ấn phẩm với barcode: " + barcode));

        Book book = publication.getBook();

        return toDto(book);
    }

    private Book findBookOrThrow(Long id) {
        return bookRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy sách ID: " + id));
    }

    private BookListDTO toDto(Book book) {
        return BookListDTO.builder()
                .bookId(book.getBookId())
                .title(book.getTitle())
                .category(book.getCategory())
                .categoryId(book.getCategory() != null ? book.getCategory().getCategoryId() : null)
                .publisher(book.getPublisher())
                .publishYear(book.getPublishYear())
                .language(book.getLanguage())
                .description(book.getDescription())
                .status(book.getStatus())
                .coverImage(book.getCoverImage())
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
        book.setCoverImage(dto.getCoverImage());
    }
}
