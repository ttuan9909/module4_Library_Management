package com.example.library.controller;

import com.example.library.entity.Employee;
import com.example.library.repository.EmployeeRepository;
import com.example.library.service.IBorrowRenewService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;

@Controller
@RequestMapping("/admin/borrow-renew")
@RequiredArgsConstructor
public class BorrowRenewAdminController {
    private final IBorrowRenewService renewService;
    private final EmployeeRepository employeeRepo;

    // danh sách chờ duyệt
    @GetMapping
    public String listPending(Model model) {
        model.addAttribute("requests", renewService.getPendingRequests());
        return "admin/borrowRenew/borrow-renew-list";
    }

    // duyệt
    @PostMapping("/{id}/approve")
    public String approve(@PathVariable Long id,
                          Principal principal, // <-- Sửa: Lấy người dùng đang login
                          RedirectAttributes ra) {
        try {
            // Tự tìm ID của admin đang đăng nhập
            Employee admin = employeeRepo.findByUsername(principal.getName())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy Employee (admin) cho user: " + principal.getName()));

            renewService.approveRequest(id, admin.getEmployeeId()); // <-- Sửa: Dùng ID vừa tìm được
            ra.addFlashAttribute("success", "Đã duyệt yêu cầu gia hạn.");
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/borrow-renew";
    }

    // từ chối
    @PostMapping("/{id}/reject")
    public String reject(@PathVariable Long id,
                         Principal principal, // <-- Sửa: Lấy người dùng đang login
                         @RequestParam("reason") String reason,
                         RedirectAttributes ra) {
        try {
            // Tự tìm ID của admin đang đăng nhập
            Employee admin = employeeRepo.findByUsername(principal.getName())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy Employee (admin) cho user: " + principal.getName()));

            renewService.rejectRequest(id, admin.getEmployeeId(), reason); // <-- Sửa: Dùng ID vừa tìm được
            ra.addFlashAttribute("success", "Đã từ chối yêu cầu.");
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/borrow-renew";
    }
}
