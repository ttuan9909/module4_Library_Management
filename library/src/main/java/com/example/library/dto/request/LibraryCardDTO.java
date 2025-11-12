package com.example.library.dto.request;

import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LibraryCardDTO {
    private String cardNumber;

    // Ngày bắt đầu: Server sẽ gán mặc định nếu null
    private LocalDate startDate;

    // Ngày kết thúc: ✨ ĐÃ LOẠI BỎ @NotNull để cho phép Service tự động gán ✨
    private LocalDate endDate;

    // Trạng thái thẻ
    @Pattern(regexp = "ACTIVE|EXPIRED|SUSPENDED",
            message = "Trạng thái thẻ không hợp lệ. Chỉ chấp nhận ACTIVE, EXPIRED, SUSPENDED.")
    private String status;

    // Trường notes (tùy chọn)
    private String notes;
}
