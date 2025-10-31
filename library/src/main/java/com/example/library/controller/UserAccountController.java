package com.example.library.controller;
import com.example.library.dto.request.UserAccountDTO;
import com.example.library.repository.RoleRepository;
import com.example.library.service.UserAccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
@Controller
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserAccountController {
    private final UserAccountService userService;
    private final RoleRepository roleRepository;
    /** 👉 Hiển thị danh sách người dùng */
    @GetMapping
    public String listUsers(Model model) {
        model.addAttribute("users", userService.getAllUsers());
        return "user/list";
    }
    /** 👉 Hiển thị form tạo người dùng mới */
    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("user", new UserAccountDTO());
        model.addAttribute("roles", roleRepository.findAll());
        return "user/create";
    }
    /** 👉 Submit form thêm người dùng mới */
    @PostMapping("/create")
    public String createUser(@ModelAttribute("user") UserAccountDTO dto, Model model) {
        try {
            userService.createUser(dto);
            model.addAttribute("message", "✅ Thêm thành viên thành công!");
            model.addAttribute("user", new UserAccountDTO());
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("user", dto);
        }
        model.addAttribute("roles", roleRepository.findAll());
        return "user/create";
    }
    /** 👉 Hiển thị form chỉnh sửa người dùng */
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        var existing = userService.getUserById(id);
        model.addAttribute("user", existing);
        model.addAttribute("roles", roleRepository.findAll());
        return "user/edit";
    }
    /** 👉 Submit form cập nhật thông tin */
    @PostMapping("/edit/{id}")
    public String updateUser(@PathVariable Long id,
                             @ModelAttribute("user") UserAccountDTO dto,
                             Model model) {
        try {
            userService.updateUser(id, dto);
            model.addAttribute("message", "✅ Cập nhật thành công!");
        } catch (Exception e) {
            model.addAttribute("error", "❌ " + e.getMessage());
        }
        model.addAttribute("roles", roleRepository.findAll());
        return "user/edit";
    }
}