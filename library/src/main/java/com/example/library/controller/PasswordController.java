package com.example.library.controller;

import com.example.library.dto.request.PasswordDoResetRequest;
import com.example.library.dto.request.PasswordResetRequest;
import com.example.library.service.PasswordService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/password")
@RequiredArgsConstructor
public class PasswordController {
    private final PasswordService passwordService;

    @PostMapping("/request-reset")
    public ResponseEntity<String> request(@Valid @RequestBody PasswordResetRequest req) {
        String token = passwordService.requestReset(req.usernameOrEmail());
        return ResponseEntity.ok(token);
    }

    @PostMapping("/reset")
    public ResponseEntity<?> reset(@Valid @RequestBody PasswordDoResetRequest req) {
        passwordService.reset(req.token(), req.newPassword());
        return ResponseEntity.ok().build();
    }
}
