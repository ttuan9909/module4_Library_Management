package com.example.library.controller;

import com.example.library.dto.request.UserAccountDTO;
import com.example.library.service.UserAccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin/users/api")
@RequiredArgsConstructor
public class UserApiController {

    private final UserAccountService userService;

    // GET: Lấy danh sách tất cả thành viên → JSON
    @GetMapping
    public List<UserAccountDTO> getAllUsers() {
        return userService.getAllUsers();
    }

    // POST: Tạo thành viên mới
    @PostMapping
    public ResponseEntity<?> createUser(@RequestBody UserAccountDTO dto) {
        try {
            UserAccountDTO saved = userService.createUser(dto);
            return ResponseEntity.ok(saved);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", e.getMessage()));
        }
    }

    // GET: Lấy 1 user theo ID (dùng cho modal sửa)
    @GetMapping("/{id}")
    public ResponseEntity<UserAccountDTO> getUserById(@PathVariable("id") Long userId) {
        try {
            UserAccountDTO userDto = userService.getUserById(userId);
            return ResponseEntity.ok(userDto);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // PUT: Cập nhật user
    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(@PathVariable("id") Long userId,
                                        @RequestBody UserAccountDTO dto) {
        try {
            UserAccountDTO updated = userService.updateUser(userId, dto);
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", "Cập nhật thất bại: " + e.getMessage()));
        }
    }

    // DELETE: Xóa user
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable("id") Long userId) {
        try {
            userService.deleteUser(userId);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", e.getMessage()));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("message", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Lỗi máy chủ: " + e.getMessage()));
        }
    }
}