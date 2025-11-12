package com.example.library.service;

//import com.example.library.dto.request.LendingRequestDTO;
import com.example.library.dto.request.LendingRequestdto;
import com.example.library.dto.response.BorrowInformation;
import com.example.library.entity.*;
import com.example.library.entity.enums.BorrowStatus;
import com.example.library.entity.enums.LibraryCardStatus;
import com.example.library.entity.enums.PublicationStatus;
import com.example.library.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class LendingBookService implements ILendingBookService {
    private static final long BORROW_DAYS_LIMIT = 14;
    @Autowired
    ILibraryCardService libraryCardService;
    @Autowired
    IPublicationService publicationService;
//    @Autowired
//    BorrowTicketRepository borrowTicketRepository;

    @Autowired
    private LibraryCardRepository cardRepository;
    @Autowired
    private PublicationRepository publicationRepository;
    @Autowired
    private BorrowTicketRepository borrowTicketRepository;
    @Autowired
    private EmployeeRepository employeeRepository;
    @Autowired
    private UserAccountRepository userAccountRepository;


//    @Transactional
//    @Override
//    public boolean isBookLent(LendingRequestdto lendingRequestdto) {
//        List<BorrowTicket> borrowTickets = new ArrayList<>();
//        Long userId = lendingRequestdto.getUserId();
//        LibraryCard card = libraryCardService.findByUserUserId(userId).orElse(null);
//
//        if (card == null) return false;
//
//        for (var bookDto : lendingRequestdto.getBooks()) {
//            Publication publication = publicationService
//                    .findByPublicationId(bookDto.getPublicationId())
//                    .filter(pub -> pub.getStatus() == PublicationStatus.Available)
//                    .orElse(null);
//
//            if (publication == null) {
//                return false;
//            }
//
//            BorrowTicket ticket = BorrowTicket.builder()
//                    .ticketNumber("TCK-" + System.currentTimeMillis())
//                    .card(card)
//                    .publication(publication)
//                    .borrowDate(LocalDateTime.now())
//                    .dueDate(bookDto.getReturnDate())
//                    .renewalCount(0)
//                    .status(BorrowStatus.Borrowing)
//                    .fineAmount(BigDecimal.ZERO)
//                    .notes("Initial lending")
//                    .build();
//
//            borrowTickets.add(ticket);
//
//            publication.setStatus(PublicationStatus.Borrowed);
//            publicationService.save(publication);
//        }
//
//        borrowTicketRepository.saveAll(borrowTickets);
//
//        return true;
//    }
//
    @Override
    public Optional<BorrowInformation> findBorrowInformationByBarcode(String barcode) {
        return Optional.of(borrowTicketRepository.findBorrowInformationByBarcode(barcode));
    }

    @Transactional
    @Override
    public boolean isBookLent(LendingRequestdto lendingRequestdto) {
        if (lendingRequestdto == null || lendingRequestdto.getUserId() == null ||
                lendingRequestdto.getBooks() == null || lendingRequestdto.getBooks().isEmpty()) {
            // invalid request
            return false;
        }

        Long userId = lendingRequestdto.getUserId();
        LibraryCard card = libraryCardService.findByUserUserId(userId).orElse(null);
        if (card == null) {
            // user has no card
            return false;
        }

        Employee defaultStaff = employeeRepository.findById(3L)
                .orElseThrow(() -> new IllegalStateException("Lỗi hệ thống: Không tìm thấy Employee"));

        List<BorrowTicket> borrowTickets = new ArrayList<>();

        for (var bookDto : lendingRequestdto.getBooks()) {
            if (bookDto == null || bookDto.getPublicationId() == null) {
                // invalid book dto
                return false;
            }

            // tìm publication và lock nếu cần (tùy cách repository)
            Publication publication = publicationService
                    .findByPublicationId(bookDto.getPublicationId())
                    .filter(pub -> pub.getStatus() == PublicationStatus.Available)
                    .orElse(null);

            if (publication == null) {
                // nếu bất kỳ publication không khả dụng => rollback toàn bộ
                return false;
            }

            // đảm bảo returnDate hợp lệ
            LocalDate returnDateLocal = bookDto.getReturnDate();
            if (returnDateLocal == null) {
                // nếu thiếu ngày trả, bạn có thể set mặc định hoặc trả false
                return false;
            }
            // chuyển sang LocalDateTime nếu entity dùng LocalDateTime
            LocalDateTime dueDateTime = returnDateLocal.atStartOfDay();

            BorrowTicket ticket = BorrowTicket.builder()
                    .ticketNumber("TCK-" + UUID.randomUUID().toString())
                    .card(card)
                    .staff(defaultStaff)
                    .publication(publication)
                    .borrowDate(LocalDateTime.now())
                    .dueDate(LocalDate.from(dueDateTime)) // tùy kiểu field trong BorrowTicket
                    .renewalCount(0)
                    .status(BorrowStatus.Borrowing)
                    .fineAmount(BigDecimal.ZERO)
                    .notes("Initial lending")
                    .build();

            borrowTickets.add(ticket);

            publication.setStatus(PublicationStatus.Borrowed);
            publicationService.save(publication); // đảm bảo save tham gia transaction
        }

        borrowTicketRepository.saveAll(borrowTickets);

        return true;
    }

}
