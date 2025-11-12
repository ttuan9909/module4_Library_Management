package com.example.library.service;

import com.example.library.entity.BorrowRenewRequest;

import java.time.LocalDate;
import java.util.List;

public interface IBorrowRenewService {
    BorrowRenewRequest requestRenew(Long borrowTicketId, LocalDate newDueDate, String notes);
    List<BorrowRenewRequest> getPendingRequests();
    void approveRequest(Long requestId, Long employeeId);
    void rejectRequest(Long requestId, Long employeeId, String reason);
    List<BorrowRenewRequest> getMyRequests(String username);
}
