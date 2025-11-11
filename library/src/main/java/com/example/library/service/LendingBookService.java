package com.example.library.service;

import com.example.library.dto.request.LendingRequestdto;
import com.example.library.dto.response.BorrowInformation;
import com.example.library.entity.BorrowTicket;
import com.example.library.entity.LibraryCard;
import com.example.library.entity.Publication;
import com.example.library.entity.enums.BorrowStatus;
import com.example.library.entity.enums.PublicationStatus;
import com.example.library.repository.BorrowTicketRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.beans.Transient;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class LendingBookService implements ILendingBookService{
    @Autowired
    ILibraryCardService libraryCardService;
    @Autowired
    IPublicationService publicationService;
    @Autowired
    BorrowTicketRepository borrowTicketRepository;

//    @Override
//    public boolean isBookLent(LendingRequestdto lendingRequestdto) {
//        List<BorrowTicket> borrowTickets = new ArrayList<>();
//        Long userId = lendingRequestdto.getUserId();
//        LibraryCard isCardExist = libraryCardService.findByUserId(userId).orElse(null);
//        boolean isPublicationsExist = true;
//        for (int i = 0; i < lendingRequestdto.getBooks().size(); i++) {
//            boolean status = publicationService.findByPublicationId(lendingRequestdto.getBooks().get(i).getPublicationId()).filter(pub -> pub.getStatus() == PublicationStatus.Available).isPresent();
//            if (!status) {
//                isPublicationsExist = false;
//                break;
//            } else {
//
//            }
//        }
//        return false;
//    }

    @Transactional
    @Override
    public boolean isBookLent(LendingRequestdto lendingRequestdto) {
        List<BorrowTicket> borrowTickets = new ArrayList<>();
        Long userId = lendingRequestdto.getUserId();
        LibraryCard card = libraryCardService.findByUserUserId(userId).orElse(null);

        if (card == null) return false;

        for (var bookDto : lendingRequestdto.getBooks()) {
            Publication publication = publicationService
                    .findByPublicationId(bookDto.getPublicationId())
                    .filter(pub -> pub.getStatus() == PublicationStatus.Available)
                    .orElse(null);

            if (publication == null) {
                return false;
            }

            BorrowTicket ticket = BorrowTicket.builder()
                    .ticketNumber("TCK-" + System.currentTimeMillis())
                    .card(card)
                    .publication(publication)
                    .borrowDate(LocalDateTime.now())
                    .dueDate(bookDto.getReturnDate())
                    .renewalCount(0)
                    .status(BorrowStatus.Borrowing)
                    .fineAmount(BigDecimal.ZERO)
                    .notes("Initial lending")
                    .build();

            borrowTickets.add(ticket);

            publication.setStatus(PublicationStatus.Borrowed);
            publicationService.save(publication);
        }

        borrowTicketRepository.saveAll(borrowTickets);

        return true;
    }

    @Override
    public Optional<BorrowInformation> findBorrowInformationByBarcode(String barcode) {
        return Optional.of(borrowTicketRepository.findBorrowInformationByBarcode(barcode));
    }
}
