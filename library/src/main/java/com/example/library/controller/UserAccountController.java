package com.example.library.controller;

import com.example.library.dto.response.UserAccountdto;
import com.example.library.entity.UserAccount;
import com.example.library.service.IUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/user")
public class UserAccountController {
    @Autowired
    IUserService userService;

    @GetMapping("/find-user")
    @ResponseBody
    public ResponseEntity<?> findUser(@RequestParam("search") String search) {
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

        if (user != null) {
            return ResponseEntity.ok(user);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }
    }

}
