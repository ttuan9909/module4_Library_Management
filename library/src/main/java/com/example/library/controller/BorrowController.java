package com.example.library.controller;

import com.example.library.service.BorrowService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/account")
public class BorrowController {
    private final BorrowService borrowService;

    @GetMapping("/borrowings")
    public String currentBorrowings(Model model) {
        model.addAttribute("borrowings", borrowService.getCurrentBorrowingsWithFine());
        return "account/borrowings";
    }

    @GetMapping("/returned")
    public String viewReturned(Model model) {
        model.addAttribute("returnedList", borrowService.getReturnedTickets());
        model.addAttribute("pageTitle", "Sách đã trả");
        model.addAttribute("breadcrumb", "Đã trả");
        return "account/returned";
    }
}
