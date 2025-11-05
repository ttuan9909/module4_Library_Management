package com.example.library.controller;

import com.example.library.service.IBorrowRenewService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/staff/borrow-renew")
@RequiredArgsConstructor
public class BorrowRenewStaffController {
    private final IBorrowRenewService renewService;

    // danh sách chờ duyệt
    @GetMapping
    public String listPending(Model model) {
        model.addAttribute("requests", renewService.getPendingRequests());
        return "staff/borrow-renew-list";
    }

    // duyệt
    @PostMapping("/{id}/approve")
    public String approve(@PathVariable Long id,
                          @RequestParam("employeeId") Long employeeId,
                          RedirectAttributes ra) {
        try {
            renewService.approveRequest(id, employeeId);
            ra.addFlashAttribute("success", "Đã duyệt yêu cầu gia hạn.");
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/staff/borrow-renew";
    }

    // từ chối
    @PostMapping("/{id}/reject")
    public String reject(@PathVariable Long id,
                         @RequestParam("employeeId") Long employeeId,
                         @RequestParam("reason") String reason,
                         RedirectAttributes ra) {
        try {
            renewService.rejectRequest(id, employeeId, reason);
            ra.addFlashAttribute("success", "Đã từ chối yêu cầu.");
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/staff/borrow-renew";
    }
}
