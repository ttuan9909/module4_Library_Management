package com.example.library.controller;

import com.example.library.dto.request.ReturnBookRequest;
import com.example.library.service.IReturnService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/return")
@RequiredArgsConstructor
public class ReturnController {

    @Autowired
    IReturnService returnService;

    @PostMapping
    public ResponseEntity<?> returnBooks(@Valid @RequestBody ReturnBookRequest request) {
        returnService.returnBooks(request);

        return ResponseEntity.ok(Map.of(
                "status", "success",
                "message", "Books returned successfully"
        ));
    }
}
