package com.example.library.repository;

import com.example.library.entity.Author;
import com.example.library.entity.Book;
import com.example.library.entity.Category;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

// Giả lập cơ sở dữ liệu (In-Memory)
@Repository
public class BookRepository {
    private final List<Book> books = new ArrayList<>();
// ... existing code ...
    // Giả lập ID tự tăng: ĐÃ CHUYỂN TẤT CẢ VỀ KIỂU Integer
    private Integer nextBookId = 1; // ✅ ĐÃ SỬA: Long -> Integer
    private Integer nextAuthorId = 1;
    private Integer nextCategoryId = 1;

    // =========================================================
// ... existing code ...
    // =========================================================

    public List<Book> findAll() { return books; }

    public Optional<Book> findById(Long id) {
        // Giả định BookId trong Entity cũng là Integer
        // Nếu BookId là Long, giữ nguyên: b.getBookId().equals(id)
        // Nếu BookId là Integer, dùng: b.getBookId().equals(id.intValue())
        // Giả sử BookId cũng là Integer:
        return books.stream().filter(b -> b.getBookId().equals(id.intValue())).findFirst();
    }
    
    public Book save(Book book) {
        if (book.getBookId() == null) {
            // ✅ ĐÃ SỬA: Gán Integer (giả định setBookId nhận Integer)
            book.setBookId(nextBookId++); 
        } else {
// ... existing code ...
        }
        books.add(book);
        return book;
    }

    public void deleteById(Long id) {
        // Giả sử BookId là Integer
        books.removeIf(b -> b.getBookId().equals(id.intValue()));
    }
    
    // =========================================================
// ... existing code ...
    // =========================================================
    
    public Optional<Author> findAuthorByName(String authorName) {
// ... existing code ...
    }
    
    public Author saveAuthor(Author author) {
        if (author.getAuthorId() == null) {
            // ✅ SỬA LỖI: Truyền trực tiếp Integer (nextAuthorId) 
            // vào setter (setAuthorId) đang mong đợi Integer.
            author.setAuthorId(nextAuthorId);
            nextAuthorId++;
            authors.add(author);
        }
        return author;
    }

    // =========================================================
// ... existing code ...
    // =========================================================

    public Optional<Category> findCategoryByName(String categoryName) {
// ... existing code ...
    }
    
    public List<Category> findAllCategories() {
// ... existing code ...
    }

    // Phương thức phụ trợ để thêm Category mẫu từ Service
    public void addInitialCategory(Category category) {
        if (category.getCategoryId() == null) {
             // ✅ SỬA LỖI: Truyền trực tiếp Integer (nextCategoryId)
             category.setCategoryId(nextCategoryId); 
             nextCategoryId++;
        }
        categories.add(category);
    }
    
    // =========================================================
// ... existing code ...
    // =========================================================
    
    public List<String>