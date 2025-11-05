package com.example.library.service;

import com.example.library.entity.BorrowTicket;
import com.example.library.entity.LibraryCard;

import java.util.List;
import java.util.Map;

public interface IBorrowService {
    LibraryCard getCurrentCard();
    List<BorrowTicket> getCurrentBorrowings();
    List<Map<String, Object>> getCurrentBorrowingsWithFine();
    List<BorrowTicket> getReturnedTickets();
    List<BorrowTicket> getOverdue();
}
