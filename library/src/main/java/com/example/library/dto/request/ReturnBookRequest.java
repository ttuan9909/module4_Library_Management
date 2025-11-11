package com.example.library.dto.request;

import com.example.library.entity.enums.BorrowStatus;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.List;

public class ReturnBookRequest {

    @NotEmpty(message = "Users list cannot be empty")
    private List<UserReturn> users;

    public List<UserReturn> getUsers() {
        return users;
    }

    public void setUsers(List<UserReturn> users) {
        this.users = users;
    }

    public static class UserReturn {
        @NotNull(message = "User ID cannot be null")
        private Long userId;

        @NotEmpty(message = "Book return list cannot be empty")
        private List<BookReturn> books;

        public Long getUserId() {
            return userId;
        }

        public void setUserId(Long userId) {
            this.userId = userId;
        }

        public List<BookReturn> getBooks() {
            return books;
        }

        public void setBooks(List<BookReturn> books) {
            this.books = books;
        }
    }

    public static class BookReturn {
        @NotNull(message = "Publication ID cannot be null")
        private Long publicationId;

        @NotNull(message = "Return date cannot be null")
        private String returnDate; // ISO String, sẽ parse thành LocalDate

        private BigDecimal fineAmount;

        private BorrowStatus status;

        public Long getPublicationId() { return publicationId; }
        public void setPublicationId(Long publicationId) { this.publicationId = publicationId; }

        public String getReturnDate() { return returnDate; }
        public void setReturnDate(String returnDate) { this.returnDate = returnDate; }

        public BigDecimal getFineAmount() { return fineAmount; }
        public void setFineAmount(BigDecimal fineAmount) { this.fineAmount = fineAmount; }

        public BorrowStatus getStatus() { return status; }
        public void setStatus(BorrowStatus status) { this.status = status; }
    }
}
