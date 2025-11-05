package com.example.library.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthorDTO {
    private Long authorId;

    @NotBlank(message = "Tên tác giả không được để trống")
    private String authorName;
}
