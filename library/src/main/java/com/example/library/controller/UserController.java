package com.example.library.controller;

import com.example.library.dto.request.ChangePasswordRequest;
import com.example.library.dto.request.ProfileUpdateRequest;
import com.example.library.entity.UserAccount;
import com.example.library.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("/me")
    public ResponseEntity<UserAccount> me(@AuthenticationPrincipal User principal) {
        return ResponseEntity.ok(userService.me(principal.getUsername()));
    }

    @PutMapping("/me")
    public ResponseEntity<UserAccount> update(@AuthenticationPrincipal User principal,
                                              @Valid @RequestBody ProfileUpdateRequest req) {
        return ResponseEntity.ok(userService.updateProfile(principal.getUsername(), req));
    }

    @PostMapping("/me/change-password")
    public ResponseEntity<?> changePassword(@AuthenticationPrincipal User principal,
                                            @Valid @RequestBody ChangePasswordRequest req) {
        userService.changePassword(principal.getUsername(), req);
        return ResponseEntity.ok().build();
    }
}
