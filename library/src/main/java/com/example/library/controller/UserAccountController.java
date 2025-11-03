package com.example.library.controller;

import com.example.library.dto.request.ChangePasswordRequest;
import com.example.library.dto.request.ProfileUpdateRequest;
import com.example.library.dto.request.UserAccountDTO;
import com.example.library.entity.UserAccount;
import com.example.library.service.UserAccountService;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.method.P;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@Controller
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserAccountController {

    private final UserAccountService userService;

    // ====================== DANH SÁCH NGƯỜI DÙNG ======================
    @GetMapping
    public String listUsers(Model model) {
        model.addAttribute("users", userService.getAllUsers());
        return "admin/user/list"; // chỉ còn trang list (modal thêm user nằm trong đây)
    }

    // ====================== TÍNH NĂNG XEM CHI TIẾT ======================
    // ⭐️ FIX: Chỉ định rõ @PathVariable("id")
    @GetMapping("/{id}/detail")
    public String getUserDetail(@PathVariable("id") Long id, Model model) {
        try {
            UserAccountDTO userDto = userService.getUserById(id);
            model.addAttribute("user", userDto);
            return "admin/user/detail"; 
        } catch (IllegalArgumentException e) {
            // Chuyển hướng khi không tìm thấy ID
            model.addAttribute("errorMessage", "Không tìm thấy người dùng: " + e.getMessage());
            return "redirect:/users";
        }
    }
    // ====================== AJAX CRUD ======================
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
    
    // AJAX: LẤY DỮ LIỆU USER THEO ID (Dùng cho Modal Edit)
    // URL: GET /users/123
    @GetMapping("/{id}")
    @ResponseBody
    public ResponseEntity<?> getUserByIdAjax(@PathVariable("id") Long userId){
        try {
            UserAccountDTO userDto = userService.getUserById(userId);
            return ResponseEntity.ok(userDto); // Trả về 200 OK với DTO
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build(); // Trả về 404 Not Found
        }
    }

    // AJAX: CẬP NHẬT NGƯỜI DÙNG (PUT)
    // URL: PUT /users/update/123
    @PutMapping("/update/{id}")
    @ResponseBody
    public ResponseEntity<?> updateUserAjax(@PathVariable("id") Long userId,
                                            @RequestBody UserAccountDTO dto){ 
        try {
            UserAccountDTO updated = userService.updateUser(userId, dto);
            return ResponseEntity.ok(updated); // Trả về 200 OK với đối tượng đã cập nhật
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Cập nhật thất bại: " + e.getMessage());
        }
    }

    // ⭐️ API XÓA THÀNH VIÊN (DÙNG AJAX DELETE)
    // URL: DELETE /users/123
    @DeleteMapping("/{id}")
    @ResponseBody 
    public ResponseEntity<?> deleteUser(@PathVariable("id") Long userId){
        try {
            // FIXED: Thay 'id' bằng 'userId'
            userService.deleteUser(userId); 
            
            // Trả về response 200 OK khi thành công
            return ResponseEntity.ok().build(); 
        } catch (IllegalArgumentException e) {
            // Lỗi 404/400: Không tìm thấy người dùng
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", e.getMessage()));
        } catch (IllegalStateException e) {
            // Lỗi 409 Conflict: Lỗi Khóa ngoại/Ràng buộc (từ Service)
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("message", e.getMessage()));
        } catch (Exception e) {
            // Lỗi 500 Internal Server Error
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", "Lỗi máy chủ: " + e.getMessage()));
        }
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