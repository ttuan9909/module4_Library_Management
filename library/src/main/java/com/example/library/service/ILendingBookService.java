package com.example.library.service;

import com.example.library.dto.request.LendingBookdto;
import com.example.library.dto.request.LendingRequestdto;

public interface ILendingBookService {
    boolean isBookLent(LendingRequestdto lendingRequestdto);
}
