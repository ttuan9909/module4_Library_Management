package com.example.library.dto.request;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LendingRequestdto {
    private Long userId;
    private List<LendingBookdto> books;
}
