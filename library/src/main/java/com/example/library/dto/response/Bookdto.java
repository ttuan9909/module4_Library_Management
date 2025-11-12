package com.example.library.dto.response;

import com.example.library.entity.Category;
import com.example.library.entity.enums.BookStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Bookdto {
    private Long id;
    private String title;
    private Category category;
    private String publisher;
    private Integer publishYear;
    private String language;
    private String description;
    private BookStatus status;
    private String coverImage;
}
