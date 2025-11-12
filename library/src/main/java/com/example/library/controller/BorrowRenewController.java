package com.example.library.controller;

import com.example.library.entity.BorrowRenewRequest;
import com.example.library.service.IBorrowRenewService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.time.LocalDate;
import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/account/borrowings")
public class BorrowRenewController {
    private final IBorrowRenewService renewService;

    @PostMapping("/{id}/renew")
    public String requestRenew(@PathVariable("id") Long borrowId,
                               @RequestParam("newDueDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
                               LocalDate newDueDate,
                               @RequestParam(value = "notes", required = false) String notes,
                               RedirectAttributes ra) {
        try {
            renewService.requestRenew(borrowId, newDueDate, notes);
            ra.addFlashAttribute("success", "Yêu cầu gia hạn đã được gửi, vui lòng chờ nhân viên duyệt.");
        } catch (Exception ex) {
            ra.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/account/borrowings";
    }

    @GetMapping("/my-requests")
    public String showMyRenewRequests(Model model, Principal principal, RedirectAttributes ra) {
        if (principal == null) {
            return "redirect:/auth/login"; // Yêu cầu đăng nhập
        }

        try {
            // Lấy username của người đang đăng nhập
            String username = principal.getName();

            // Gọi service (Bước 2)
            List<BorrowRenewRequest> requests = renewService.getMyRequests(username);

            // Gửi danh sách qua Model
            model.addAttribute("myRequests", requests);

            // Trả về tên file HTML
            return "account/my-renew-requests"; // (Tên file view bạn sẽ tạo)

        } catch (Exception e) {
            ra.addFlashAttribute("error", "Lỗi khi tải danh sách yêu cầu: " + e.getMessage());
            return "redirect:/account/borrowings";
        }
    }
}
