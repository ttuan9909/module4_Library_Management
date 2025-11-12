package com.example.library.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CategoryDTO {

    private Integer categoryId;
    @NotBlank(message = "Tên thể loại không được để trống")
    @Size(max = 120, message = "Tên thể loại không được vượt quá 120 ký tự")
    private String categoryName;
    @Size(max = 255, message = "Mô tả không được vượt quá 255 ký tự")
    private String description;


}
