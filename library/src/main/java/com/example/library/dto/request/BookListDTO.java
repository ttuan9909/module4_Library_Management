package com.example.library.dto.request;

import com.example.library.entity.Category;
import com.example.library.entity.enums.BookStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookListDTO {

    private Long bookId;
    private String title;
    private Category category;
    private Integer categoryId;
    private String publisher;
    private Integer publishYear;
    private String language;
    private String description;
    private BookStatus status;
    private String coverImage;

    @Builder.Default
    private Integer quantity = 0;
}
