package com.example.library.controller;

import com.example.library.dto.request.ChangePasswordRequest;
import com.example.library.dto.request.ProfileUpdateRequest;
import com.example.library.dto.request.UserAccountDTO;
import com.example.library.dto.response.UserAccountdto;
import com.example.library.entity.UserAccount;
import com.example.library.service.IUserAccountService;
import com.example.library.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @Autowired
    IUserAccountService userAccountService;

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

    @GetMapping("/{search}")
    public ResponseEntity<?> findUser(@PathVariable String search) {
        Long userId = null;
        String phoneNumber = null;
        String email = null;

        if (search.matches("\\d+")) {
            if (search.length() >= 9) phoneNumber = search;
            else userId = Long.valueOf(search);
        } else if (search.contains("@")) {
            email = search;
        }

        UserAccount user = userService.findByUserIdOrPhoneNumberOrEmail(userId, phoneNumber, email);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "User not found"));
        }

        UserAccountdto dto = new UserAccountdto(
                user.getUserId(),
                user.getFullName(),
                user.getEmail(),
                user.getPhoneNumber(),
                user.getAddress(),
                user.getAvatarUrl(),
                user.getStatus()
        );
        return ResponseEntity.ok(dto);
    }

    @PostMapping
    public ResponseEntity<?> createUser(@RequestBody @Valid UserAccountDTO dto) {
        return ResponseEntity.ok(userAccountService.createUser(dto));
    }
}
