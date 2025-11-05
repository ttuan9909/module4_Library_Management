package com.example.library.service;

import com.example.library.entity.BorrowTicket;
import com.example.library.entity.LibraryCard;
import com.example.library.entity.UserAccount;
import com.example.library.entity.enums.BorrowStatus;
import com.example.library.repository.BorrowTicketRepository;
import com.example.library.repository.LibraryCardRepository;
import com.example.library.repository.UserAccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class BorrowService implements IBorrowService {
    private final UserAccountRepository userRepo;
    private final LibraryCardRepository cardRepo;
    private final BorrowTicketRepository borrowRepo;

    private static final BigDecimal DAILY_FINE = BigDecimal.valueOf(5000);

    @Override
    public LibraryCard getCurrentCard() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        UserAccount user = userRepo.findByUsername(username)
                .orElseThrow(() -> new IllegalStateException("User not found"));
        return cardRepo.findByUser_UserId(user.getUserId())
                .orElseThrow(() -> new IllegalStateException("Library card not found"));
    }

    @Override
    public List<BorrowTicket> getCurrentBorrowings() {
//        String username = SecurityContextHolder.getContext().getAuthentication().getName();
//        UserAccount user = userRepo.findByUsername(username)
//                .orElseThrow(() -> new IllegalStateException("User not found: " + username));
//
//        LibraryCard card = cardRepo.findByUser_UserId(user.getUserId())
//                .orElseThrow(() -> new IllegalStateException("Library card not found for user: " + username));
//
//        return borrowRepo.findByCard_CardIdAndStatusIn(
//                card.getCardId(),
//                List.of(BorrowStatus.Borrowing, BorrowStatus.Overdue)
//        );
        LibraryCard card = getCurrentCard();
        return borrowRepo.findByCard_CardIdAndStatusIn(
                card.getCardId(),
                List.of(BorrowStatus.Borrowing, BorrowStatus.Overdue)
        );
    }

    @Override
    public List<Map<String, Object>> getCurrentBorrowingsWithFine() {
        List<BorrowTicket> tickets = getCurrentBorrowings();
        return tickets.stream()
                .map(t -> Map.of(
                        "ticket", t,
                        "fine", calculateFine(t)
                ))
                .toList();
    }

    @Override
    public List<BorrowTicket> getReturnedTickets() {
        LibraryCard card = getCurrentCard();
        return borrowRepo.findByCard_CardIdAndStatusIn(
                card.getCardId(),
                List.of(BorrowStatus.Returned, BorrowStatus.LostOrDamaged)
        );
    }

    @Override
    public List<BorrowTicket> getOverdue() {
        LibraryCard card = getCurrentCard();
        return borrowRepo.findByCard_CardIdAndStatusIn(
                card.getCardId(),
                List.of(BorrowStatus.Overdue)
        );
    }

    private BigDecimal calculateFine(BorrowTicket t) {
        LocalDate due = t.getDueDate();
        if (due == null) return t.getFineAmount();

        LocalDate today = LocalDate.now();

        // Nếu đã trả hoặc chưa quá hạn → giữ nguyên
        if (t.getReturnDate() != null || !today.isAfter(due)) {
            return t.getFineAmount();
        }

        // Nếu quá hạn → cộng thêm
        long daysLate = ChronoUnit.DAYS.between(due, today);
        BigDecimal extra = DAILY_FINE.multiply(BigDecimal.valueOf(daysLate));
        return t.getFineAmount().add(extra);
    }
}
