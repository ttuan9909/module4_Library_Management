package com.example.library.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Publicationdto {
    private Long publicationId;
    private String barcode;
    private String notes;

    private Long bookId;
    private String title;
    private String publisher;
    private Integer publishYear;
    private String language;
    private String description;

    private Integer categoryId;
    private String categoryName;
}
