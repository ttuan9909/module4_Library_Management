package com.example.library.controller;

//import com.example.library.dto.request.LendingRequestDTO;
import com.example.library.dto.request.LendingRequestdto;
import com.example.library.dto.response.BorrowInformation;
import com.example.library.service.ILendingBookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

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
//@PostMapping("")
//public ResponseEntity<?> createNewLending(
//        @RequestBody LendingRequestDTO request, // <-- Hứng DTO "xịn"
//        @AuthenticationPrincipal UserDetails loggedInUser) {
//
//    if (loggedInUser == null) {
//        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Yêu cầu đăng nhập.");
//    }
//
//    try {
//        // 3. GỌI MỘT HÀM SERVICE "THỰC THI"
//        // (Như tôi đã "kê đơn" ở lượt trước)
//        lendingBookService.createNewBorrowTicket(request, loggedInUser.getUsername());
//
//        // 4. TRẢ VỀ "OK" (200) KHI THÀNH CÔNG
//        return ResponseEntity.ok().body("Tạo phiếu mượn thành công!");
//
//    } catch (Exception e) {
//        // 5. NẾU LỖI NGHIỆP VỤ (sách đã mượn, user bị khóa...),
//        // SERVICE SẼ "NÉM" VÀ BỊ BẮT Ở ĐÂY (VẪN LÀ 400)
//        return ResponseEntity.badRequest().body(e.getMessage());
//    }
//}


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
