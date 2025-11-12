//package com.example.library.controller;
//
//import com.example.library.dto.request.LoginRequest;
//import com.example.library.dto.request.RegisterRequest;
//import com.example.library.dto.response.AuthResponse;
//import com.example.library.service.AuthService;
//import jakarta.validation.Valid;
//import lombok.RequiredArgsConstructor;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//@RestController
//@RequestMapping("/api/auth")
//@RequiredArgsConstructor
//public class AuthController {
//    private final AuthService authService;
//
//    @PostMapping("/register")
//    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest req,
//                                      @RequestParam(defaultValue = "false") boolean admin) {
//        authService.register(req, admin);
//        return ResponseEntity.ok().build();
//    }
//
//    @PostMapping("/login")
//    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest req) {
//        return ResponseEntity.ok(authService.login(req));
//    }
//}
