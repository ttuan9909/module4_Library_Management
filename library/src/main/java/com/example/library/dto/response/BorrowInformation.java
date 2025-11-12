package com.example.library.dto.response;

import com.example.library.entity.enums.BorrowStatus;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BorrowInformation {
    private Long borrowId;
    private Long userId;
    private String fullName;
    private String title;
    private LocalDateTime borrowDate;
    private LocalDate dueDate;
    private BigDecimal fineAmount;
    private BorrowStatus status;
    private String barcode;
    private Long publicationId;
}
