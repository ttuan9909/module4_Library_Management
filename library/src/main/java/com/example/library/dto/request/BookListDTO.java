package com.example.library.dto.request;

import com.example.library.entity.Category;
import com.example.library.entity.enums.BookStatus;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookListDTO {

    private Long bookId;
    private String title;
    private Category category;
    private String publisher;
    private Integer publishYear;
    private String language;
    private String description;
    private BookStatus status;

    @Builder.Default
    private Integer quantity = 0; // THÊM VÀO ĐÂY – KHÔNG CẦN TRONG ENTITY
}