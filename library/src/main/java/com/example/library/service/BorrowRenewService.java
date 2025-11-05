package com.example.library.service;

import com.example.library.entity.*;
import com.example.library.entity.enums.BorrowStatus;
import com.example.library.entity.enums.RenewRequestStatus;
import com.example.library.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BorrowRenewService implements IBorrowRenewService{
    private final BorrowTicketRepository borrowTicketRepo;
    private final LibraryCardRepository cardRepo;
    private final BorrowRenewRequestRepository renewRequestRepo;
    private final UserAccountRepository userRepo;
    private final EmployeeRepository employeeRepo; // nếu bạn có

    // ví dụ giới hạn gia hạn tối đa 30 ngày từ hôm nay
    private static final int MAX_EXTEND_DAYS = 30;

    @Override
    public BorrowRenewRequest requestRenew(Long borrowTicketId, LocalDate newDueDate, String notes) {
        // 1. lấy user hiện tại
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        UserAccount user = userRepo.findByUsername(username)
                .orElseThrow(() -> new IllegalStateException("User not found"));

        // 2. lấy card của user
        LibraryCard card = cardRepo.findByUser_UserId(user.getUserId())
                .orElseThrow(() -> new IllegalStateException("Library card not found"));

        // 3. lấy borrow ticket
        BorrowTicket ticket = borrowTicketRepo.findById(borrowTicketId)
                .orElseThrow(() -> new IllegalArgumentException("Borrow ticket not found"));

        // 3a. kiểm tra ticket này có thuộc card của user không
        if (!ticket.getCard().getCardId().equals(card.getCardId())) {
            throw new IllegalStateException("You are not the owner of this borrow ticket");
        }

        // 4. kiểm tra trạng thái phải đang mượn hoặc quá hạn
        if (ticket.getStatus() != BorrowStatus.Borrowing &&
                ticket.getStatus() != BorrowStatus.Overdue) {
            throw new IllegalStateException("This ticket cannot be renewed");
        }

        // 5. validate ngày user nhập
        if (newDueDate == null) {
            throw new IllegalArgumentException("New due date is required");
        }

        // không được gia hạn lùi lại
        if (newDueDate.isBefore(ticket.getDueDate())) {
            throw new IllegalArgumentException("New due date must be after current due date");
        }

        // hạn chế không cho quá xa (tùy bạn, có thể bỏ)
        if (newDueDate.isAfter(LocalDate.now().plusDays(MAX_EXTEND_DAYS))) {
            throw new IllegalArgumentException("New due date is too far");
        }

        // 6. kiểm tra đã có request PENDING cho ticket này chưa
        boolean existsPending = renewRequestRepo.existsByBorrowTicket_BorrowIdAndStatus(
                borrowTicketId,
                RenewRequestStatus.PENDING
        );
        if (existsPending) {
            throw new IllegalStateException("There is already a pending renew request for this ticket");
        }

        // 7. tạo request
        BorrowRenewRequest req = BorrowRenewRequest.builder()
                .borrowTicket(ticket)
                .requestedBy(card)
                .proposedDueDate(newDueDate)
                .notes(notes)
                .status(RenewRequestStatus.PENDING)
                .build();

        return renewRequestRepo.save(req);
    }


    @Override
    public List<BorrowRenewRequest> getPendingRequests() {
        return renewRequestRepo.findByStatus(RenewRequestStatus.PENDING);
    }

    @Override
    public void approveRequest(Long requestId, Long employeeId) {
        BorrowRenewRequest req = renewRequestRepo.findById(requestId)
                .orElseThrow(() -> new IllegalArgumentException("Renew request not found"));

        if (req.getStatus() != RenewRequestStatus.PENDING) {
            throw new IllegalStateException("Request is not pending");
        }

        BorrowTicket ticket = req.getBorrowTicket();

        // cập nhật borrow ticket: set hạn mới và tăng số lần gia hạn
        ticket.setDueDate(req.getProposedDueDate());
        ticket.setRenewalCount(ticket.getRenewalCount() + 1);

        // nếu trước đó là Overdue thì cho về Borrowing
        if (ticket.getStatus() == BorrowStatus.Overdue) {
            ticket.setStatus(BorrowStatus.Borrowing);
        }

        // gán người duyệt
        Employee emp = employeeRepo.findById(employeeId)
                .orElseThrow(() -> new IllegalArgumentException("Employee not found"));
        req.setApprovedBy(emp);
        req.setApprovedAt(LocalDateTime.now());
        req.setStatus(RenewRequestStatus.APPROVED);

        // lưu
        borrowTicketRepo.save(ticket);
        renewRequestRepo.save(req);
    }

    @Override
    public void rejectRequest(Long requestId, Long employeeId, String reason) {
        BorrowRenewRequest req = renewRequestRepo.findById(requestId)
                .orElseThrow(() -> new IllegalArgumentException("Renew request not found"));

        if (req.getStatus() != RenewRequestStatus.PENDING) {
            throw new IllegalStateException("Request is not pending");
        }

        Employee emp = employeeRepo.findById(employeeId)
                .orElseThrow(() -> new IllegalArgumentException("Employee not found"));

        req.setApprovedBy(emp);
        req.setApprovedAt(LocalDateTime.now());
        req.setStatus(RenewRequestStatus.REJECTED);

        // lưu lý do từ chối vào notes (hoặc tạo cột riêng, tùy bạn)
        if (reason != null && !reason.isBlank()) {
            String existing = req.getNotes();
            if (existing == null || existing.isBlank()) {
                req.setNotes("REJECTED: " + reason);
            } else {
                req.setNotes(existing + " | REJECTED: " + reason);
            }
        }

        renewRequestRepo.save(req);
    }
    }

