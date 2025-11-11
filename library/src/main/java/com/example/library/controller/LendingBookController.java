package com.example.library.controller;

import com.example.library.dto.request.LendingRequestdto;
import com.example.library.dto.response.BorrowInformation;
import com.example.library.service.ILendingBookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/v1/lending")
public class LendingBookController {
    @Autowired
    ILendingBookService lendingBookService;

    @PostMapping("")
    public ResponseEntity<?> lendBook(@RequestBody LendingRequestdto request) {
        boolean isBookLent = lendingBookService.isBookLent(request);
        if (!isBookLent) {
            return ResponseEntity.status(400).build();
        } else {
            return ResponseEntity.ok().build();
        }
    }

    @GetMapping("/{barcode}")
    public ResponseEntity<?> findBorrowInformationByBarcode(@PathVariable String barcode) {
        BorrowInformation borrowInformation = lendingBookService.findBorrowInformationByBarcode(barcode).orElse(null);
        if (barcode == null || borrowInformation == null) {
            return ResponseEntity.status(400).build();
        } else {
            return ResponseEntity.ok(borrowInformation);
        }
    }

}
