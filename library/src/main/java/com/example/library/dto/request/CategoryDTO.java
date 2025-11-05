package com.example.library.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoryDTO {
    private Long categoryId;

    @NotBlank(message = "Tên thể loại không được để trống")
    private String categoryName;
}
