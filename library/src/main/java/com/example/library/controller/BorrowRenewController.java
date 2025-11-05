package com.example.library.controller;

import com.example.library.service.IBorrowRenewService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;

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
}
