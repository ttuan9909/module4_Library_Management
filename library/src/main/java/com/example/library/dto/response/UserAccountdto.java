package com.example.library.dto.response;

import com.example.library.entity.enums.UserStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserAccountdto {
    private Long id;
    private String fullName;
    private String email;
    private String phoneNumber;
    private String address;
    private String avatarUrl;
    private UserStatus status;
}
