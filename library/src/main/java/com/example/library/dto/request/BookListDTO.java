package com.example.library.dto.request;

import com.example.library.entity.enums.BookStatus;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookListDTO {

    private Long bookId;

    @NotBlank(message = "Tên sách không được để trống")
    @Size(max = 255, message = "Tên sách không được vượt quá 255 ký tự")
    private String title;

    @NotBlank(message = "Tên nhà xuất bản không được để trống")
    @Size(max = 150, message = "Tên nhà xuất bản không được vượt quá 150 ký tự")
    private String publisher;

    @NotNull(message = "Năm xuất bản không được để trống")
    @Min(value = 1800, message = "Năm xuất bản phải lớn hơn hoặc bằng 1800")
    @Max(value = 2100, message = "Năm xuất bản phải nhỏ hơn hoặc bằng 2100")
    private Integer publishYear;

    @NotBlank(message = "Ngôn ngữ không được để trống")
    @Size(max = 50, message = "Ngôn ngữ không được vượt quá 50 ký tự")
    @Pattern(regexp = "^[A-Z]{3}$", message = "Ngôn ngữ phải là 3 chữ cái in hoa, ví dụ: ENG, VIE")
    private String language;

    @Size(max = 2000, message = "Mô tả không được vượt quá 2000 ký tự")
    private String description;

    /** Mặc định là Available khi thêm mới */
    private BookStatus status = BookStatus.Available;

    /** Liên kết Category */
    @Valid
    @NotNull(message = "Thể loại không được để trống")
    private CategoryDTO category;

    /** Liên kết Author (có thể null) */
    @Valid
    private AuthorDTO author;

    // SỐ LƯỢNG CÓ SẴN
    @Min(value = 0, message = "Số lượng có sẵn phải ≥ 0")
    private Integer availableQuantity = 0; // DEFAULT = 0

    // TỔNG SỐ LƯỢNG
    @Min(value = 0, message = "Tổng số lượng phải ≥ 0")
    private Integer totalQuantity = 0; // DEFAULT = 0
}