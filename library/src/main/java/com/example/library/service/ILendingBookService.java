package com.example.library.service;

import com.example.library.dto.request.LendingBookdto;
import com.example.library.dto.request.LendingRequestdto;
import com.example.library.dto.response.BorrowInformation;

import java.util.Optional;

public interface ILendingBookService {
    boolean isBookLent(LendingRequestdto lendingRequestdto);
    Optional<BorrowInformation> findBorrowInformationByBarcode(String barcode);
}
