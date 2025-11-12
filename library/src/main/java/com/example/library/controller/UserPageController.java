package com.example.library.controller;

import com.example.library.dto.request.UserAccountDTO;
import com.example.library.service.UserAccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin/users")
@RequiredArgsConstructor
public class UserPageController {
    private final UserAccountService userService;

    // Trang danh sách thành viên (chỉ trả HTML, JS sẽ tự load dữ liệu)
    @GetMapping
    public String listUsers() {
        return "admin/user/list"; // → src/main/resources/templates/admin/user/list.html
    }

    // Trang chi tiết thành viên
    @GetMapping("/detail/{userId}")
    public String getUserDetailView(@PathVariable("userId") Long userId, Model model) {
        try {
            UserAccountDTO userDTO = userService.getUserById(userId);
            model.addAttribute("user", userDTO);
            return "admin/user/detail"; // → detail.html
        } catch (IllegalArgumentException e) {
            // Không tìm thấy → quay về danh sách
            return "redirect:/admin/users";
        }
    }
}
