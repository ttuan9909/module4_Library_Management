package com.example.library.service;

import com.example.library.dto.request.*;
import com.example.library.entity.*;
import com.example.library.entity.enums.BookStatus;
import com.example.library.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.server.ResponseStatusException;

import java.lang.reflect.Field;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookListService implements IBookListService {

    private final BookRepository bookRepository;
    private final CategoryRepository categoryRepository;
    private final AuthorRepository authorRepository;

    @Override
    public List<BookListDTO> getAllBooks() {
        return bookRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public BookListDTO createBook(BookListDTO dto) {
        try {
            Category category = getOrCreateCategory(dto.getCategory().getCategoryName());
            Author author = getOrCreateAuthor(dto.getAuthor());

            Book book = new Book();
            BeanUtils.copyProperties(dto, book);
            setField(book, "category", category);
            setField(book, "author", author);
            setField(book, "availableQuantity", dto.getAvailableQuantity() != null ? dto.getAvailableQuantity() : 0);
            setField(book, "totalQuantity", dto.getTotalQuantity() != null ? dto.getTotalQuantity() : 0);
            setField(book, "status", dto.getStatus() != null ? dto.getStatus() : BookStatus.Available);

            Book saved = bookRepository.save(book);
            log.info("Tạo sách thành công! ID: {}", saved.getBookId());
            return convertToDTO(saved);

        } catch (Exception e) {
            log.error("Lỗi khi tạo sách: ", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Không thể tạo sách: " + e.getMessage(), e);
        }
    }

    @Override
    public BookListDTO updateBook(Long id, BookListDTO dto) {
        try {
            Book book = bookRepository.findById(id)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy sách ID: " + id));

            Category category = getOrCreateCategory(dto.getCategory().getCategoryName());
            Author author = getOrCreateAuthor(dto.getAuthor());

            BeanUtils.copyProperties(dto, book);
            setField(book, "category", category);
            setField(book, "author", author);
            setField(book, "availableQuantity", dto.getAvailableQuantity() != null ? dto.getAvailableQuantity() : 0);
            setField(book, "totalQuantity", dto.getTotalQuantity() != null ? dto.getTotalQuantity() : 0);
            setField(book, "status", dto.getStatus());

            Book updated = bookRepository.save(book);
            log.info("Cập nhật sách thành công! ID: {}", id);
            return convertToDTO(updated);

        } catch (ResponseStatusException e) {
            throw e; // Đẩy lên controller
        } catch (Exception e) {
            log.error("Lỗi khi cập nhật sách ID {}: ", id, e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Cập nhật thất bại: " + e.getMessage(), e);
        }
    }

    @Override
    public void deleteBook(Long id) {
        if (!bookRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Sách không tồn tại với ID: " + id);
        }
        try {
            bookRepository.deleteById(id);
            log.info("Xóa sách thành công! ID: {}", id);
        } catch (Exception e) {
            log.error("Lỗi khi xóa sách ID {}: ", id, e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Xóa thất bại: " + e.getMessage(), e);
        }
    }

    @Override
    public BookListDTO getBookById(Long id) {
        return bookRepository.findById(id)
                .map(this::convertToDTO)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy sách ID: " + id));
    }

    // ==================== HELPER: CATEGORY & AUTHOR ====================
    private Category getOrCreateCategory(String categoryName) {
        return categoryRepository.findByCategoryName(categoryName)
                .orElseGet(() -> {
                    Category c = new Category();
                    setField(c, "categoryName", categoryName);
                    return categoryRepository.save(c);
                });
    }

    private Author getOrCreateAuthor(AuthorDTO authorDTO) {
        if (authorDTO == null || authorDTO.getAuthorName() == null || authorDTO.getAuthorName().trim().isEmpty()) {
            return null;
        }
        String name = authorDTO.getAuthorName().trim();
        return authorRepository.findByAuthorName(name)
                .orElseGet(() -> {
                    Author a = new Author();
                    setField(a, "authorName", name);
                    return authorRepository.save(a);
                });
    }
    

    // ==================== CONVERTER – AN TOÀN 100% ====================
    private BookListDTO convertToDTO(Book book) {
        BookListDTO.BookListDTOBuilder builder = BookListDTO.builder();

        builder.bookId(toLong(getField(book, "bookId")));
        builder.title((String) getField(book, "title"));
        builder.publisher((String) getField(book, "publisher"));
        builder.publishYear((Integer) getField(book, "publishYear"));
        builder.language((String) getField(book, "language"));
        builder.description((String) getField(book, "description"));
        builder.status((BookStatus) getField(book, "status"));
        builder.availableQuantity((Integer) getField(book, "availableQuantity"));
        builder.totalQuantity((Integer) getField(book, "totalQuantity"));

        Category cat = (Category) getField(book, "category");
        if (cat != null) {
            builder.category(CategoryDTO.builder()
                    .categoryId(toLong(getField(cat, "categoryId")))
                    .categoryName((String) getField(cat, "categoryName"))
                    .build());
        }

        Author auth = (Author) getField(book, "author");
        if (auth != null) {
            builder.author(AuthorDTO.builder()
                    .authorId(toLong(getField(auth, "authorId")))
                    .authorName((String) getField(auth, "authorName"))
                    .build());
        }

        return builder.build();
    }

    // ==================== REFLECTION HELPER ====================
    private Object getField(Object obj, String fieldName) {
        try {
            Field field = ReflectionUtils.findField(obj.getClass(), fieldName);
            if (field != null) {
                ReflectionUtils.makeAccessible(field);
                return field.get(obj);
            }
        } catch (Exception e) {
            log.warn("Không lấy được field {} từ {}", fieldName, obj.getClass().getSimpleName());
        }
        return null;
    }

    private void setField(Object obj, String fieldName, Object value) {
        try {
            Field field = ReflectionUtils.findField(obj.getClass(), fieldName);
            if (field != null) {
                ReflectionUtils.makeAccessible(field);
                field.set(obj, value);
            }
        } catch (Exception e) {
            log.warn("Không set được field {} cho {}", fieldName, obj.getClass().getSimpleName());
        }
    }

    private Long toLong(Object obj) {
        if (obj == null) return null;
        if (obj instanceof Long) return (Long) obj;
        if (obj instanceof Integer) return ((Integer) obj).longValue();
        if (obj instanceof Number) return ((Number) obj).longValue();
        return Long.valueOf(obj.toString());
    }
}