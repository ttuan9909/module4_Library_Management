package com.example.library.controller;

import com.example.library.dto.request.ChangePasswordRequest;
import com.example.library.dto.request.ProfileUpdateRequest;
import com.example.library.dto.request.UserAccountDTO;
import com.example.library.entity.UserAccount;
import com.example.library.repository.RoleRepository;
import com.example.library.service.UserAccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

@Controller
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserAccountController {

    private final UserAccountService userService;
    private final RoleRepository roleRepository;

    // ====================== DANH SÁCH NGƯỜI DÙNG ======================
    @GetMapping
    public String listUsers(Model model) {
        model.addAttribute("users", userService.getAllUsers());
        return "admin/user/list"; // chỉ còn trang list (modal thêm user nằm trong đây)
    }

    // ====================== AJAX: TẠO NGƯỜI DÙNG ======================
    @PostMapping(value = "/create", consumes = "application/json", produces = "application/json")
    @ResponseBody
    public ResponseEntity<?> createUserAjax(@RequestBody UserAccountDTO dto) {
        try {
            UserAccountDTO saved = userService.createUser(dto);
            return ResponseEntity.ok(saved);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // ====================== SỬA NGƯỜI DÙNG ======================
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        var existing = userService.getUserById(id);
        model.addAttribute("user", existing);
        model.addAttribute("roles", roleRepository.findAll());
        return "admin/user/edit";
    }

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
        return "admin/user/edit";
    }

    // ====================== HỒ SƠ NGƯỜI DÙNG ======================
    @GetMapping("/me")
    @ResponseBody
    public UserAccount getProfile(@RequestParam String username) {
        return userService.me(username);
    }

    @PostMapping("/update-profile")
    @ResponseBody
    public UserAccount updateProfile(@RequestParam String username,
                                     @RequestBody ProfileUpdateRequest req) {
        return userService.updateProfile(username, req);
    }

    // ====================== ĐỔI MẬT KHẨU ======================
    @PostMapping("/change-password")
    @ResponseBody
    public ResponseEntity<?> changePassword(@RequestParam String username,
                                            @RequestBody ChangePasswordRequest req) {
        userService.changePassword(username, req);
        return ResponseEntity.ok("Password updated successfully");
    }
}
